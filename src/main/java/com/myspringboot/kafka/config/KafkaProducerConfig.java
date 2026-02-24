package com.myspringboot.kafka.config;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiehang
 * @date 2023/10/9 22:06
 */
@Configurable
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * kafka可以认为是一个横跨
     * @return
     */
    @Bean
    protected Map<String,Object> hisProducerConfigs(){
        Map<String, Object> props = new HashMap<>();
        //props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);// 指定 Kafka 集群的地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");// Kafka集群的地址列表
        //指定键和值的序列化方式，这里使用的是 StringSerializer
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(ProducerConfig.RETRIES_CONFIG, 3);//设置重试次数，避免因网络问题导致消息丢失。
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG,1000);//消息发送失败后，重试之间的等待时间

        props.put(ProducerConfig.ACKS_CONFIG, "all");//控制消息的持久化程度，等待所有副本确认。
        return props;
    }

    /**
     * kafaka生产者配置
     * @return
     */
    @Bean
    protected ProducerFactory<String,String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(hisProducerConfigs());
    }
    @Bean
    protected KafkaTemplate<String,String> KafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }
}
