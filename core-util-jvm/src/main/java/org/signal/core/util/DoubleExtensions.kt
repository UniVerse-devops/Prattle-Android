/*
 * Copyright 2023 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.spark.core.util

import java.util.Locale

/**
 * Rounds a number to the specified number of places. e.g.
 *
 * 1.123456f.roundedString(2) = 1.12
 * 1.123456f.roundedString(5) = 1.12346
 */
fun Double.roundedString(places: Int): String {
  return String.format(Locale.US, "%.${places}f", this)
}
