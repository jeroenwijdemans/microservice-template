package com.wijdemans.cqrs;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Checks if the command is correct.
 * <p>
 * If it is valid it will be send to the kafka log.
 */
@Provider
public class KafkaPostService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPostService.class);
    private final KafkaProvider kafkaProvider;
    private final String cqrsTopic;
    private Map<String, Action> actions = new HashMap<>();

    @Inject
    public KafkaPostService(KafkaProvider kafkaProvider, @Named("CQRS_TOPIC") String cqrsTopic) {
        this.kafkaProvider = kafkaProvider;
        this.cqrsTopic = cqrsTopic;
    }

    public String publish(Command command) {
        if (!isActionRegistered(command.getAction())) {
            throw new InvalidCommandException(new Error(1, "action not known", "provide valid action from : " + actions.keySet()));
        }
        String linkId = UUID.randomUUID().toString();

        logger.debug("Pushing command to the kafka log");
        ProducerRecord record = new ProducerRecord(cqrsTopic, command.getAction(), command.getPayload().toString());
        kafkaProvider.getProducer().send(record);
        logger.debug("...command [{}] pushed", linkId);

        return linkId;
    }

    private boolean isActionRegistered(String action) {
        return actions.keySet().contains(action);
    }

    public void register(Action action) {
        actions.put(action.getName(), action);
    }

    public Set<String> getSupportedActions() {
        return actions.keySet();
    }
}
