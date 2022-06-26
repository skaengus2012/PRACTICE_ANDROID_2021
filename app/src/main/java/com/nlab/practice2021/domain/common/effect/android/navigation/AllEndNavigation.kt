/*
 * Copyright (C) 2022 The N's lab Open Source Project
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

package com.nlab.practice2021.domain.common.effect.android.navigation

import android.util.Log
import androidx.navigation.NavController
import com.nlab.practice2021.core.effect.android.navigation.NavigationMessage
import com.nlab.practice2021.core.effect.android.navigation.SendNavigationEffect

/**
 * @author Doohyun
 */
object AllEndNavigationMessage : NavigationMessage

suspend fun SendNavigationEffect.navigateAllEnd() = send(AllEndNavigationMessage)

class AllEndNavigationEffectRunner {
    operator fun invoke(navController: NavController) {
        Log.w("TODO", "navigate all")
    }
}