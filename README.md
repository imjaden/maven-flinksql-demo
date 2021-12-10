# FlinkSQL Maven Demo

## Deployment

```
# Step1: 
$ docker-compose up -d

# Step2:
$ run LocalKafkaProducer.java -> topic: source001

# Step3:
$ run LocalFlinkSQL.java -> group by -> topic: sink001
# edit resources/running.sql

# Step
$ run LocalKafkaConsume.java -> topic: source001, sink001

    Topic: source001, offset: 4, value: {"userid": 333, "value": 7, "timestamp": "2021-12-11 00:17:26.000"}
    Topic: sink001, offset: 4, value: {"per_minute":17,"amt":196,"cnt":35}
```