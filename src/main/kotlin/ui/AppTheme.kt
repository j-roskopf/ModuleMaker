package ui

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

object AppTheme {

    val colors: Colors = Colors()

    class Colors(
        private val backgroundDark: Color = Color(0xFF2B2B2B),
        private val backgroundMedium: Color = Color(0xFF3C3F41),
        val backgroundLight: Color = Color(0xFF4E5254),

        val material: androidx.compose.material.Colors = darkColors(
            background = backgroundDark,
            surface = backgroundMedium,
            primary = Color.White
        ),
    )
}
