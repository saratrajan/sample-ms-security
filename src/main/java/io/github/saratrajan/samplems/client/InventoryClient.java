package io.github.saratrajan.samplems.client;

import io.github.saratrajan.samplems.client.config.FeignClientsConfig;
import io.github.saratrajan.samplems.client.dto.HoldRequest;
import io.github.saratrajan.samplems.client.dto.HoldResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory", url = "${clients.inventory.url}", configuration = FeignClientsConfig.class)
public interface InventoryClient {

    @PostMapping(path = ClientPaths.INVENTORY_HOLDS, consumes = MediaType.APPLICATION_JSON_VALUE)
    HoldResult reserve(@RequestBody HoldRequest request);

    @DeleteMapping(path = ClientPaths.INVENTORY_HOLD)
    void release(@PathVariable("holdId") String holdId);
}
