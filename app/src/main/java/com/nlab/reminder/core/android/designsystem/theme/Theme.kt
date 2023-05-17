/*
 * Copyright (C) 2023 The N's lab Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nlab.reminder.core.android.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import javax.annotation.concurrent.Immutable

/**
 * Theme color set used in Reminder.
 * @author Doohyun
 */
@Immutable
data class ReminderColorScheme(
    val bg1: Color,
    val bgCard1: Color,
    val bgCard1Ripple: Color,
    val bgLine1: Color,
    val bgTag: Color,
    val bgTagRipple: Color,
    val font1: Color,
    val font2: Color,
    val fontTag: Color
)

val LocalReminderColorScheme = staticCompositionLocalOf {
    ReminderColorScheme(
        bg1 = Color.Unspecified,
        bgCard1 = Color.Unspecified,
        bgCard1Ripple = Color.Unspecified,
        bgLine1 = Color.Unspecified,
        bgTag = Color.Unspecified,
        bgTagRipple = Color.Unspecified,
        font1 = Color.Unspecified,
        font2 = Color.Unspecified,
        fontTag = Color.Unspecified
    )
}

val LightDefaultColorScheme = lightColorScheme(
    surface = Gray1
)
val LightReminderColorScheme = ReminderColorScheme(
    bg1 = Bg1Light,
    bgCard1 = BgCard1Light,
    bgCard1Ripple = BgCard1RippleLight,
    bgLine1 = BgLine1Light,
    bgTag = BgTagLight,
    bgTagRipple = BgTagRippleLight,
    font1 = Font1Light,
    font2 = Font2Light,
    fontTag = FontTagLight
)

val DarkDefaultColorScheme = darkColorScheme(
    surface = Color.Black,
)
val DarkReminderColorScheme = ReminderColorScheme(
    bg1 = Bg1Dark,
    bgCard1 = BgCard1Dark,
    bgCard1Ripple = BgCard1RippleDark,
    bgLine1 = BgLine1Dark,
    bgTag = BgTagDark,
    bgTagRipple = BgTagRippleDark,
    font1 = Font1Dark,
    font2 = Font2Dark,
    fontTag = FontTagDark
)

/**
 * Extended Theme for Reminder.
 * @see <a href="https://developer.android.com/jetpack/compose/designsystems/custom?hl=ko">Reference</a>
 * @author Doohyun
 */
object ReminderTheme {
    val colors: ReminderColorScheme @Composable get() = LocalReminderColorScheme.current
}

@Composable
fun ReminderTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val defaultColorScheme = if (isDarkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    val reminderColorScheme = if (isDarkTheme) DarkReminderColorScheme else LightReminderColorScheme

    CompositionLocalProvider(
        LocalReminderColorScheme provides reminderColorScheme
    ) {
        MaterialTheme(
            colorScheme = defaultColorScheme,
            content = content
        )
    }
}