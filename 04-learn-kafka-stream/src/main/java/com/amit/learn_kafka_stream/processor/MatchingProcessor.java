package com.amit.learn_kafka_stream.processor;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchingProcessor implements Processor<String, String, String, String> {
    private static final Logger logger =
            LoggerFactory.getLogger(MatchingProcessor.class);

    private final String orderStateStoreName;
    private final String userCountStoreName;


    private ProcessorContext<String, String> context;
    private KeyValueStore<String, String> orderCountStateStore;
    private KeyValueStore<String, Long> userCountStateStore;

    public MatchingProcessor(String orderStateStoreName, String userCountStoreName) {
        this.orderStateStoreName = orderStateStoreName;
        this.userCountStoreName = userCountStoreName;
    }

    @Override
    public void init(ProcessorContext<String, String> context) {
        Processor.super.init(context);
        this.context = context;
        this.orderCountStateStore = context.getStateStore(orderStateStoreName);
        this.userCountStateStore =  context.getStateStore(userCountStoreName);
    }

    @Override
    public void process(Record<String, String> record) {
        String userName = record.key();
        String orderName = record.value();


        // 🔥 update user count (String -> Long store)
        Long currentCount = userCountStateStore.get(userName);
        if (currentCount == null) {
            currentCount = 0L;
        }
        userCountStateStore.put(userName, currentCount + 1);

        if (userName.startsWith("a")) {
            logger.info("adding in state store " + userName);
            orderCountStateStore.put(userName + "dlt", orderName);
        } else {
            logger.info("forwarding to another topic " + userName);
            context.forward(new Record<>(userName + ":forward", orderName, record.timestamp()));
        }

    }

    @Override
    public void close() {
        Processor.super.close();
    }
}
