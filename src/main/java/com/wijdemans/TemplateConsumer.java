package com.wijdemans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wijdemans.cqrs.Action;
import com.wijdemans.cqrs.KafkaPostService;
import com.wijdemans.cqrs.KafkaProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.glassfish.hk2.api.Immediate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Immediate
@Singleton
public class TemplateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TemplateConsumer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static KafkaConsumer consumer;
    private final String cqrsTopic;
    private final KafkaProvider kafkaProvider;
    private final KafkaPostService kafkaPostService;
    private final TemplateService templateService;

    @Inject
    public TemplateConsumer(
            KafkaProvider kafkaProvider,
            KafkaPostService kafkaPostService,
            TemplateService templateService,
            @Named("CQRS_TOPIC") String cqrsTopic) {
        this.kafkaProvider = kafkaProvider;
        this.kafkaPostService = kafkaPostService;
        this.templateService = templateService;
        this.consumer = kafkaProvider.getConsumer();
        this.cqrsTopic = cqrsTopic;
    }

    @PostConstruct
    public void start() {

        logger.debug("Registering actions");
        kafkaPostService.register(new Action("addSection"));
        kafkaPostService.register(new Action("updateSection"));

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
                if (record.topic().equals(cqrsTopic)) {
                    handleTopicMessage(record);
                } else {
                    logger.warn("Shouldn't be possible to get message on topic " + record.topic());
                }
            }
        }
    }

    private void handleTopicMessage(ConsumerRecord<String, String> record) {
        logger.trace("Got record {}", record.key());
        // handle all actions
        switch (record.key()) {
            case "addSection": {
                try {
                    JSONObject type = new JSONObject(record.value());
                    logger.debug("got ..." + type);
                } catch (RuntimeException e) {
                    logger.debug("Error parsing add-type message", e);
                }
                break;
            }
            case "updateSection": {
                try {
                    JSONObject type = new JSONObject(record.value());
                    logger.debug("got something else..." + type);
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
}
