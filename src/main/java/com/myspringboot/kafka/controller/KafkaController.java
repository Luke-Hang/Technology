package com.myspringboot.kafka.controller;

import com.myspringboot.kafka.service.CmdbkafkaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

    @Autowired
    private CmdbkafkaService service;

    @Autowired
    private ObjectMapper mapper;

    public static class DeleteHostRequest {
        @NotBlank @Size(max = 128)
        public String hostId;
    }

    /**
     * 发送给消息
     * @param request
     * @return
     */
    @PostMapping("/hosts/delete")
    public CompletableFuture<Map<String, Object>> deleteHost(@Valid @RequestBody DeleteHostRequest request) {
        return service.sendDeleteHostMessage(request.hostId).thenApply(metadata -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("code", 200);
            response.put("message", "删除主机消息发送成功");
            return response;
        });
    }

    /**
     *  Spring 为 @KafkaListener 创建的后台线程一直重复：调用 poll() → 等待消息 → 有消息就处理 → 再调用 poll()
     *  Spring Kafka，默认 pollTimeout 是 5000 毫秒（5 秒），一次 poll() 最多等待约 5 秒
     *  有消息：poll() 可以提前返回，处理完成后继续下一轮。
     *  没消息：等待到超时时间后返回空结果，再继续下一轮。
     *
     * 总结：
     *      Spring 为 @KafkaListener 创建的后台线程，主动持续从 delete-topic 配置对应的 Topic（这里是 host-delete）拉取消息，
     *      收到后调用 KafkaController.consume() 方法处理。
     *      @KafkaListener：消费者后台持续调用 poll()，主动从 Kafka 拉取消息
     *
     *      Kafka：消费者订阅 Topic → 消费者主动拉取消息 → 消费者处理。
     */
    // 由 Kafka 自动触发，不是 HTTP 接口。
    @KafkaListener(topics = "${spring.kafka.delete-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) throws IOException {
        // 使用 record.value() 取得消息内容  {"operation":"DELETE_HOST","hostId":"vm-001"}
        // 使用 ObjectMapper mapper 解析 JSON 为 message，然后取出主机 ID hostId
//        JsonNode message = mapper.readTree(record.value());
//        String hostId = message.path("hostId").asText();
        JsonNode message = mapper.readTree(record.value());
        if (message == null || !"DELETE_HOST".equals(message.path("operation").asText())) {
            throw new IllegalArgumentException("消息操作类型必须为 DELETE_HOST");
        }

        // 从 JsonNode message 里面取出 hostId
        JsonNode hostIdNode = message.path("hostId");
        if (!hostIdNode.isTextual() || hostIdNode.asText().trim().isEmpty()) {
            throw new IllegalArgumentException("删除主机消息缺少 hostId");
        }
        String hostId = hostIdNode.asText();

        logger.info("收到删除主机消息：hostId={}, topic={}, partition={}, offset={}",
                hostId, record.topic(), record.partition(), record.offset());

        // 在这里接入实际删除主机的业务方法；当前仅记录消息。
        // 实际删除操作需要支持重复消息，避免重复执行。
        deleteHost(hostId);
    }

    /**
     *
     * @param hostId
     */
    private void deleteHost(String hostId) {
    }
}
