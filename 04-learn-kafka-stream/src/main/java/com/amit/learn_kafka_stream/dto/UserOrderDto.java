package com.amit.learn_kafka_stream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDto {
    private String userName;
    private String orderName;
}
