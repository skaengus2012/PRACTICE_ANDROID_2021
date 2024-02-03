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

package com.nlab.reminder.domain.feature.schedule.all

import com.nlab.reminder.core.data.model.ScheduleId
import com.nlab.reminder.core.kotlin.collection.minOf
import com.nlab.reminder.core.kotlin.util.getOrThrow
import com.nlab.reminder.core.data.repository.AllScheduleData
import com.nlab.reminder.core.data.repository.CompletedScheduleShownRepository
import com.nlab.reminder.core.data.repository.ScheduleDeleteRequest
import com.nlab.reminder.core.data.repository.ScheduleRepository
import com.nlab.reminder.core.data.repository.ScheduleUpdateRequest
import com.nlab.reminder.core.domain.CalculateItemSwapResultUseCase
import com.nlab.reminder.core.domain.CompleteScheduleWithIdsUseCase
import com.nlab.reminder.core.domain.CompleteScheduleWithMarkUseCase
import com.nlab.reminder.core.domain.FetchLinkMetadataUseCase
import com.nlab.reminder.core.schedule.model.findId
import com.nlab.statekit.middleware.interceptor.Interceptor
import com.nlab.statekit.util.buildDslInterceptor
import javax.inject.Inject

/**
 * @author thalys
 */
class AllScheduleInterceptor @Inject constructor(
    scheduleRepository: ScheduleRepository,
    @AllScheduleData completedScheduleShownRepository: CompletedScheduleShownRepository,
    completeScheduleWithMark: CompleteScheduleWithMarkUseCase,
    completeScheduleWithIds: CompleteScheduleWithIdsUseCase,
    fetchLinkMetadata: FetchLinkMetadataUseCase,
    calculateItemSwapResult: CalculateItemSwapResultUseCase
) : Interceptor<AllScheduleAction, AllScheduleUiState> by buildDslInterceptor(defineDSL = {
    state<AllScheduleUiState.Loaded> {
        action<AllScheduleAction.OnCompletedScheduleVisibilityToggleClicked> { (_, before) ->
            completedScheduleShownRepository
                .setShown(isShown = before.isCompletedScheduleShown.not())
                .getOrThrow()
        }
        // TODO ScheduleId 에 대한 필터를 AOP 형태로 분리
        // https://github.com/skaengus2012/REMINDER_ANDROID/issues/236
        action<AllScheduleAction.OnScheduleCompleteClicked> { (action, before) ->
            completeScheduleWithMark(
                id = before.scheduleElements.findId(action.position) ?: return@action,
                isComplete = action.isComplete
            )
        }
        action<AllScheduleAction.OnSelectedSchedulesCompleteClicked> { (action) ->
            completeScheduleWithIds(action.ids, action.isComplete)
        }
        action<AllScheduleAction.OnScheduleDeleteClicked> { (action, before) ->
            val scheduleId: ScheduleId = before.scheduleElements.getOrNull(action.position)?.id ?: return@action
            scheduleRepository
                .delete(ScheduleDeleteRequest.ById(scheduleId))
                .getOrThrow()
        }
        action<AllScheduleAction.OnSelectedSchedulesDeleteClicked> { (action) ->
            scheduleRepository.delete(ScheduleDeleteRequest.ByIds(action.ids))
                .getOrThrow()
        }
        action<AllScheduleAction.OnCompletedScheduleDeleteClicked> {
            scheduleRepository.delete(ScheduleDeleteRequest.ByComplete(isComplete = true))
                .getOrThrow()
        }

        action<AllScheduleAction.OnScheduleItemMoved> { (action, before) ->
            val swapResult = calculateItemSwapResult(
                items = before.scheduleElements,
                fromPosition = action.fromPosition,
                toPosition = action.toPosition
            )
            if (swapResult.isEmpty()) return@action

            val minVisiblePriority = swapResult.minOf { it.visiblePriority }
            scheduleRepository
                .update(ScheduleUpdateRequest.VisiblePriority(buildMap {
                    swapResult.forEachIndexed { index, scheduleElement ->
                        this[scheduleElement.id] = minVisiblePriority + index
                    }
                }))
                .getOrThrow()
        }
    }
    anyState {
        action<AllScheduleAction.ScheduleElementsLoaded> { (action) ->
            fetchLinkMetadata(action.scheduleElements)
        }
    }
})