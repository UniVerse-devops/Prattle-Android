package org.thoughtcrime.securesms.scribbles.stickers

import org.sparkimageeditor.core.Renderer

/**
 * A renderer that can handle a tap event
 */
interface TappableRenderer : Renderer {
  fun onTapped()
}
