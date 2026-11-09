package io.github.saratrajan.samplems.client;

import io.github.saratrajan.samplems.client.config.FeignClientsConfig;
import io.github.saratrajan.samplems.client.dto.CustomerProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer", url = "${clients.customer.url}", configuration = FeignClientsConfig.class)
public interface CustomerClient {

    @GetMapping(path = ClientPaths.CUSTOMER)
    CustomerProfile getCustomer(@PathVariable("customerId") String customerId);
}
