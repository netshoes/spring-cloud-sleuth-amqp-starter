package com.netshoes.springframework.cloud.sleuth.instrument.amqp.support;

import java.util.Map;
import org.springframework.amqp.core.Message;
import org.springframework.cloud.sleuth.Span;
import org.springframework.util.ObjectUtils;

/**
 * Utility class for access headers from AMQP message. Do not register this class as a bean. This
 * class is not thread safe.
 *
 * @author André Ignacio
 */
public class AmqpMessageHeaderAccessor {
  private final Map<String, Object> headers;
  private boolean modified = false;

  /**
   * Creates a new instance. This is a private constructor, use {@link #getAccessor(Message)} to get
   * a instance.
   *
   * @param message Message
   */
  private AmqpMessageHeaderAccessor(Message message) {
    this.headers = message.getMessageProperties().getHeaders();
  }

  /**
   * Get instance from this class.
   *
   * @param message AMQP Message
   * @return A instance of this class.
   */
  public static AmqpMessageHeaderAccessor getAccessor(Message message) {
    return new AmqpMessageHeaderAccessor(message);
  }

  /**
   * Set a header.
   *
   * @param name Name
   * @param value Value
   */
  public void setHeader(String name, Object value) {
    verifyType(name, value);
    if (value != null) {
      // Modify header if necessary
      if (!ObjectUtils.nullSafeEquals(value, getHeader(name))) {
        this.modified = true;
        this.headers.put(name, value);
      }
    } else {
      // Remove header if available
      if (this.headers.containsKey(name)) {
        this.modified = true;
        this.headers.remove(name);
      }
    }
  }

  /**
   * Get a header value.
   *
   * @param name Name of header
   * @return Value of header
   */
  public Object getHeader(String name) {
    return headers.get(name);
  }

  /**
   * Get a header value casting to expected type.
   *
   * @param name Name of header
   * @param type Expected type of header's value
   * @param <T> Expected type of header's value
   * @return Value of header
   */
  public <T> T getHeader(String name, Class<T> type) {
    return (T) headers.get(name);
  }

  public boolean hasHeader(String headerName) {
    return headers.containsKey(headerName);
  }

  protected void verifyType(String headerName, Object headerValue) {
    if (headerName != null && headerValue != null) {
      if (!(headerValue instanceof String || headerValue instanceof Span)) {
        throw new IllegalArgumentException("'" + headerName + "' header value must be a String");
      }
    }
  }

  /**
   * Header`s value was modified or new header was added?
   *
   * @return True if header`s value was modified or new header was added?
   */
  public boolean isModified() {
    return modified;
  }
}
