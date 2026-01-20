package com.amit.learn_kafka_stream.producer;

import com.amit.learn_kafka_stream.processor.MatchingProcessor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Future;

public class ProducerHelper {
    private static final Logger logger =
            LoggerFactory.getLogger(ProducerHelper.class);
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        List<String> name = List.of("amit", "aman", "sumit", "rahul", "anil", "sachin", "shiva", "vivek");
        List<String> order = List.of("order1", "order2", "order3", "order4", "order5");

        Random random = new Random();

        for (int i = 1; i <= 20; i++) {
            String randomName = name.get(random.nextInt(name.size()));
            String randomOrder = order.get(random.nextInt(order.size()));
            System.out.println("Loop " + (i + 1) + ": Name = " + randomName + ", Order = " + randomOrder);
            ProducerRecord<String, String> record = new ProducerRecord<>("orders-topic", randomName, randomOrder);

            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();

            logger.info("Message sent to topic " + metadata.topic() +
                    " partition " + metadata.partition() +
                    " offset " + metadata.offset());
        }



        producer.close();
    }
}

