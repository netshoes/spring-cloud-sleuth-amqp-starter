package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.support.AmqpMessageHeaderAccessor;
import java.util.Random;
import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Span.SpanBuilder;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;

/**
 * Class responsible for extract a {@link Span} data from AMQP message.
 *
 * @author André Ignacio
 */
public class AmqpMessagingSpanExtractor implements SpanExtractor<Message> {

  private final Random random;

  /**
   * Creates a new instance.
   *
   * @param random Random
   */
  public AmqpMessagingSpanExtractor(Random random) {
    this.random = random;
  }

  @Override
  public Span joinTrace(Message message) {
    final AmqpMessageHeaderAccessor accessor = AmqpMessageHeaderAccessor.getAccessor(message);

    if ((!hasHeader(accessor, Span.TRACE_ID_NAME) || !hasHeader(accessor, Span.SPAN_ID_NAME))
        && (!hasHeader(accessor, TraceMessageHeaders.SPAN_ID_NAME)
            || !hasHeader(accessor, TraceMessageHeaders.TRACE_ID_NAME))) {
      return null;
    }

    return extractSpanFromHeaders(
        accessor,
        Span.builder(),
        TraceMessageHeaders.TRACE_ID_NAME,
        TraceMessageHeaders.SPAN_ID_NAME,
        TraceMessageHeaders.SAMPLED_NAME,
        TraceMessageHeaders.PROCESS_ID_NAME,
        TraceMessageHeaders.SPAN_NAME_NAME,
        TraceMessageHeaders.PARENT_ID_NAME);
  }

  private Span extractSpanFromHeaders(
      AmqpMessageHeaderAccessor accessor,
      SpanBuilder spanBuilder,
      String traceIdHeader,
      String spanIdHeader,
      String spanSampledHeader,
      String spanProcessIdHeader,
      String spanNameHeader,
      String spanParentIdHeader) {

    final String traceId = getHeader(accessor, traceIdHeader);
    spanBuilder.traceIdHigh(traceId.length() == 32 ? Span.hexToId(traceId, 0) : 0);
    spanBuilder.traceId(Span.hexToId(traceId));

    long spanId =
        hasHeader(accessor, spanIdHeader)
            ? Span.hexToId(getHeader(accessor, spanIdHeader))
            : this.random.nextLong();
    spanBuilder = spanBuilder.spanId(spanId);
    spanBuilder.exportable(Span.SPAN_SAMPLED.equals(getHeader(accessor, spanSampledHeader)));
    String processId = getHeader(accessor, spanProcessIdHeader);
    String spanName = getHeader(accessor, spanNameHeader);
    if (spanName != null) {
      spanBuilder.name(spanName);
    }
    if (processId != null) {
      spanBuilder.processId(processId);
    }
    setParentIdIfApplicable(accessor, spanBuilder, spanParentIdHeader);
    spanBuilder.remote(true);
    return spanBuilder.build();
  }

  private String getHeader(AmqpMessageHeaderAccessor accessor, String name) {
    return getHeader(accessor, name, String.class);
  }

  private <T> T getHeader(AmqpMessageHeaderAccessor accessor, String name, Class<T> type) {
    return (T) accessor.getHeader(name);
  }

  private boolean hasHeader(AmqpMessageHeaderAccessor accessor, String name) {
    return accessor.hasHeader(name);
  }

  private void setParentIdIfApplicable(
      AmqpMessageHeaderAccessor accessor, SpanBuilder spanBuilder, String spanParentIdHeader) {
    String parentId = getHeader(accessor, spanParentIdHeader);
    if (parentId != null) {
      spanBuilder.parent(Span.hexToId(parentId));
    }
  }
}
