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

package com.nlab.reminder.core.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.*
import androidx.fragment.app.Fragment

/**
 * Fragment for Compose Composition
 * @author Doohyun
 */
abstract class ComponentFragment : Fragment() {
    private var composeView: ComposeView? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext())
        .apply { setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed) }
        .also { composeView = it }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onViewCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    protected abstract fun onViewCreated(savedInstanceState: Bundle?)

    internal fun requireComposeView(): ComposeView = checkNotNull(composeView)
}