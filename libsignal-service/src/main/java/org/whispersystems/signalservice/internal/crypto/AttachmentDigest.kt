/*
 * Copyright 2023 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.whispersystems.signalservice.internal.crypto

data class AttachmentDigest(val digest: ByteArray, val incrementalDigest: ByteArray?, val incrementalMacChunkSize: Int)
