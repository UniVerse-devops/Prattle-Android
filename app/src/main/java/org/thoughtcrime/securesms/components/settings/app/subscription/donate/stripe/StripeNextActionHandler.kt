/*
 * Copyright 2023 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.thoughtcrime.securesms.components.settings.app.subscription.donate.stripe

import io.reactivex.rxjava3.core.Single
import org.sparkdonations.StripeApi
import org.sparkdonations.StripeIntentAccessor

fun interface StripeNextActionHandler {
  fun handle(
    action: StripeApi.Secure3DSAction,
    stripe3DSData: Stripe3DSData
  ): Single<StripeIntentAccessor>
}
