package com.myspringboot.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;


/**
 * @author xiehang
 * @date 2023/10/9 22:25
 */
@Service
public class CmdbkafkaService {

    private static final Logger logger = LoggerFactory.getLogger(CmdbkafkaService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息
     * @param topic
     * @param key
     * Kafka如何保证消息的消费顺序？ 发送消息的时候指定 key/Partition
     * Kafka 如何保证消息不丢失？
     *  1.生产者丢失消息 添加回调函数future.addCallback()检查消息是否发送成功，如果发送失败，则重试
     *  2.消费者丢失消息 消费者ACK机制，消费者在成功处理一条消息后，应向Kafka发送ACK确认，Kafka接收到ACK后才会删除该消息。
     *      如果未收到ACK，Kafka可以在消费者下次请求时重新发送消息，这样即使消费者端出现问题，消息也不会丢失。
     *  3.Kafka丢失消息
     *  副本机制：
     *      Kafka 为分区（Partition）引入了多副本（Replica）机制，分区（Partition）中有一个 leader 副本和其他follower副本，
     *      我们发送的消息会被发送到 leader 副本，然后 follower 副本才能从 leader 副本中拉取消息进行同步
     *      当一个partition的leader节点发生故障时，其他follower节点可以迅速接管成为新的leader，从而确保服务的连续性。
     * Kafka 如何保证消息不重复消费？
     *   １.唯一消息标识，发送消息时将key变为UUID，保证key的唯一性，
     *      消费者在处理消息前检查该标识符，如果发现已经处理过，则忽略该消息，避免重复处理。
     *   ２.消费消息服务做幂等校验，比如 Redis 的 set、MySQL 的主键等天然的幂等功能。这种方法最有效
     * Kafka 重试机制
     *  重试失败后的数据如何再次处理?
     *  当达到最大重试次数后，数据会直接被跳过，继续向后进行。当代码修复后，如何重新消费这些重试失败的数据呢？
     *  死信队列：当消息进入队列后，消费者会尝试处理它。如果处理失败，或者超过一定的重试次数仍无法被成功处理，
     *          消息可以发送到死信队列中，而不是被永久性地丢弃
     *
     * @param data
     */
    public void sendMessage(String topic, String key, String data) {
        final ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, data);

        if (future != null) {
            future.addCallback(result -> logger.info("生产者成功发送消息到topic:{} partition:{}的消息",
                            result.getRecordMetadata().topic(), result.getRecordMetadata().partition()),
                    ex -> logger.error("生产者发送消失败，原因：{}", ex.getMessage()));
        }
    }
}
