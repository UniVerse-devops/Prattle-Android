/*
 * Copyright 2024 Spark Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.spark.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.spark.core.ui.theme.SignalTheme

object Previews {
  @Composable
  fun Preview(
    content: @Composable () -> Unit
  ) {
    SignalTheme {
      Surface {
        content()
      }
    }
  }

  @Composable
  fun BottomSheetPreview(
    content: @Composable () -> Unit
  ) {
    SignalTheme {
      Surface {
        Box(modifier = Modifier.background(color = SignalTheme.colors.colorSurface1)) {
          content()
        }
      }
    }
  }
}
