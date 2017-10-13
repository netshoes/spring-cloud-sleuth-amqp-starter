package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
@EnableAutoConfiguration
public class AutoConfigurationTest {

  @Autowired private AmqpMessagingSpanInjector amqpMessagingSpanInjector;

  @Autowired private AmqpMessagingSpanExtractor amqpMessagingSpanExtractor;

  @Autowired private RabbitListenerAspect rabbitListenerAspect;

  @Autowired private AmqpTemplateAspect amqpTemplateAspect;

  @Autowired private AmqpMessagingSpanManager amqpMessagingSpanManager;

  @Test
  public void test() {
    Assertions.assertThat(amqpMessagingSpanInjector).isNotNull();
    Assertions.assertThat(amqpMessagingSpanExtractor).isNotNull();
    Assertions.assertThat(rabbitListenerAspect).isNotNull();
    Assertions.assertThat(amqpTemplateAspect).isNotNull();
    Assertions.assertThat(amqpMessagingSpanManager).isNotNull();
  }
}
