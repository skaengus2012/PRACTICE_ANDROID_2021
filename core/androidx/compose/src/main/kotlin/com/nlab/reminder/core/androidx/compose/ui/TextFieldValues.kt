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

package com.nlab.reminder.core.androidx.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.debounce

/**
 * @author Doohyun
 */
@Composable
fun rememberDebouncedTextFieldValueState(
    value: String,
    onTextChanged: (String) -> Unit,
    debounceMillis: Long = 500L
): MutableState<TextFieldValue> {
    val debouncedTextFieldState = remember {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }
    LaunchedEffect(onTextChanged, debounceMillis) {
        snapshotFlow { debouncedTextFieldState.value }
            .debounce(debounceMillis)
            .collect { newValue -> onTextChanged(newValue.text) }
    }

    return debouncedTextFieldState
}