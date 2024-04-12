/*
 * Copyright 2023 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.whispersystems.signalservice.internal.push

import com.fasterxml.jackson.annotation.JsonCreator
import org.spark.core.util.Base64
import org.spark.libsignal.zkgroup.calllinks.CreateCallLinkCredentialRequest

/**
 * Request body to create a call link credential response.
 */
data class CreateCallLinkAuthRequest @JsonCreator constructor(
  val createCallLinkCredentialRequest: String
) {
  companion object {
    @JvmStatic
    fun create(createCallLinkCredentialRequest: CreateCallLinkCredentialRequest): CreateCallLinkAuthRequest {
      return CreateCallLinkAuthRequest(
        Base64.encodeWithPadding(createCallLinkCredentialRequest.serialize())
      )
    }
  }
}
