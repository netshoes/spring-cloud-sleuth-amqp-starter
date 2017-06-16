package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

/**
 * Message post processor responsible for create a new {@link Span} from current {@link Span} and
 * injecting him in AMQP message before it is published.
 *
 * @see AmqpMessagingSpanInjector
 * @author André Ignacio
 */
public class AmqpMessagingBeforePublishPostProcessor implements MessagePostProcessor {

  private final AmqpMessagingSpanInjector injector;
  private final Tracer tracer;

  /**
   * Creates a new instance.
   *
   * @param injector AMQP span injector
   * @param tracer Sleuth Tracer
   */
  public AmqpMessagingBeforePublishPostProcessor(AmqpMessagingSpanInjector injector, Tracer tracer) {
    this.tracer = tracer;
    this.injector = injector;
  }

  @Override
  public Message postProcessMessage(Message message) throws AmqpException {
    final Span currentSpan = tracer.getCurrentSpan();
    final Span span = tracer.createSpan(currentSpan.getName(), currentSpan);
    injector.inject(span, message);
    return message;
  }
}
