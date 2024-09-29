package com.myspringboot.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

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
        Map<String,Object> props=new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
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
