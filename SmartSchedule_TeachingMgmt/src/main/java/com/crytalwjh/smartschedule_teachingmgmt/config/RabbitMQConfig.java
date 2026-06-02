package com.crytalwjh.smartschedule_teachingmgmt.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String EMAIL_ROUTING_KEY = "email.routing.key";
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}