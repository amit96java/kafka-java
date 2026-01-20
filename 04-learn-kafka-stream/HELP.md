# Understanding for beginner

## run kafka container
cd C:\Guru\Aprojects\Kafka\udemy\kafka-java\01-workspace\01-kafka-setup\compose
docker-compose up 

## create topic

if producer produce clear:state message than consumer will clear its all state, logic in MatchingProcessor.java

# commands

## kafka bash

docker exec -it kafka bash

## create topics

### 1st topic: orders-topic
kafka-topics.sh --create \
--topic orders-topic \
--bootstrap-server localhost:9092

/usr/bin/kafka-topics --create \
--topic orders-topic \
--bootstrap-server localhost:9092

### 2nd topic: order-count-topic

kafka-topics.sh --create \
--topic order-count-topic \
--bootstrap-server localhost:9092

/usr/bin/kafka-topics --create \
--topic order-count-topic \
--bootstrap-server localhost:9092

## producer

kafka-console-producer.sh \
--topic orders-topic \
--bootstrap-server localhost:9092 \
--property parse.key=true \
--property key.separator=:


## consumer

kafka-console-consumer.sh \
--topic order-count-topic \
--bootstrap-server localhost:9092 \
--from-beginning \
--property print.key=true

/usr/bin/kafka-console-consumer \
--topic order-count-topic \
--bootstrap-server localhost:9092 \
--property print.key=true

## message example

user1:order1
user2:order1
user1:order2
user1:order3

## delete topic

kafka-topics.sh --delete \
--topic orders-topic \
--bootstrap-server localhost:9092

kafka-topics.sh --delete \
--topic order-count-topic \
--bootstrap-server localhost:9092
