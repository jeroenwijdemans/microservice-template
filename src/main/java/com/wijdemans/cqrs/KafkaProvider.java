package com.wijdemans.cqrs;

import com.wijdemans.standard.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

@Provider
public class KafkaProvider {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProvider.class);

    public final String cqrsTopic;

    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    @Inject
    public KafkaProvider(@Named("CQRS_TOPIC") String cqrsTopic) {
        this.cqrsTopic = cqrsTopic;
        logger.debug("Starting provider");
    }

    @PostConstruct
    public void init() {
        logger.info("Start kafka consumer ...");

        Properties properties = Config.getProperties("consumer.props");
        // http://stackoverflow.com/questions/28561147/how-to-read-data-using-kafka-consumer-api-from-beginning
        // making sure the goupId is reset and thus can read from earliest
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer(properties);

        producer = new KafkaProducer(Config.getProperties("producer.props"));

        consumer.subscribe(Arrays.asList(cqrsTopic));
    }

    @PreDestroy
    public void destory() {
        consumer.close();
    }

    public KafkaConsumer<String, String> getConsumer() {
        return consumer;
    }

    public KafkaProducer<String, String> getProducer() {
        return producer;
    }
}
