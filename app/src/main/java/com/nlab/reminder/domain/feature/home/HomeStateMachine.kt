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

package com.nlab.reminder.domain.feature.home

import com.nlab.reminder.core.effect.android.navigation.SendNavigationEffect
import com.nlab.reminder.core.state.StateMachine
import com.nlab.reminder.core.state.util.StateMachine
import com.nlab.reminder.domain.common.effect.android.navigation.navigateAllEnd
import com.nlab.reminder.domain.common.effect.android.navigation.navigateTagEnd
import com.nlab.reminder.domain.common.effect.android.navigation.navigateTimetableEnd
import com.nlab.reminder.domain.common.effect.android.navigation.navigateTodayEnd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

typealias HomeStateMachine = StateMachine<HomeAction, HomeState>

/**
 * @author Doohyun
 */
@Suppress("FunctionName")
fun HomeStateMachine(
    scope: CoroutineScope,
    initState: HomeState,
    navigationEffect: SendNavigationEffect,
    getHomeSummary: GetHomeSummaryUseCase,
    homeStateLoadedFactory: HomeStateLoadedFactory,
    onHomeSummaryLoaded: (HomeSummary) -> Unit
): HomeStateMachine = StateMachine(scope, initState) {
    updateTo { (action, oldState) ->
        when (action) {
            is HomeAction.Fetch -> {
                if (oldState is HomeState.Init) HomeState.Loading
                else oldState
            }
            is HomeAction.HomeSummaryRefreshed -> {
                homeStateLoadedFactory.create(action.homeSummary)
            }
            else -> oldState
        }
    }

    withSideEffect<HomeAction.Fetch> {
        scope.launch { getHomeSummary().collect { onHomeSummaryLoaded(it) } }
    }

    withSideEffect<HomeAction.OnTodayCategoryClicked> {
        scope.launch { navigationEffect.navigateTodayEnd() }
    }

    withSideEffect<HomeAction.OnTimetableCategoryClicked> {
        scope.launch { navigationEffect.navigateTimetableEnd() }
    }

    withSideEffect<HomeAction.OnAllCategoryClicked> {
        scope.launch { navigationEffect.navigateAllEnd() }
    }

    withSideEffect<HomeAction.OnTagClicked> { (action) ->
        scope.launch { navigationEffect.navigateTagEnd(action.tag) }
    }
}