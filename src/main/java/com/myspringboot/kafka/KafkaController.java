package com.myspringboot.kafka;

import com.myspringboot.kafka.service.CmdbkafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author xiehang
 * @date 2024/6/18 18:18
 */
@RestController("/kafka")
public class KafkaController {

    @Autowired
    private CmdbkafkaService cmdbkafkaService;

    @RequestMapping("/sendMessage")
    public void sendMessage(String topic, String message) {
        cmdbkafkaService.sendMessage("topic", String.valueOf(UUID.randomUUID()), "data");
    }
}
