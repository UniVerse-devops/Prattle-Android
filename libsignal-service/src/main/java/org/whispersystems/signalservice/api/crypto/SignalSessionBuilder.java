package org.whispersystems.signalservice.api.crypto;

import org.spark.libsignal.protocol.InvalidKeyException;
import org.spark.libsignal.protocol.SessionBuilder;
import org.spark.libsignal.protocol.UntrustedIdentityException;
import org.spark.libsignal.protocol.state.PreKeyBundle;
import org.whispersystems.signalservice.api.SignalSessionLock;

/**
 * A thread-safe wrapper around {@link SessionBuilder}.
 */
public class SignalSessionBuilder {

  private final SignalSessionLock lock;
  private final SessionBuilder    builder;

  public SignalSessionBuilder(SignalSessionLock lock, SessionBuilder builder) {
    this.lock    = lock;
    this.builder = builder;
  }

  public void process(PreKeyBundle preKey) throws InvalidKeyException, UntrustedIdentityException {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      builder.process(preKey);
    }
  }
}
