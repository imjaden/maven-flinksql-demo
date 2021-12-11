package org.flinksql;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class LocalKafkaProducer {
    static final DateTimeFormatter SQL_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .toFormatter();
    static final DateTimeFormatter SQL_TIMESTAMP_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(SQL_TIME_FORMAT)
            .toFormatter();

    public static void main( String[] args ) {
        String topicName = "source001";
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = null;
        try {
            producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(properties);
            for (int i = 0; i < 50000; i++) {
                int num = (int) (Math.random() * 10)+1;
                String msg = "{\"userid\": " + (num%5) + ", \"value\": " + num + ", \"timestamp\": \"" + timestamp3() + "\"}";
                producer.send(new ProducerRecord<String, String>(topicName, msg));
                System.out.println("Sent: " + topicName + ", Message: " + msg);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            producer.close();
        }
    }
    public static String timestamp3() {
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        long ts = new Date().getTime();
        Instant instant = Instant.ofEpochMilli(ts + tz.getOffset(ts));
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Z"));
        return SQL_TIMESTAMP_FORMAT.format(dateTime);
    }
}
