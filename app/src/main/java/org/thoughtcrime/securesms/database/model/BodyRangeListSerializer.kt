package org.thoughtcrime.securesms.database.model

import org.sparkcore.util.Base64
import org.sparkcore.util.StringSerializer
import org.thoughtcrime.securesms.database.model.databaseprotos.BodyRangeList

object BodyRangeListSerializer : StringSerializer<BodyRangeList> {
  override fun serialize(data: BodyRangeList): String = Base64.encodeWithPadding(data.encode())
  override fun deserialize(data: String): BodyRangeList = BodyRangeList.ADAPTER.decode(Base64.decode(data))
}

fun BodyRangeList.serialize(): String {
  return BodyRangeListSerializer.serialize(this)
}
