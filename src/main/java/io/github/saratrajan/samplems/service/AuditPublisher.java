package io.github.saratrajan.samplems.service;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/** Writes audit events as JSON lines. Direct Jackson use inside the service layer. */
@Component
public class AuditPublisher {

    private static final Logger AUDIT = LoggerFactory.getLogger("audit");

    private final JsonMapper jsonMapper;

    public AuditPublisher(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public void publish(String event, String orderId, Map<String, ?> details) {
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("event", event);
        line.put("orderId", orderId);
        line.put("at", OffsetDateTime.now().toString());
        line.putAll(details);
        AUDIT.info(jsonMapper.writeValueAsString(line));
    }
}
