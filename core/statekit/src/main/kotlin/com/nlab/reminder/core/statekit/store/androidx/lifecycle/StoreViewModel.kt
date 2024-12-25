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

package com.nlab.reminder.core.statekit.store.androidx.lifecycle

import androidx.lifecycle.ViewModel
import com.nlab.statekit.store.Store
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

/**
 * @author Doohyun
 */
abstract class StoreViewModel<A : Any, S : Any> : ViewModel() {
    private val store: Store<A, S> by lazy(LazyThreadSafetyMode.NONE) { onCreateStore() }
    val uiState: StateFlow<S> get() = store.state

    fun dispatch(action: A): Job = store.dispatch(action)
    protected abstract fun onCreateStore(): Store<A, S>
}