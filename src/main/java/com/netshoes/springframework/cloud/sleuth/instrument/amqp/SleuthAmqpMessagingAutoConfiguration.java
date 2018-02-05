package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AutoConfiguration that register a {@link RabbitListenerAspect}, a {@link AmqpTemplateAspect} and
 * {@link AmqpMessagingSpanManager}.
 *
 * @author André Ignacio
 */
@Configuration
@AutoConfigureAfter({TraceSpanAmqpMessagingAutoConfiguration.class, TraceAutoConfiguration.class})
@ConditionalOnClass(Message.class)
@ConditionalOnBean(Tracer.class)
@ConditionalOnProperty(value = "spring.sleuth.enabled", matchIfMissing = true)
public class SleuthAmqpMessagingAutoConfiguration {

  @Bean
  @ConditionalOnProperty(value = "spring.sleuth.amqp.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean(RabbitListenerAspect.class)
  public RabbitListenerAspect rabbitListenerAspect(
      AmqpMessagingSpanManager amqpMessagingSpanManager) {
    return new RabbitListenerAspect(amqpMessagingSpanManager);
  }

  @Bean
  @ConditionalOnProperty(value = "spring.sleuth.amqp.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean(AmqpTemplateAspect.class)
  public AmqpTemplateAspect rabbitTemplateAspect(
      AmqpMessagingSpanManager amqpMessagingSpanManager) {
    return new AmqpTemplateAspect(amqpMessagingSpanManager);
  }

  @Bean
  @ConditionalOnProperty(value = "spring.sleuth.amqp.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean(RabbitHandlerAspect.class)
  public RabbitHandlerAspect rabbitHandlerAspect(
      AmqpMessagingSpanManager amqpMessagingSpanManager) {
    return new RabbitHandlerAspect(amqpMessagingSpanManager);
  }

  @Bean
  @ConditionalOnProperty(value = "spring.sleuth.amqp.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean(MessageConverterAspect.class)
  public MessageConverterAspect messageConverterAspect(
      AmqpMessagingSpanManager amqpMessagingSpanManager) {
    return new MessageConverterAspect(amqpMessagingSpanManager);
  }

  @Bean
  @ConditionalOnProperty(value = "spring.sleuth.amqp.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean(AmqpMessagingSpanManager.class)
  public AmqpMessagingSpanManager amqpMessagingSpanManager(
      AmqpMessagingSpanInjector amqpMessagingSpanInjector,
      AmqpMessagingSpanExtractor amqpMessagingSpanExtractor,
      Tracer tracer) {
    return new DefaultAmqpMessagingSpanManager(
        amqpMessagingSpanInjector, amqpMessagingSpanExtractor, tracer);
  }
}
