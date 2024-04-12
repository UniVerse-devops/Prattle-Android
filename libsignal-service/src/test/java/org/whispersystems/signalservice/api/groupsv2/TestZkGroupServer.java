package org.whispersystems.signalservice.api.groupsv2;

import org.spark.libsignal.zkgroup.ServerPublicParams;
import org.spark.libsignal.zkgroup.ServerSecretParams;
import org.spark.libsignal.zkgroup.VerificationFailedException;
import org.spark.libsignal.zkgroup.groups.GroupPublicParams;
import org.spark.libsignal.zkgroup.profiles.ExpiringProfileKeyCredentialResponse;
import org.spark.libsignal.zkgroup.profiles.ProfileKeyCommitment;
import org.spark.libsignal.zkgroup.profiles.ProfileKeyCredentialPresentation;
import org.spark.libsignal.zkgroup.profiles.ProfileKeyCredentialRequest;
import org.spark.libsignal.zkgroup.profiles.ServerZkProfileOperations;
import org.whispersystems.signalservice.api.push.ServiceId.ACI;
import org.whispersystems.signalservice.testutil.LibSignalLibraryUtil;

import java.time.Instant;

/**
 * Provides Zk group operations that the server would provide.
 */
final class TestZkGroupServer {

  private final ServerPublicParams        serverPublicParams;
  private final ServerZkProfileOperations serverZkProfileOperations;

  TestZkGroupServer() {
    LibSignalLibraryUtil.assumeLibSignalSupportedOnOS();

    ServerSecretParams serverSecretParams = ServerSecretParams.generate();

    serverPublicParams        = serverSecretParams.getPublicParams();
    serverZkProfileOperations = new ServerZkProfileOperations(serverSecretParams);
  }

  public ServerPublicParams getServerPublicParams() {
    return serverPublicParams;
  }

  public ExpiringProfileKeyCredentialResponse getExpiringProfileKeyCredentialResponse(ProfileKeyCredentialRequest request, ACI aci, ProfileKeyCommitment commitment, Instant expiration) throws VerificationFailedException {
    return serverZkProfileOperations.issueExpiringProfileKeyCredential(request, aci.getLibSignalAci(), commitment, expiration);
  }

  public void assertProfileKeyCredentialPresentation(GroupPublicParams publicParams, ProfileKeyCredentialPresentation profileKeyCredentialPresentation, Instant now) {
    try {
      serverZkProfileOperations.verifyProfileKeyCredentialPresentation(publicParams, profileKeyCredentialPresentation, now);
    } catch (VerificationFailedException e) {
      throw new AssertionError(e);
    }
  }
}
