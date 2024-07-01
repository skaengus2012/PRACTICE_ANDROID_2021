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

import com.nlab.reminder.core.state.UserMessage
import com.nlab.reminder.core.annotation.test.ExcludeGeneratedFromTestReport
import com.nlab.reminder.core.data.model.Tag
import com.nlab.statekit.Action
import com.nlab.statekit.lifecycle.viewmodel.ContractUiAction

/**
 * @author Doohyun
 */
internal sealed interface HomeAction : Action {
    data class SummaryLoaded(
        val todaySchedulesCount: Long,
        val timetableSchedulesCount: Long,
        val allSchedulesCount: Long,
        val tags: List<Tag>,
    ) : HomeAction

    @ContractUiAction
    data object CompleteWorkflow : HomeAction

    @ContractUiAction
    data class UserMessageShown(val shownMessage: UserMessage) : HomeAction

    @ExcludeGeneratedFromTestReport
    data class ErrorOccurred(val throwable: Throwable) : HomeAction

    @ContractUiAction
    data object OnTodayCategoryClicked : HomeAction

    @ContractUiAction
    data object OnTimetableCategoryClicked : HomeAction

    @ContractUiAction
    object OnAllCategoryClicked : HomeAction

    @ContractUiAction
    data class OnTagLongClicked(val tag: Tag) : HomeAction

    data class TagConfigMetadataLoaded(val tag: Tag, val usageCount: Long) : HomeAction

    @ContractUiAction
    data object OnTagRenameRequestClicked : HomeAction

    @ContractUiAction
    data object OnTagRenameInputKeyboardShown : HomeAction

    @ContractUiAction
    data object OnTagRenameConfirmClicked : HomeAction

    @ContractUiAction
    data class OnTagRenameInputted(val text: String) : HomeAction

    @ContractUiAction
    data object OnTagDeleteRequestClicked : HomeAction

    @ContractUiAction
    data object OnTagDeleteConfirmClicked : HomeAction
}