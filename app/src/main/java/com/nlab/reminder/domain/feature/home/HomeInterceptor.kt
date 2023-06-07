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

package com.nlab.reminder.domain.feature.home

import com.nlab.reminder.core.kotlin.util.getOrThrow
import com.nlab.reminder.core.kotlin.util.onFailure
import com.nlab.reminder.core.kotlin.util.onSuccess
import com.nlab.reminder.domain.common.data.repository.TagRepository
import com.nlab.statekit.middleware.interceptor.Interceptor
import com.nlab.statekit.util.buildDslInterceptor
import javax.inject.Inject

/**
 * @author Doohyun
 */
internal class HomeInterceptor @Inject constructor(
    private val tagRepository: TagRepository
) : Interceptor<HomeAction, HomeUiState> by buildDslInterceptor(defineDSL = {
    state<HomeUiState.Success> {
        action<HomeAction.OnTagLongClicked> { (action) ->
            tagRepository.getUsageCount(action.tag)
                .onSuccess { usageCount ->
                    dispatch(HomeAction.TagConfigMetadataLoaded(action.tag, usageCount.value))
                }
                .onFailure { e -> dispatch(HomeAction.ErrorOccurred(e)) }
                .getOrThrow()
        }
    }
})