package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import java.util.Random;
import org.springframework.amqp.core.Message;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AutoConfiguration containing span extractor and injector for AMQP messaging.
 *
 * @author André Ignacio
 */
@Configuration
@AutoConfigureAfter(TraceAutoConfiguration.class)
@ConditionalOnClass(Message.class)
@ConditionalOnBean(Tracer.class)
public class TraceSpanAmqpMessagingAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(AmqpMessagingSpanInjector.class)
  public AmqpMessagingSpanInjector amqpMessagingSpanInjector(TraceKeys traceKeys) {
    return new AmqpMessagingSpanInjector(traceKeys);
  }

  @Bean
  @ConditionalOnMissingBean(AmqpMessagingSpanExtractor.class)
  public AmqpMessagingSpanExtractor amqpMessagingSpanExtrator(Random random) {
    return new AmqpMessagingSpanExtractor(random);
  }
}
