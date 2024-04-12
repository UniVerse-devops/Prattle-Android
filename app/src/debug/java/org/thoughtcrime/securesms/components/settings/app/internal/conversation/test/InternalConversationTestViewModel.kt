/*
 * Copyright 2023 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.thoughtcrime.securesms.components.settings.app.internal.conversation.test

import androidx.lifecycle.ViewModel
import org.spark.paging.PagedData
import org.spark.paging.PagingConfig

class InternalConversationTestViewModel : ViewModel() {
  private val generator = ConversationElementGenerator()
  private val dataSource = InternalConversationTestDataSource(
    500,
    generator
  )

  private val config = PagingConfig.Builder().setPageSize(25)
    .setBufferPages(2)
    .build()

  private val pagedData = PagedData.createForObservable(dataSource, config)

  val controller = pagedData.controller
  val data = pagedData.data
}
