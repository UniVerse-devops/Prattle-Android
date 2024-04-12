package org.thoughtcrime.securesms;

import org.spark.libsignal.zkgroup.ServerPublicParams;
import org.spark.libsignal.zkgroup.ServerSecretParams;
import org.spark.libsignal.zkgroup.VerificationFailedException;
import org.spark.libsignal.zkgroup.groups.GroupPublicParams;
import org.spark.libsignal.zkgroup.profiles.ProfileKeyCommitment;
import org.spark.libsignal.zkgroup.profiles.ProfileKeyCredentialPresentation;
import org.spark.libsignal.zkgroup.profiles.ProfileKeyCredentialRequest;
import org.spark.libsignal.zkgroup.profiles.ServerZkProfileOperations;
import org.whispersystems.signalservice.test.LibSignalLibraryUtil;

import java.util.UUID;

/**
 * Provides Zk group operations that the server would provide.
 * Copied in app from libsignal
 */
public final class TestZkGroupServer {

  private final ServerPublicParams        serverPublicParams;
  private final ServerZkProfileOperations serverZkProfileOperations;

  public TestZkGroupServer() {
    LibSignalLibraryUtil.assumeLibSignalSupportedOnOS();

    ServerSecretParams serverSecretParams = ServerSecretParams.generate();

    serverPublicParams        = serverSecretParams.getPublicParams();
    serverZkProfileOperations = new ServerZkProfileOperations(serverSecretParams);
  }

  public ServerPublicParams getServerPublicParams() {
    return serverPublicParams;
  }
}
