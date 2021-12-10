package org.flinksql;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.Arrays;
import java.util.Properties;

public class LocalKafkaConsume {
    public static void main( String[] args ) {
        String[] topicName = {"source001", "sink001"};
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("group.id", "flinksql_demo");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);//100是超时时间
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("Topic: %s, offset: %d, value: %s\n", record.topic(), record.offset(), record.value());
            }
        }
    }
}
