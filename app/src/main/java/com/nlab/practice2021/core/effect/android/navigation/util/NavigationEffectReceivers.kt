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

package com.nlab.practice2021.core.effect.android.navigation.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.nlab.practice2021.core.effect.android.navigation.NavigationEffect
import com.nlab.practice2021.core.effect.android.navigation.NavigationEffectReceiver
import com.nlab.practice2021.core.effect.android.navigation.NavigationMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * @author Doohyun
 */
inline fun NavigationEffectReceiver(
    crossinline lifecycleOwner: () -> LifecycleOwner,
    crossinline onNavigationMessageReceived: (NavigationMessage) -> Unit
): NavigationEffectReceiver = object : NavigationEffectReceiver {
    override fun observeEvent(navigationEffect: NavigationEffect) {
        navigationEffect.event
            .flowWithLifecycle(lifecycleOwner().lifecycle)
            .onEach { onNavigationMessageReceived(it) }
            .launchIn(lifecycleOwner().lifecycleScope)
    }
}