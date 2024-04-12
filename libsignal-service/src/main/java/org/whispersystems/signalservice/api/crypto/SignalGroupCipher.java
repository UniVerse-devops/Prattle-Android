package org.whispersystems.signalservice.api.crypto;

import org.spark.libsignal.protocol.DuplicateMessageException;
import org.spark.libsignal.protocol.InvalidMessageException;
import org.spark.libsignal.protocol.LegacyMessageException;
import org.spark.libsignal.protocol.NoSessionException;
import org.spark.libsignal.protocol.groups.GroupCipher;
import org.spark.libsignal.protocol.message.CiphertextMessage;
import org.whispersystems.signalservice.api.SignalSessionLock;

import java.util.UUID;

/**
 * A thread-safe wrapper around {@link GroupCipher}.
 */
public class SignalGroupCipher {

  private final SignalSessionLock lock;
  private final GroupCipher       cipher;

  public SignalGroupCipher(SignalSessionLock lock, GroupCipher cipher) {
    this.lock   = lock;
    this.cipher = cipher;
  }

  public CiphertextMessage encrypt(UUID distributionId, byte[] paddedPlaintext) throws NoSessionException {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.encrypt(distributionId, paddedPlaintext);
    }
  }

  public byte[] decrypt(byte[] senderKeyMessageBytes)
      throws LegacyMessageException, DuplicateMessageException, InvalidMessageException, NoSessionException
  {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.decrypt(senderKeyMessageBytes);
    }
  }
}
