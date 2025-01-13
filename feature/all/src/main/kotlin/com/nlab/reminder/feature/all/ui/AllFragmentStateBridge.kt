/*
 * Copyright (C) 2024 The N's lab Open Source Project
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

package com.nlab.reminder.feature.all.ui

import androidx.compose.runtime.*
import com.nlab.reminder.core.component.schedule.ui.view.list.SimpleEdit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author Thalys
 */
@Stable
internal class AllFragmentStateBridge(
    testFlow: Flow<Boolean> = MutableStateFlow(false), // TODO replace UiState
    isToolbarTitleVisible: Boolean,
    toolbarBackgroundAlpha: Float,
    val onSimpleEdited: (SimpleEdit) -> Unit
) {
    var isToolbarTitleVisible: Boolean by mutableStateOf(isToolbarTitleVisible)
    var toolbarBackgroundAlpha: Float by mutableFloatStateOf(toolbarBackgroundAlpha)

    val itemSelectionEnabled: Flow<Boolean> = testFlow
}

@Composable
internal fun rememberAllFragmentStateBridge(
    testFlow: Flow<Boolean> = MutableStateFlow(false),
    isToolbarTitleVisible: Boolean,
    toolbarBackgroundAlpha: Float,
    onSimpleEdited: (SimpleEdit) -> Unit,
): AllFragmentStateBridge = remember(testFlow, onSimpleEdited) {
    AllFragmentStateBridge(
        testFlow = testFlow,
        isToolbarTitleVisible = isToolbarTitleVisible,
        toolbarBackgroundAlpha = toolbarBackgroundAlpha,
        onSimpleEdited = onSimpleEdited
    )
}