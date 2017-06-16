package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

/**
 * Interceptor responsible for extract a span from a AMQP message and join him to the current span.
 *
 * @see AmqpMessagingSpanExtractor
 * @author André Ignacio
 */
public class AmqpMessagingBeforeReceiveInterceptor implements MethodInterceptor {

  private final AmqpMessagingSpanExtractor extractor;
  private final Tracer tracer;

  /**
   * Creates a new instance.
   *
   * @param extractor AMQP span extractor
   * @param tracer Sleuth Tracer
   */
  public AmqpMessagingBeforeReceiveInterceptor(
      AmqpMessagingSpanExtractor extractor, Tracer tracer) {
    this.extractor = extractor;
    this.tracer = tracer;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    final Message message = (Message) invocation.getArguments()[1];
    before(message);
    return invocation.proceed();
  }

  private void before(Message message) {
    final Span span = extractor.joinTrace(message);
    tracer.continueSpan(span);
  }
}
