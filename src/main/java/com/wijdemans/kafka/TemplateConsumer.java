package com.wijdemans.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wijdemans.TemplateService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wijdemans.kafka.KafkaProvider.TOPIC;

@Singleton
public class TemplateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TemplateConsumer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static KafkaConsumer consumer;
    private final KafkaProvider kafkaProvider;
    private final TemplateService templateService;

    @Inject
    public TemplateConsumer(KafkaProvider kafkaProvider, TemplateService templateService) {
        this.kafkaProvider = kafkaProvider;
        this.templateService = templateService;
        this.consumer = kafkaProvider.getConsumer();
    }

    @PostConstruct
    public void start() {
        logger.info("Start the consumer ...");
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(this::poll);
    }


    public void poll() {
        int pollEachTime = 500;
        logger.info("Start poll loop each [{}] ms.", pollEachTime);
        int timeouts = 0;
        //noinspection InfiniteLoopStatement
        while (true) {
            // read records with a short timeout. If we time out, we don't really care.
            ConsumerRecords<String, String> records = consumer.poll(pollEachTime);
            if (records.count() == 0) {
                timeouts++;
            } else {
                logger.debug("Got [{}] records after [{}] timeouts\n", records.count(), timeouts);
                timeouts = 0;
            }
            for (ConsumerRecord<String, String> record : records) {
                switch (record.topic()) {
                    case TOPIC:
                        handleTopicMessage(record);
                        break;
                    default:
                        logger.warn("Shouldn't be possible to get message on topic " + record.topic());
                }
            }
        }
    }

    private void handleTopicMessage(ConsumerRecord<String, String> record) {
        logger.trace("Got record {}", record.key());
        switch (record.key()) {
            case "add-typology-v1": {
                try {
                    JSONObject type = new JSONObject(record.value());
                    upsertV1(type);
                } catch (RuntimeException e) {
                    logger.debug("Error parsing add-type message", e);
                }
                break;
            }
            case "update-typology-v1": {
                try {
                    JSONObject type = new JSONObject(record.value());
                    upsertV1(type);

                } catch (RuntimeException e) {
                    logger.debug("Error parsing update-type message", e);
                }
                break;
            }
            default:
                logger.debug("Unknown key [{}] in topic [{}]", record.key(), record.topic());
                break;
        }
    }

    private void upsertV1(JSONObject json) {

    }


}
