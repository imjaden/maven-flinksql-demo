package org.flinksql;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
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
    /** Formatter for SQL string representation of a time value. */
    static final DateTimeFormatter SQL_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .toFormatter();
    /** Formatter for SQL string representation of a timestamp value (without UTC timezone). */
    static final DateTimeFormatter SQL_TIMESTAMP_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(SQL_TIME_FORMAT)
            .toFormatter();

    public static void main( String[] args ) {
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        long ts = new Date().getTime();
        Instant instant = Instant.ofEpochMilli(ts + tz.getOffset(ts));
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Z"));
        String timestamp3 = SQL_TIMESTAMP_FORMAT.format(dateTime);
        System.out.println("TIMESTAMP(3): " +  timestamp3);

        String topicName = "source001";
        Properties properties = new Properties();

        properties.put("bootstrap.servers", "127.0.0.1:9092");
        /**
         * acks 三种状态:
         * acks=0 不等待服务器确认直接发送消息，无法保证服务器收到消息数据
         * acks=1 把消息记录写到本地，但不会保证所有的消息数据被确认记录的情况下进行释放
         * acks=all 确认所有的消息数据被同步副本确认，这样保证了记录不会丢失
         *
         */
        properties.put("acks", "all");
        /**
         * 设置成大于0将导致客户端重新发送任何发送失败的记录
         */
        properties.put("retries", 0);
        /**
         * 16384字节是默认设置的批处理的缓冲区
         */
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        /**
         * 序列化类型。
         * kafka是以键值对的形式发送到kafka集群的，其中key是可选的，value可以是任意类型，Message再被发送到kafka之前，Producer需要
         * 把不同类型的消息转化成二进制类型。
         */
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000");
            producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(properties);
            for (int i = 0; i < 50000; i++) {
                String timestamp = sdf.format(new Date());
                int num = (int) (Math.random() * 10)+1;
                String msg = "{\"userid\": 333, \"value\": " + num + ", \"timestamp\": \"" + timestamp + "\"}";
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
}
