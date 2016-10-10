package com.wijdemans.cqrs;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Checks if the command is correct.
 * <p>
 * If it is valid it will be send to the kafka log.
 */
@Provider
public class KafkaPostService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPostService.class);
    private final KafkaProvider kafkaProvider;
    private Map<String, Action> actions = new HashMap<>();

    @Inject
    public KafkaPostService(KafkaProvider kafkaProvider) {
        this.kafkaProvider = kafkaProvider;
    }

    public void publish(Command command) {
        if (!isActionRegistered(command.getAction())) {
            throw new InvalidCommandException(new Error(1, "action not known", "provide valid action from : " + actions.keySet()));
        }
        logger.debug("Pushing command to the kafka log");
        ProducerRecord record = new ProducerRecord("test", "produced-v1", command.getPayload().toString());
        kafkaProvider.getProducer().send(record);
        logger.debug("Command pushed");
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
