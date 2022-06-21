package com.kenkeremath.mtgcounter.compose

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils

object ComposeTheme {

    val LocalScColors = staticCompositionLocalOf {
        ScThemeColors()
    }

    @Composable
    fun ScComposeTheme(
        content: @Composable () -> Unit,
    ) {

        val context = LocalContext.current
        val scColors = ScThemeColors(
            isLight = ScThemeUtils.isLightTheme(context),
            scToolbarColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scToolbarColor
                )
            ),
            scStatusBarColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scStatusBarColor
                )
            ),
            scAccentColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scAccentColor
                )
            ),
            scAccentContrastTextColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scAccentContrastTextColor
                )
            ),
            scRippleColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scRippleColor
                )
            ),
            scCounterSelectionColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scCounterSelectionColor
                )
            ),
            scOptionButtonColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scOptionButtonColor
                )
            ),
            scGameButtonColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scGameButtonColor
                )
            ),
            scMenuEnabledButtonColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scMenuEnabledButtonColor
                )
            ),
            scMenuDisabledButtonColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scMenuDisabledButtonColor
                )
            ),
            scMenuPressedButtonColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scMenuPressedButtonColor
                )
            ),
            scMenuButtonTextColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scMenuButtonTextColor
                )
            ),
            scTextColorCaption = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scTextColorCaption
                )
            ),
            scTextColorPrimary = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scTextColorPrimary
                )
            ),
            scDividerColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scDividerColor
                )
            ),
            scPlayerDividerColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scPlayerDividerColor
                )
            ),
            scBackgroundColor = Color(
                ScThemeUtils.resolveThemeColor(
                    context,
                    R.attr.scBackgroundColor
                )
            ),
        )

        CompositionLocalProvider(
            LocalScColors provides scColors
        ) {
            MaterialTheme(
                colors = Colors(
                    isLight = scColors.isLight,
                    primary = scColors.scToolbarColor,
                    primaryVariant = scColors.scStatusBarColor,
                    secondary = scColors.scAccentColor,
                    secondaryVariant = scColors.scRippleColor,
                    background = scColors.scBackgroundColor,
                    surface = scColors.scOptionButtonColor,
                    error = Color.Red,
                    onPrimary = scColors.scTextColorPrimary,
                    onSecondary = scColors.scAccentContrastTextColor,
                    onBackground = scColors.scTextColorPrimary,
                    onSurface = scColors.scTextColorPrimary,
                    onError = scColors.scTextColorPrimary,
                ),
                content = content
            )
        }
    }

    //TODO: defaults? probably only helpful for preview
    data class ScThemeColors(
        val isLight: Boolean = true,
        val scStatusBarColor: Color = Color.White,
        val scToolbarColor: Color = Color.Blue,
        val scAccentColor: Color = Color.Blue,
        val scAccentContrastTextColor: Color = Color.White,
        val scRippleColor: Color = Color.White,
        val scCounterSelectionColor: Color = Color.White,
        val scOptionButtonColor: Color = Color.White,
        val scGameButtonColor: Color = Color.White,
        val scMenuEnabledButtonColor: Color = Color.White,
        val scMenuDisabledButtonColor: Color = Color.White,
        val scMenuPressedButtonColor: Color = Color.White,
        val scMenuButtonTextColor: Color = Color.Black,
        val scTextColorCaption: Color = Color.Gray,
        val scTextColorPrimary: Color = Color.Black,
        val scDividerColor: Color = Color.Gray,
        val scPlayerDividerColor: Color = Color.Gray,
        val scBackgroundColor: Color = Color.White,
    )
}