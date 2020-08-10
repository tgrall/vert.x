/*
 * Copyright (c) 2011-2019 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.core.http;

import io.vertx.codegen.annotations.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;

import java.util.List;

/**
 * Represents a client-side HTTP response.
 * <p>
 * Vert.x provides you with one of these via the handler that was provided when creating the {@link io.vertx.core.http.HttpClientRequest}
 * or that was set on the {@link io.vertx.core.http.HttpClientRequest} instance.
 * <p>
 * It implements {@link io.vertx.core.streams.ReadStream} so it can be used with
 * {@link io.vertx.core.streams.Pump} to pump data with flow control.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen
public interface HttpClientResponse extends ReadStream<Buffer> {

  @Override
  HttpClientResponse fetch(long amount);

  @Override
  HttpClientResponse resume();

  @Override
  HttpClientResponse exceptionHandler(Handler<Throwable> handler);

  @Override
  HttpClientResponse handler(Handler<Buffer> handler);

  @Override
  HttpClientResponse pause();

  @Override
  HttpClientResponse endHandler(Handler<Void> endHandler);

  /**
   * @return the version of the response
   */
  HttpVersion version();

  /**
   * @return the status code of the response
   */
  int statusCode();

  /**
   * @return the status message of the response
   */
  String statusMessage();

  /**
   * @return the headers
   */
  @CacheReturn
  MultiMap headers();

  /**
   * Return the first header value with the specified name
   *
   * @param headerName  the header name
   * @return the header value
   */
  @Nullable String getHeader(String headerName);

  /**
   * Return the first header value with the specified name
   *
   * @param headerName  the header name
   * @return the header value
   */
  @GenIgnore(GenIgnore.PERMITTED_TYPE)
  String getHeader(CharSequence headerName);

  /**
   * Return the first trailer value with the specified name
   *
   * @param trailerName  the trailer name
   * @return the trailer value
   */
  @Nullable String getTrailer(String trailerName);

  /**
   * @return the trailers
   */
  @CacheReturn
  MultiMap trailers();

  /**
   * @return the Set-Cookie headers (including trailers)
   */
  @CacheReturn
  List<String> cookies();

  /**
   * Convenience method for receiving the entire request body in one piece.
   * <p>
   * This saves you having to manually set a dataHandler and an endHandler and append the chunks of the body until
   * the whole body received. Don't use this if your request body is large - you could potentially run out of RAM.
   *
   * @param bodyHandler This handler will be called after all the body has been received
   */
  @Fluent
  default HttpClientResponse bodyHandler(Handler<Buffer> bodyHandler) {
    body().onSuccess(bodyHandler);
    return this;
  }

  /**
   * Same as {@link #body()} but with an {@code handler} called when the operation completes
   */
  @Fluent
  default HttpClientResponse body(Handler<AsyncResult<Buffer>> handler) {
    Future<Buffer> fut = body();
    fut.onComplete(handler);
    return this;
  }

  /**
   * Convenience method for receiving the entire request body in one piece.
   * <p>
   * This saves you having to manually set a dataHandler and an endHandler and append the chunks of the body until
   * the whole body received. Don't use this if your request body is large - you could potentially run out of RAM.
   *
   * @return a future completed with the body result
   */
  Future<Buffer> body();

  /**
   * Same as {@link #end()} but with an {@code handler} called when the operation completes
   */
  default void end(Handler<AsyncResult<Void>> handler) {
    end().onComplete(handler);
  }

  /**
   * Returns a future signaling when the response has been fully received successfully or failed.
   *
   * @return a future completed with the body result
   */
  Future<Void> end();

  /**
   * Set an custom frame handler. The handler will get notified when the http stream receives an custom HTTP/2
   * frame. HTTP/2 permits extension of the protocol.
   *
   * @return a reference to this, so the API can be used fluently
   */
  @Fluent
  HttpClientResponse customFrameHandler(Handler<HttpFrame> handler);

  /**
   * @return the corresponding request
   */
  @CacheReturn
  HttpClientRequest request();

  /**
   * Set an handler for stream priority changes.
   * <p/>
   * This is not implemented for HTTP/1.x.
   *
   * @param handler the handler to be called when the stream priority changes
   */
  @Fluent
  HttpClientResponse streamPriorityHandler(Handler<StreamPriority> handler);
}
