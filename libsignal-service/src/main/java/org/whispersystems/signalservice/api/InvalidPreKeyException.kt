/*
 * Copyright 2023 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.whispersystems.signalservice.api

import org.spark.libsignal.protocol.InvalidKeyException
import org.spark.libsignal.protocol.SignalProtocolAddress
import java.io.IOException

/**
 * Wraps an [InvalidKeyException] in an [IOException] with a nicer message.
 */
class InvalidPreKeyException(
  address: SignalProtocolAddress,
  invalidKeyException: InvalidKeyException
) : IOException("Invalid prekey for $address", invalidKeyException)
