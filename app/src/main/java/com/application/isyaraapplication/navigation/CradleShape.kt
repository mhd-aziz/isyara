package com.application.isyaraapplication.navigation

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class CradleShape(
    private val fabDiameter: Dp,
    private val cornerRadius: Dp = 24.dp,
    private val cradleVerticalOffset: Dp = 24.dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val fabRadiusPx = with(density) { (fabDiameter / 2).toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val cradleVerticalOffsetPx = with(density) { cradleVerticalOffset.toPx() }

        val cradleCenterX = size.width / 2
        val cradleHorizontalStart = cradleCenterX - fabRadiusPx - cornerRadiusPx

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(cradleHorizontalStart, 0f)
            arcTo(
                rect = Rect(
                    left = cradleHorizontalStart,
                    top = 0f,
                    right = cradleHorizontalStart + cornerRadiusPx,
                    bottom = cornerRadiusPx
                ),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            arcTo(
                rect = Rect(
                    left = cradleCenterX - fabRadiusPx,
                    top = -cradleVerticalOffsetPx,
                    right = cradleCenterX + fabRadiusPx,
                    bottom = fabRadiusPx - cradleVerticalOffsetPx
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            arcTo(
                rect = Rect(
                    left = cradleCenterX + fabRadiusPx,
                    top = 0f,
                    right = cradleCenterX + fabRadiusPx + cornerRadiusPx,
                    bottom = cornerRadiusPx
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -90f,
                forceMoveTo = false
            )
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}