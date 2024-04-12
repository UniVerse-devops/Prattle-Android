package org.thoughtcrime.securesms.database.model

import org.sparkcore.util.DatabaseId
import org.sparkcore.util.IntSerializer

/**
 * Export status for a message.
 */
enum class MessageExportStatus(val code: Int) : DatabaseId {
  UNEXPORTED(0),
  EXPORTED(1),
  ERROR(-1);

  override fun serialize(): String {
    return Serializer.serialize(this).toString()
  }

  companion object Serializer : IntSerializer<MessageExportStatus> {
    override fun serialize(data: MessageExportStatus): Int {
      return data.code
    }

    override fun deserialize(data: Int): MessageExportStatus {
      return when (data) {
        UNEXPORTED.code -> UNEXPORTED
        EXPORTED.code -> EXPORTED
        ERROR.code -> ERROR
        else -> throw AssertionError("Unknown message export status: $data")
      }
    }
  }
}
