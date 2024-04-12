/*
 * Copyright 2024 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.whispersystems.signalservice.api.util

import org.spark.libsignal.usernames.Username

val Username.nickname: String get() = username.split(Usernames.DELIMITER)[0]
val Username.discriminator: String get() = username.split(Usernames.DELIMITER)[1]
