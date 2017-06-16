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
 * AutoConfiguration that register a {@link AmqpMessagingBeforePublishPostProcessor} and a {@link
 * AmqpMessagingBeforeReceiveInterceptor}.
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
  @ConditionalOnMissingBean(AmqpMessagingBeforePublishPostProcessor.class)
  public AmqpMessagingBeforePublishPostProcessor amqpMessagingBeforePublishPostProcessor(
      Tracer tracer, AmqpMessagingSpanInjector amqpMessagingSpanInjector) {
    return new AmqpMessagingBeforePublishPostProcessor(amqpMessagingSpanInjector, tracer);
  }

  @Bean
  @ConditionalOnProperty(value = "spring.sleuth.amqp.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean(AmqpMessagingBeforeReceiveInterceptor.class)
  public AmqpMessagingBeforeReceiveInterceptor amqpMessagingBeforeReceiveInterceptor(
      AmqpMessagingSpanExtractor amqpMessagingSpanExtractor, Tracer tracer) {
    return new AmqpMessagingBeforeReceiveInterceptor(amqpMessagingSpanExtractor, tracer);
  }
}
