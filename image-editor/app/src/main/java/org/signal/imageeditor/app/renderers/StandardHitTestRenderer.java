package org.sparkimageeditor.app.renderers;

import org.sparkimageeditor.core.Bounds;
import org.sparkimageeditor.core.Renderer;

public abstract class StandardHitTestRenderer implements Renderer {

  @Override
  public boolean hitTest(float x, float y) {
    return Bounds.contains(x, y);
  }
}
