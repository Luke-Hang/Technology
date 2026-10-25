package com.myspringboot.kafka.controller;

import com.myspringboot.kafka.service.CmdbkafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
    @Autowired
    private CmdbkafkaService service;

    public static class DeleteHostRequest {
        @NotBlank @Size(max = 128)
        public String hostId;
    }

    /** Only sends a command; a successful response does not mean the host was deleted. */
    @PostMapping("/hosts/delete")
    public CompletableFuture<Map<String, Object>> deleteHost(@Valid @RequestBody DeleteHostRequest request) {
        return service.sendDeleteHostMessage(request.hostId).thenApply(metadata -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("code", 200);
            response.put("message", "删除主机消息发送成功");
            return response;
        });
    }
}
