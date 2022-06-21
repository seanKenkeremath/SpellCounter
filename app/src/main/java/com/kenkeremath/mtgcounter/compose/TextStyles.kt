package com.kenkeremath.mtgcounter.compose

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

//TODO: colors
object TextStyles {
    val toolbarTitle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.SansSerif
    )

    val header1 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.SansSerif
    )

    val header2 = header1.copy(
        fontSize = 24.sp
    )

    //TODO: drawable padding?
    val header3 = header1.copy(
        fontSize = 14.sp
    )

    //TODO: color
    val subheader = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily.SansSerif
    )

    //TODO: color
    val label = TextStyle(
        fontSize = 16.sp,
    )

    //TODO: color + drawable padding/tint
    val navMenuText = TextStyle(
        fontSize = 16.sp,
    )

    //TODO: color
    val selectableText = label.copy(
    )

    //TODO: color
    val gameButtonLabel = header2.copy(
    )
}