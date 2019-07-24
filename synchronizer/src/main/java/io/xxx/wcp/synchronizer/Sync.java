package io.xxx.wcp.synchronizer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class Sync {

    private final AmqpTemplate amqpTemplate;

    public Sync(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void handle() {
        for (String openid : getOpenids()) {
            amqpTemplate.convertAndSend("wechat-user", openid);
        }
    }

    private List<String> getOpenids() {
        return Collections.emptyList();
    }
}
