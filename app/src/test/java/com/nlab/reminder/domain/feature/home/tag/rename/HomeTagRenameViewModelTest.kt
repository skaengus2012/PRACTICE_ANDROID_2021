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

package com.nlab.reminder.domain.feature.home.tag.rename

import com.nlab.reminder.test.createMockingViewModelComponent
import com.nlab.reminder.test.genBothify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * @author Doohyun
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeTagRenameViewModelTest {
    private fun createViewModel(initText: String = ""): HomeTagRenameViewModel {
        return HomeTagRenameViewModel(HomeTagRenameStateMachineFactory(initText))
    }

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `notify event to stateMachine when viewModel event invoked`() {
        val (viewModel, stateMachine) = createMockingViewModelComponent(
            MutableStateFlow(genHomeTagRenameState()),
            createViewModel = { HomeTagRenameViewModel(it) },
            wheneverMocking = { factory: HomeTagRenameStateMachineFactory ->
                factory.create(scope = any(), homeTagRenameSideEffect = any())
            }
        )
        val event = HomeTagRenameEvent.OnKeyboardShownWhenViewCreated
        viewModel.invoke(event)
        verify(stateMachine, times(1)).send(event)
    }

    @Test
    fun `notify changed state when state event sent`() = runTest {
        val actualHomeRenameState = mutableListOf<HomeTagRenameState>()
        val initText = genBothify()
        val expectedInitState = genHomeTagRenameState(initText, isKeyboardShowWhenViewCreated = true)
        val viewModel: HomeTagRenameViewModel = createViewModel(initText)
        CoroutineScope(Dispatchers.Unconfined).launch { viewModel.state.collect(actualHomeRenameState::add) }
        viewModel.onKeyboardShownWhenViewCreated()
        assertThat(
            actualHomeRenameState,
            equalTo(buildList {
                add(expectedInitState)
                add(expectedInitState.copy(isKeyboardShowWhenViewCreated = false))
            })
        )
    }

    @Test
    fun `notify sideEffect message when sideEffect event sent`() = runTest {
        val inputText = genBothify()
        val viewModel: HomeTagRenameViewModel = createViewModel()
        viewModel.onRenameTextInput(inputText)
        viewModel.onConfirmClicked()
        viewModel.onRenameTextClearClicked()
        viewModel.onConfirmClicked()
        viewModel.onCancelClicked()
        assertThat(
            viewModel.homeTagRenameSideEffect
                .event
                .take(3)
                .toList(),
            equalTo(
                listOf(
                    HomeTagRenameSideEffectMessage.Complete(inputText),
                    HomeTagRenameSideEffectMessage.Complete(rename = ""),
                    HomeTagRenameSideEffectMessage.Dismiss,
                )
            )
        )
    }
}