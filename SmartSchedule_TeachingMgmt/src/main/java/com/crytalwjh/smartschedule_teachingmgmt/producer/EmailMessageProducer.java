package com.crytalwjh.smartschedule_teachingmgmt.producer;

import com.crytalwjh.smartschedule_teachingmgmt.config.RabbitMQConfig;
import com.crytalwjh.smartschedule_teachingmgmt.entities.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.listener.simple.retry.max-attempts}")
    private int maxAttempts;

    public void sendEmailMessage(EmailMessage emailMessage) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    emailMessage
            );
            log.info("邮件消息发送成功：{}", emailMessage.getTo());
        } catch (Exception e) {
            log.error("邮件消息发送失败：{}", e.getMessage());
            throw new RuntimeException("邮件消息发送失败");
        }
    }
}