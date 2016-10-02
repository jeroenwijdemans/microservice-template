package com.wijdemans.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

@Provider
public class KafkaProvider {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProvider.class);

    public static final String TOPIC = "my-template";


    private KafkaConsumer<String, String> consumer;

    public KafkaProvider() {
        logger.debug("Starting provider");
    }

    @PostConstruct
    public void init() {
        logger.info("Start kafka consumer ...");

        try (InputStream props = ClassLoader.getSystemResourceAsStream("consumer.props")) {
            Properties properties = new Properties();
            properties.load(props);
            // http://stackoverflow.com/questions/28561147/how-to-read-data-using-kafka-consumer-api-from-beginning
            // making sure the goupId is reset and thus can read from earliest
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumer = new KafkaConsumer(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        consumer.subscribe(Arrays.asList(TOPIC));
    }

    @PreDestroy
    public void destory() {
        consumer.close();
    }

    public KafkaConsumer<String, String> getConsumer() {
        return consumer;
    }
}
