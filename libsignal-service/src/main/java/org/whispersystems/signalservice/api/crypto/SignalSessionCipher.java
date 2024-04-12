package org.whispersystems.signalservice.api.crypto;

import org.spark.libsignal.protocol.DuplicateMessageException;
import org.spark.libsignal.protocol.InvalidKeyException;
import org.spark.libsignal.protocol.InvalidKeyIdException;
import org.spark.libsignal.protocol.InvalidMessageException;
import org.spark.libsignal.protocol.InvalidVersionException;
import org.spark.libsignal.protocol.LegacyMessageException;
import org.spark.libsignal.protocol.NoSessionException;
import org.spark.libsignal.protocol.SessionCipher;
import org.spark.libsignal.protocol.UntrustedIdentityException;
import org.spark.libsignal.protocol.message.CiphertextMessage;
import org.spark.libsignal.protocol.message.PreKeySignalMessage;
import org.spark.libsignal.protocol.message.SignalMessage;
import org.whispersystems.signalservice.api.SignalSessionLock;

/**
 * A thread-safe wrapper around {@link SessionCipher}.
 */
public class SignalSessionCipher {

  private final SignalSessionLock lock;
  private final SessionCipher     cipher;

  public SignalSessionCipher(SignalSessionLock lock, SessionCipher cipher) {
    this.lock   = lock;
    this.cipher = cipher;
  }

  public CiphertextMessage encrypt(byte[] paddedMessage) throws org.spark.libsignal.protocol.UntrustedIdentityException, NoSessionException {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.encrypt(paddedMessage);
    }
  }

  public byte[] decrypt(PreKeySignalMessage ciphertext) throws DuplicateMessageException, LegacyMessageException, InvalidMessageException, InvalidKeyIdException, InvalidKeyException, org.spark.libsignal.protocol.UntrustedIdentityException {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.decrypt(ciphertext);
    }
  }

  public byte[] decrypt(SignalMessage ciphertext) throws InvalidMessageException, InvalidVersionException, DuplicateMessageException, LegacyMessageException, NoSessionException, UntrustedIdentityException {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.decrypt(ciphertext);
    }
  }

  public int getRemoteRegistrationId() {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.getRemoteRegistrationId();
    }
  }

  public int getSessionVersion() {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.getSessionVersion();
    }
  }
}
