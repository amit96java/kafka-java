# commands

## kafka bash

docker exec -it kafka bash

## create topics

kafka-topics.sh --create \
--topic orders-topic \
--bootstrap-server localhost:9092

kafka-topics.sh --create \
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
