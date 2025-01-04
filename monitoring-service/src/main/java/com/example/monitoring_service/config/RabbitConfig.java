package com.example.monitoring_service.config;

import com.example.monitoring_service.consumer.DeviceSyncConsumer;
import com.example.monitoring_service.consumer.MeasurementConsumer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange topic(){
        return new TopicExchange("exchange");
    }

    @Bean
    @Qualifier("deviceSyncQueue")
    public Queue deviceSyncQueue(){
        return new Queue("deviceSyncQueue");
    }


    @Bean
    @Qualifier("measurementQueue")
    public Queue measurementQueue() {
        return new Queue("measurementQueue");
    }

    @Bean
    public Binding bindingDeviceSyncQueue(@Qualifier("deviceSyncQueue") Queue queue, TopicExchange topic) {
        return BindingBuilder.bind(queue).to(topic).with("routing.device.sync");
    }



    @Bean
    public Binding bindingMeasurementQueue(TopicExchange topicExchange, @Qualifier("measurementQueue") Queue measurementQueue) {
        return BindingBuilder.bind(measurementQueue)
                .to(topicExchange)
                .with("measurement.simulator");
    }

    @Bean
    public MessageListenerAdapter deviceListenerAdapter(MeasurementConsumer measurementConsumer, Jackson2JsonMessageConverter jsonMessageConverter) {
        MessageListenerAdapter messageListenerAdapter =
                new MessageListenerAdapter(measurementConsumer, "receiveMeasurement");
        messageListenerAdapter.setMessageConverter(jsonMessageConverter);
        return messageListenerAdapter;
    }

    @Bean
    public MessageListenerAdapter measurementsListenerAdapter(MeasurementConsumer measurementsListener, Jackson2JsonMessageConverter jsonMessageConverter ) {
        MessageListenerAdapter messageListenerAdapter =
                new MessageListenerAdapter(measurementsListener, "receiveMeasurement");
        messageListenerAdapter.setMessageConverter(jsonMessageConverter);
        return messageListenerAdapter;
    }

    @Bean
    public SimpleMessageListenerContainer measurementsContainer(ConnectionFactory connectionFactory, MessageListenerAdapter measurementsListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("measurementQueue");
        container.setMessageListener(measurementsListenerAdapter);
        return container;
    }



    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
