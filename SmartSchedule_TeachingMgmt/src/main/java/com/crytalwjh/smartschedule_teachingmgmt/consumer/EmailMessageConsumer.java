package com.crytalwjh.smartschedule_teachingmgmt.consumer;

import com.crytalwjh.smartschedule_teachingmgmt.config.RabbitMQConfig;
import com.crytalwjh.smartschedule_teachingmgmt.entities.EmailMessage;
import com.crytalwjh.smartschedule_teachingmgmt.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
@Component
@Slf4j
public class EmailMessageConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(bindings =@QueueBinding(value =
    @Queue(RabbitMQConfig.EMAIL_QUEUE),exchange =
    @Exchange(value = RabbitMQConfig.EMAIL_EXCHANGE,type = "direct"),
            key = RabbitMQConfig.EMAIL_ROUTING_KEY))
    public void handleEmailMessage(Message message) {
        byte[] body = message.getBody();
        String emailStr = new String(body);
        EmailMessage emailMessage= JSON.parseObject(emailStr,EmailMessage.class);
        log.info("收到邮件消息：{}", emailMessage);
        try {
            emailService.sendEmail(
                    emailMessage.getTo(),
                    emailMessage.getSubject(),
                    emailMessage.getContent()
            );
        } catch (Exception e) {
            log.error("邮件处理失败：{}", e.getMessage());
            throw new AmqpRejectAndDontRequeueException("邮件处理失败", e);
        }
    }
}