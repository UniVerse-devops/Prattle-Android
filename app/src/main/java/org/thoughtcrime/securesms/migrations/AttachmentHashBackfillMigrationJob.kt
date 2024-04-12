/*
 * Copyright 2024 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.thoughtcrime.securesms.migrations

import org.sparkcore.util.logging.Log
import org.thoughtcrime.securesms.dependencies.ApplicationDependencies
import org.thoughtcrime.securesms.jobmanager.Job
import org.thoughtcrime.securesms.jobs.AttachmentHashBackfillJob
import java.lang.Exception

/**
 * Kicks off the attachment hash backfill process by enqueueing a [AttachmentHashBackfillJob].
 */
internal class AttachmentHashBackfillMigrationJob(parameters: Parameters = Parameters.Builder().build()) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(AttachmentHashBackfillMigrationJob::class.java)
    const val KEY = "AttachmentHashBackfillMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    ApplicationDependencies.getJobManager().add(AttachmentHashBackfillJob())
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<AttachmentHashBackfillMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): AttachmentHashBackfillMigrationJob {
      return AttachmentHashBackfillMigrationJob(parameters)
    }
  }
}
