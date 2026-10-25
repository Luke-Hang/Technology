package com.myspringboot.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;
// 匿名内部类写法需要的导入：
// import org.springframework.kafka.support.SendResult;
// import org.springframework.util.concurrent.ListenableFutureCallback;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class CmdbkafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    public CompletableFuture<RecordMetadata> sendDeleteHostMessage(String hostId) {
        if (hostId == null || hostId.trim().isEmpty()) throw new IllegalArgumentException("hostId is required");
        Map<String, String> message = new HashMap<>();
        message.put("operation", "DELETE_HOST");
        message.put("hostId", hostId);
        try {
            return sendMessage(topic, hostId, mapper.writeValueAsString(message));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Cannot serialize host deletion message", ex);
        }
    }

    public CompletableFuture<RecordMetadata> sendMessage(String topic, String key, String data) {
        CompletableFuture<RecordMetadata> result = new CompletableFuture<>();
        try {
            kafkaTemplate.send(topic, key, data).addCallback(
sent -> result.complete(sent.getRecordMetadata()),
                    result::completeExceptionally);

            // 等价的匿名内部类写法，仅供对照：
/*            kafkaTemplate.send(topic, key, data).addCallback(
                    new ListenableFutureCallback<SendResult<String, String>>() {
                        @Override
                        public void onSuccess(SendResult<String, String> sent) {
                            // Kafka 确认发送成功，将消息信息传给调用方。
                            result.complete(sent.getRecordMetadata());
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            // 发送失败，将异常传给调用方。
                            result.completeExceptionally(error);
                        }
                    });*/
        } catch (RuntimeException ex) { result.completeExceptionally(ex); }
        return result;
    }
}
