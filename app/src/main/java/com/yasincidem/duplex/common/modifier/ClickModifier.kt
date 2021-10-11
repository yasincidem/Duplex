package com.yasincidem.duplex.common.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.disableMultiTouch(isTouchDisabled: Boolean = false): Modifier =
    pointerInput(isTouchDisabled) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(PointerEventPass.Initial).changes.forEachIndexed { index, change ->
                    if (index > 0 || isTouchDisabled)
                        change.consumeDownChange()
                }
            }
        }
    }
