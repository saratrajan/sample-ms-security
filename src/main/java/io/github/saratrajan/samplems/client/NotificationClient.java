package io.github.saratrajan.samplems.client;

import io.github.saratrajan.samplems.client.config.FeignClientsConfig;
import io.github.saratrajan.samplems.client.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification", url = "${clients.notification.url}", configuration = FeignClientsConfig.class)
public interface NotificationClient {

    @PostMapping(path = ClientPaths.NOTIFICATIONS, consumes = MediaType.APPLICATION_JSON_VALUE)
    void send(@RequestBody NotificationRequest request);
}
