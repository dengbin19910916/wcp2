package io.xxx.wcp.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "wechat-user")
public class UserHandler {

    private final StringRedisTemplate redisTemplate;

    public UserHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RabbitHandler
    public void handle(String openid) {
        UserInfo userInfo = getUserInfo(openid);
        redisTemplate.opsForList().leftPush("WCP:USER", openid);
//        redisTemplate.opsForList().
    }

    private UserInfo getUserInfo(String openid) {
        return new UserInfo();
    }
}
