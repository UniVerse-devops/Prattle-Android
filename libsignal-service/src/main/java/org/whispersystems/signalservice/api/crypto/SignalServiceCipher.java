/*
 * Copyright (C) 2014-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 */

package org.whispersystems.signalservice.api.crypto;

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
import org.spark.libsignal.metadata.SealedSessionCipher.DecryptionResult;
import org.spark.libsignal.metadata.SelfSendException;
import org.spark.libsignal.metadata.certificate.CertificateValidator;
import org.spark.libsignal.metadata.certificate.SenderCertificate;
import org.spark.libsignal.metadata.protocol.UnidentifiedSenderMessageContent;
import org.spark.libsignal.protocol.DuplicateMessageException;
import org.spark.libsignal.protocol.InvalidKeyException;
import org.spark.libsignal.protocol.InvalidKeyIdException;
import org.spark.libsignal.protocol.InvalidMessageException;
import org.spark.libsignal.protocol.InvalidRegistrationIdException;
import org.spark.libsignal.protocol.InvalidSessionException;
import org.spark.libsignal.protocol.InvalidVersionException;
import org.spark.libsignal.protocol.LegacyMessageException;
import org.spark.libsignal.protocol.NoSessionException;
import org.spark.libsignal.protocol.SessionCipher;
import org.spark.libsignal.protocol.SignalProtocolAddress;
import org.spark.libsignal.protocol.UntrustedIdentityException;
import org.spark.libsignal.protocol.groups.GroupCipher;
import org.spark.libsignal.protocol.logging.Log;
import org.spark.libsignal.protocol.message.CiphertextMessage;
import org.spark.libsignal.protocol.message.PlaintextContent;
import org.spark.libsignal.protocol.message.PreKeySignalMessage;
import org.spark.libsignal.protocol.message.SignalMessage;
import org.spark.libsignal.protocol.state.SessionRecord;
import org.whispersystems.signalservice.api.InvalidMessageStructureException;
import org.whispersystems.signalservice.api.SignalServiceAccountDataStore;
import org.whispersystems.signalservice.api.SignalSessionLock;
import org.whispersystems.signalservice.api.messages.SignalServiceMetadata;
import org.whispersystems.signalservice.api.push.DistributionId;
import org.whispersystems.signalservice.api.push.ServiceId;
import org.whispersystems.signalservice.api.push.ServiceId.ACI;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.internal.push.Content;
import org.whispersystems.signalservice.internal.push.Envelope;
import org.whispersystems.signalservice.internal.push.OutgoingPushMessage;
import org.whispersystems.signalservice.internal.push.PushTransportDetails;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is used to encrypt + decrypt received envelopes.
 */
public class SignalServiceCipher {

  @SuppressWarnings("unused")
  private static final String TAG = SignalServiceCipher.class.getSimpleName();

  private final SignalServiceAccountDataStore signalProtocolStore;
  private final SignalSessionLock             sessionLock;
  private final SignalServiceAddress localAddress;
  private final int                  localDeviceId;
  private final CertificateValidator certificateValidator;

  public SignalServiceCipher(SignalServiceAddress localAddress,
                             int localDeviceId,
                             SignalServiceAccountDataStore signalProtocolStore,
                             SignalSessionLock sessionLock,
                             CertificateValidator certificateValidator)
  {
    this.signalProtocolStore  = signalProtocolStore;
    this.sessionLock          = sessionLock;
    this.localAddress         = localAddress;
    this.localDeviceId        = localDeviceId;
    this.certificateValidator = certificateValidator;
  }

  public byte[] encryptForGroup(DistributionId distributionId,
                                List<SignalProtocolAddress> destinations,
                                Map<SignalProtocolAddress, SessionRecord> sessionMap,
                                SenderCertificate senderCertificate,
                                byte[] unpaddedMessage,
                                ContentHint contentHint,
                                Optional<byte[]> groupId)
      throws NoSessionException, UntrustedIdentityException, InvalidKeyException, InvalidRegistrationIdException
  {
    PushTransportDetails             transport            = new PushTransportDetails();
    SignalProtocolAddress            localProtocolAddress = new SignalProtocolAddress(localAddress.getIdentifier(), localDeviceId);
    SignalGroupCipher                groupCipher          = new SignalGroupCipher(sessionLock, new GroupCipher(signalProtocolStore, localProtocolAddress));
    SignalSealedSessionCipher        sessionCipher        = new SignalSealedSessionCipher(sessionLock, new SealedSessionCipher(signalProtocolStore, localAddress.getServiceId().getRawUuid(), localAddress.getNumber().orElse(null), localDeviceId));
    CiphertextMessage                message              = groupCipher.encrypt(distributionId.asUuid(), transport.getPaddedMessageBody(unpaddedMessage));
    UnidentifiedSenderMessageContent messageContent       = new UnidentifiedSenderMessageContent(message,
                                                                                                 senderCertificate,
                                                                                                 contentHint.getType(),
                                                                                                 groupId);

    return sessionCipher.multiRecipientEncrypt(destinations, sessionMap, messageContent);
  }

  public OutgoingPushMessage encrypt(SignalProtocolAddress destination,
                                     Optional<UnidentifiedAccess> unidentifiedAccess,
                                     EnvelopeContent content)
      throws UntrustedIdentityException, InvalidKeyException
  {
    try {
      SignalSessionCipher sessionCipher = new SignalSessionCipher(sessionLock, new SessionCipher(signalProtocolStore, destination));
      if (unidentifiedAccess.isPresent()) {
        SignalSealedSessionCipher sealedSessionCipher = new SignalSealedSessionCipher(sessionLock, new SealedSessionCipher(signalProtocolStore, localAddress.getServiceId().getRawUuid(), localAddress.getNumber()
                                                                                                                                                                                                      .orElse(null), localDeviceId));

        return content.processSealedSender(sessionCipher, sealedSessionCipher, destination, unidentifiedAccess.get().getUnidentifiedCertificate());
      } else {
        return content.processUnsealedSender(sessionCipher, destination);
      }
    } catch (NoSessionException e) {
      throw new InvalidSessionException("Session not found.");
    }
  }

  public SignalServiceCipherResult decrypt(Envelope envelope, long serverDeliveredTimestamp)
      throws InvalidMetadataMessageException, InvalidMetadataVersionException,
             ProtocolInvalidKeyIdException, ProtocolLegacyMessageException,
             ProtocolUntrustedIdentityException, ProtocolNoSessionException,
             ProtocolInvalidVersionException, ProtocolInvalidMessageException,
             ProtocolInvalidKeyException, ProtocolDuplicateMessageException,
             SelfSendException, InvalidMessageStructureException
  {
    try {
      if (envelope.content != null) {
        Plaintext plaintext = decryptInternal(envelope, serverDeliveredTimestamp);
        Content   content   = Content.ADAPTER.decode(plaintext.getData());

        return new SignalServiceCipherResult(
            content,
            new EnvelopeMetadata(
                plaintext.metadata.getSender().getServiceId(),
                plaintext.metadata.getSender().getNumber().orElse(null),
                plaintext.metadata.getSenderDevice(),
                plaintext.metadata.isNeedsReceipt(),
                plaintext.metadata.getGroupId().orElse(null),
                localAddress.getServiceId()
            )
        );
      } else {
        return null;
      }
    } catch (IOException | IllegalArgumentException e) {
      throw new InvalidMetadataMessageException(e);
    }
  }

  private Plaintext decryptInternal(Envelope envelope, long serverDeliveredTimestamp)
      throws InvalidMetadataMessageException, InvalidMetadataVersionException,
      ProtocolDuplicateMessageException, ProtocolUntrustedIdentityException,
      ProtocolLegacyMessageException, ProtocolInvalidKeyException,
      ProtocolInvalidVersionException, ProtocolInvalidMessageException,
      ProtocolInvalidKeyIdException, ProtocolNoSessionException,
      SelfSendException, InvalidMessageStructureException
  {
    try {

      byte[]                paddedMessage;
      SignalServiceMetadata metadata;

      if (envelope.sourceServiceId == null && envelope.type != Envelope.Type.UNIDENTIFIED_SENDER) {
        throw new InvalidMessageStructureException("Non-UD envelope is missing a UUID!");
      }

      if (envelope.type == Envelope.Type.PREKEY_BUNDLE) {
        SignalProtocolAddress sourceAddress = new SignalProtocolAddress(envelope.sourceServiceId, envelope.sourceDevice);
        SignalSessionCipher   sessionCipher = new SignalSessionCipher(sessionLock, new SessionCipher(signalProtocolStore, sourceAddress));

        paddedMessage = sessionCipher.decrypt(new PreKeySignalMessage(envelope.content.toByteArray()));
        metadata      = new SignalServiceMetadata(getSourceAddress(envelope), envelope.sourceDevice, envelope.timestamp, envelope.serverTimestamp, serverDeliveredTimestamp, false, envelope.serverGuid, Optional.empty(), envelope.destinationServiceId);

        signalProtocolStore.clearSenderKeySharedWith(Collections.singleton(sourceAddress));
      } else if (envelope.type == Envelope.Type.CIPHERTEXT) {
        SignalProtocolAddress sourceAddress = new SignalProtocolAddress(envelope.sourceServiceId, envelope.sourceDevice);
        SignalSessionCipher   sessionCipher = new SignalSessionCipher(sessionLock, new SessionCipher(signalProtocolStore, sourceAddress));

        paddedMessage = sessionCipher.decrypt(new SignalMessage(envelope.content.toByteArray()));
        metadata      = new SignalServiceMetadata(getSourceAddress(envelope), envelope.sourceDevice, envelope.timestamp, envelope.serverTimestamp, serverDeliveredTimestamp, false, envelope.serverGuid, Optional.empty(), envelope.destinationServiceId);
      } else if (envelope.type == Envelope.Type.PLAINTEXT_CONTENT) {
        paddedMessage = new PlaintextContent(envelope.content.toByteArray()).getBody();
        metadata      = new SignalServiceMetadata(getSourceAddress(envelope), envelope.sourceDevice, envelope.timestamp, envelope.serverTimestamp, serverDeliveredTimestamp, false, envelope.serverGuid, Optional.empty(), envelope.destinationServiceId);
      } else if (envelope.type == Envelope.Type.UNIDENTIFIED_SENDER) {
        SignalSealedSessionCipher sealedSessionCipher = new SignalSealedSessionCipher(sessionLock, new SealedSessionCipher(signalProtocolStore, localAddress.getServiceId().getRawUuid(), localAddress.getNumber().orElse(null), localDeviceId));
        DecryptionResult          result              = sealedSessionCipher.decrypt(certificateValidator, envelope.content.toByteArray(), envelope.serverTimestamp);
        SignalServiceAddress      resultAddress       = new SignalServiceAddress(ACI.parseOrThrow(result.getSenderUuid()), result.getSenderE164());
        Optional<byte[]>          groupId             = result.getGroupId();
        boolean                   needsReceipt        = true;

        if (envelope.sourceServiceId != null) {
          Log.w(TAG, "[" + envelope.timestamp + "] Received a UD-encrypted message sent over an identified channel. Marking as needsReceipt=false");
          needsReceipt = false;
        }

        if (result.getCiphertextMessageType() == CiphertextMessage.PREKEY_TYPE) {
          signalProtocolStore.clearSenderKeySharedWith(Collections.singleton(new SignalProtocolAddress(result.getSenderUuid(), result.getDeviceId())));
        }

        paddedMessage = result.getPaddedMessage();
        metadata      = new SignalServiceMetadata(resultAddress, result.getDeviceId(), envelope.timestamp, envelope.serverTimestamp, serverDeliveredTimestamp, needsReceipt, envelope.serverGuid, groupId, envelope.destinationServiceId);
      } else {
        throw new InvalidMetadataMessageException("Unknown type: " + envelope.type);
      }

      PushTransportDetails transportDetails = new PushTransportDetails();
      byte[]               data             = transportDetails.getStrippedPaddingMessageBody(paddedMessage);

      return new Plaintext(metadata, data);
    } catch (DuplicateMessageException e) {
      throw new ProtocolDuplicateMessageException(e, envelope.sourceServiceId, envelope.sourceDevice);
    } catch (LegacyMessageException e) {
      throw new ProtocolLegacyMessageException(e, envelope.sourceServiceId, envelope.sourceDevice);
    } catch (InvalidMessageException e) {
      throw new ProtocolInvalidMessageException(e, envelope.sourceServiceId, envelope.sourceDevice);
    } catch (InvalidKeyIdException e) {
      throw new ProtocolInvalidKeyIdException(e, envelope.sourceServiceId, envelope.sourceDevice);
    } catch (InvalidKeyException e) {
      throw new ProtocolInvalidKeyException(e, envelope.sourceServiceId, envelope.sourceDevice);
    } catch (UntrustedIdentityException e) {
      throw new ProtocolUntrustedIdentityException(e, envelope.sourceServiceId, envelope.sourceDevice);
    } catch (InvalidVersionException e) {
      throw new ProtocolInvalidVersionException(e, envelope.sourceServiceId, envelope.sourceDevice);
    } catch (NoSessionException e) {
      throw new ProtocolNoSessionException(e, envelope.sourceServiceId, envelope.sourceDevice);
    }
  }

  private static SignalServiceAddress getSourceAddress(Envelope envelope) {
    return new SignalServiceAddress(ServiceId.parseOrNull(envelope.sourceServiceId));
  }

  private static class Plaintext {
    private final SignalServiceMetadata metadata;
    private final byte[]   data;

    private Plaintext(SignalServiceMetadata metadata, byte[] data) {
      this.metadata = metadata;
      this.data     = data;
    }

    public SignalServiceMetadata getMetadata() {
      return metadata;
    }

    public byte[] getData() {
      return data;
    }
  }
}
