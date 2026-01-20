package com.amit.learn_kafka_stream.Serde;

import com.amit.learn_kafka_stream.dto.UserOrderDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.List;
import java.util.Map;

public class UserOrderDtoListSerde implements Serde<List<UserOrderDto>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Serializer<List<UserOrderDto>> serializer() {
        return new Serializer<List<UserOrderDto>>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {}

            @Override
            public byte[] serialize(String topic, List<UserOrderDto> data) {
                try {
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() {}
        };
    }

    @Override
    public Deserializer<List<UserOrderDto>> deserializer() {
        return new Deserializer<List<UserOrderDto>>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {}

            @Override
            public List<UserOrderDto> deserialize(String topic, byte[] data) {
                try {
                    if (data == null || data.length == 0) return null;
                    return objectMapper.readValue(data, new TypeReference<List<UserOrderDto>>() {});
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() {}
        };
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
