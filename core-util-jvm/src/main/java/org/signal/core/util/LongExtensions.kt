/*
 * Copyright 2024 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.spark.core.util

import java.nio.ByteBuffer

/**
 * Converts the long into [ByteArray].
 */
fun Long.toByteArray(): ByteArray {
  return ByteBuffer
    .allocate(Long.SIZE_BYTES)
    .putLong(this)
    .array()
}
