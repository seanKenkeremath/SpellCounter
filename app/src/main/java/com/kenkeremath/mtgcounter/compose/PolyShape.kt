package com.kenkeremath.mtgcounter.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class PolyShape(private val sides: Int) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(path = Path().apply { polygon(sides, size.minDimension/2f, size.center) })
    }
}

fun Path.polygon(sides: Int, radius: Float, center: Offset) {
    val angle = 2.0 * Math.PI / sides
    moveTo(
        x = center.x + (radius * kotlin.math.cos(0.0)).toFloat(),
        y = center.y + (radius * kotlin.math.sin(0.0)).toFloat()
    )
    for (i in 1 until sides) {
        lineTo(
            x = center.x + (radius * kotlin.math.cos(angle * i)).toFloat(),
            y = center.y + (radius * kotlin.math.sin(angle * i)).toFloat()
        )
    }
    close()
}