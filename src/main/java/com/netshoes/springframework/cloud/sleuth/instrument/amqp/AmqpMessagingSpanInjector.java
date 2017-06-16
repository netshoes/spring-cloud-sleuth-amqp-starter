package com.netshoes.springframework.cloud.sleuth.instrument.amqp;

import com.netshoes.springframework.cloud.sleuth.instrument.amqp.support.AmqpMessageHeaderAccessor;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanInjector;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;
import org.springframework.util.StringUtils;

/**
 * Class responsible for injecting a {@link Span} in AMQP message.
 *
 * @author André Ignacio
 */
public class AmqpMessagingSpanInjector implements SpanInjector {

  private static final String SPAN_HEADER = "currentSpan";
  private final TraceKeys traceKeys;

  /**
   * Creates a new instance.
   *
   * @param traceKeys Trace`s keys of Spring Cloud Sleuth
   */
  public AmqpMessagingSpanInjector(TraceKeys traceKeys) {
    this.traceKeys = traceKeys;
  }

  @Override
  public void inject(Span span, Object carrier) {
    final Message message = (Message) carrier;

    final AmqpMessageHeaderAccessor accessor = AmqpMessageHeaderAccessor.getAccessor(message);
    if (span == null) {
      if (!isSampled(message, Span.SAMPLED_NAME)
          || !isSampled(message, TraceMessageHeaders.SAMPLED_NAME)) {
        accessor.setHeader(TraceMessageHeaders.SAMPLED_NAME, Span.SPAN_NOT_SAMPLED);
        return;
      }
      return;
    }
    addHeaders(span, message, accessor);
  }

  private boolean isSampled(Message message, String sampledHeaderName) {
    return Span.SPAN_SAMPLED.equals(
        message.getMessageProperties().getHeaders().get(sampledHeaderName));
  }

  private void addHeaders(Span span, Message initialMessage, AmqpMessageHeaderAccessor accessor) {
    addHeaders(
        span,
        initialMessage,
        accessor,
        TraceMessageHeaders.TRACE_ID_NAME,
        TraceMessageHeaders.SPAN_ID_NAME,
        TraceMessageHeaders.PARENT_ID_NAME,
        TraceMessageHeaders.SPAN_NAME_NAME,
        TraceMessageHeaders.PROCESS_ID_NAME,
        TraceMessageHeaders.SAMPLED_NAME,
        SPAN_HEADER);
  }

  private void addHeaders(
      Span span,
      Message initialMessage,
      AmqpMessageHeaderAccessor accessor,
      String traceIdHeader,
      String spanIdHeader,
      String parentIdHeader,
      String spanNameHeader,
      String processIdHeader,
      String spanSampledHeader,
      String spanHeader) {
    addHeader(traceIdHeader, span.traceIdString(), accessor);
    addHeader(spanIdHeader, Span.idToHex(span.getSpanId()), accessor);
    if (span.isExportable()) {
      addAnnotations(this.traceKeys, initialMessage, span);
      final Long parentId = getFirst(span.getParents());
      if (parentId != null) {
        addHeader(parentIdHeader, Span.idToHex(parentId), accessor);
      }
      addHeader(spanNameHeader, span.getName(), accessor);
      addHeader(processIdHeader, span.getProcessId(), accessor);
      addHeader(spanSampledHeader, Span.SPAN_SAMPLED, accessor);
    } else {
      addHeader(spanSampledHeader, Span.SPAN_NOT_SAMPLED, accessor);
    }
    accessor.setHeader(spanHeader, span);
  }

  private void addAnnotations(TraceKeys traceKeys, Message message, Span span) {
    for (String name : traceKeys.getMessage().getHeaders()) {
      final MessageProperties messageProperties = message.getMessageProperties();
      final Map<String, Object> headers = messageProperties.getHeaders();
      if (headers.containsKey(name)) {
        String key = traceKeys.getMessage().getPrefix() + name.toLowerCase();
        Object value = headers.get(name);
        if (value == null) {
          value = "null";
        }
        tagIfEntryMissing(span, key, value.toString());
      }
    }
    addPayloadAnnotations(traceKeys, message.getBody(), span);
  }

  private void addPayloadAnnotations(TraceKeys traceKeys, Object payload, Span span) {
    if (payload != null) {
      tagIfEntryMissing(
          span,
          traceKeys.getMessage().getPayload().getType(),
          payload.getClass().getCanonicalName());
      if (payload instanceof String) {
        tagIfEntryMissing(
            span,
            traceKeys.getMessage().getPayload().getSize(),
            String.valueOf(((String) payload).length()));
      } else if (payload instanceof byte[]) {
        tagIfEntryMissing(
            span,
            traceKeys.getMessage().getPayload().getSize(),
            String.valueOf(((byte[]) payload).length));
      }
    }
  }

  private void tagIfEntryMissing(Span span, String key, String value) {
    if (!span.tags().containsKey(key)) {
      span.tag(key, value);
    }
  }

  private void addHeader(String name, String value, AmqpMessageHeaderAccessor accessor) {
    if (StringUtils.hasText(value)) {
      accessor.setHeader(name, value);
    }
  }

  private Long getFirst(List<Long> parents) {
    return parents.isEmpty() ? null : parents.get(0);
  }
}
