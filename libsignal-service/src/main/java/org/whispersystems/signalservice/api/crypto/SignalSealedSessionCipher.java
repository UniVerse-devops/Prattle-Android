package org.whispersystems.signalservice.api.crypto;

import org.spark.libsignal.internal.Native;
import org.spark.libsignal.internal.NativeHandleGuard;
import org.spark.libsignal.metadata.InvalidMetadataMessageException;
import org.spark.libsignal.metadata.InvalidMetadataVersionException;
import org.spark.libsignal.metadata.ProtocolDuplicateMessageException;
import org.spark.libsignal.metadata.ProtocolInvalidKeyException;
import org.spark.libsignal.metadata.ProtocolInvalidKeyIdException;
import org.spark.libsignal.metadata.ProtocolInvalidMessageException;
import org.spark.libsignal.metadata.ProtocolInvalidVersionException;
import org.spark.libsignal.metadata.ProtocolLegacyMessageException;
import org.spark.libsignal.metadata.ProtocolNoSessionException;
import org.spark.libsignal.metadata.ProtocolUntrustedIdentityException;
import org.spark.libsignal.metadata.SealedSessionCipher;
import org.spark.libsignal.metadata.SelfSendException;
import org.spark.libsignal.metadata.certificate.CertificateValidator;
import org.spark.libsignal.metadata.protocol.UnidentifiedSenderMessageContent;
import org.spark.libsignal.protocol.InvalidKeyException;
import org.spark.libsignal.protocol.InvalidRegistrationIdException;
import org.spark.libsignal.protocol.NoSessionException;
import org.spark.libsignal.protocol.SignalProtocolAddress;
import org.spark.libsignal.protocol.UntrustedIdentityException;
import org.spark.libsignal.protocol.state.SessionRecord;
import org.spark.libsignal.protocol.state.SignalProtocolStore;
import org.whispersystems.signalservice.api.SignalSessionLock;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A thread-safe wrapper around {@link SealedSessionCipher}.
 */
public class SignalSealedSessionCipher {

  private final SignalSessionLock   lock;
  private final SealedSessionCipher cipher;

  public SignalSealedSessionCipher(SignalSessionLock lock, SealedSessionCipher cipher) {
    this.lock   = lock;
    this.cipher = cipher;
  }

  public byte[] encrypt(SignalProtocolAddress destinationAddress, UnidentifiedSenderMessageContent content)
      throws InvalidKeyException, UntrustedIdentityException
  {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.encrypt(destinationAddress, content);
    }
  }

  public byte[] multiRecipientEncrypt(List<SignalProtocolAddress> recipients, Map<SignalProtocolAddress, SessionRecord> sessionMap, UnidentifiedSenderMessageContent content)
      throws InvalidKeyException, UntrustedIdentityException, NoSessionException, InvalidRegistrationIdException
  {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      List<SessionRecord> recipientSessions = recipients.stream().map(sessionMap::get).collect(Collectors.toList());

      if (recipientSessions.contains(null)) {
        throw new NoSessionException("No session for some recipients");
      }

      return cipher.multiRecipientEncrypt(recipients, recipientSessions, content);
    }
  }

  public SealedSessionCipher.DecryptionResult decrypt(CertificateValidator validator, byte[] ciphertext, long timestamp) throws InvalidMetadataMessageException, InvalidMetadataVersionException, ProtocolInvalidMessageException, ProtocolInvalidKeyException, ProtocolNoSessionException, ProtocolLegacyMessageException, ProtocolInvalidVersionException, ProtocolDuplicateMessageException, ProtocolInvalidKeyIdException, ProtocolUntrustedIdentityException, SelfSendException {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.decrypt(validator, ciphertext, timestamp);
    }
  }

  public int getSessionVersion(SignalProtocolAddress remoteAddress) {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.getSessionVersion(remoteAddress);
    }
  }

  public int getRemoteRegistrationId(SignalProtocolAddress remoteAddress) {
    try (SignalSessionLock.Lock unused = lock.acquire()) {
      return cipher.getRemoteRegistrationId(remoteAddress);
    }
  }
}
