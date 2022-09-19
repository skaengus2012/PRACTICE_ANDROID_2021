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

package com.nlab.reminder.core.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * @author thalys
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StateMachineHandleBuilderTest {
    @Test
    fun `handle when any event sent`() = runTest {
        testOnceHandleTemplate { action ->
            handled {
                anyEvent {
                    anyState { action() }
                }
            }
        }
    }

    @Test
    fun `handle when specific event sent`() = runTest {
        val fixedEvent: TestEvent = TestEvent.genEvent()
        testOnceHandleTemplate(input = fixedEvent) { action ->
            handled {
                filteredEvent(predicate = { event -> event === fixedEvent }) {
                    anyState { action() }
                }
            }
        }
    }

    @Test
    fun `never handled when not specific event sent`() = runTest {
        val fixedEvent: TestEvent = TestEvent.genEvent()
        testNeverHandleTemplate(input = fixedEvent) { action ->
            handled {
                filteredEvent(predicate = { event -> event !== fixedEvent }) {
                    anyState { action() }
                }
            }
        }
    }

    @Test
    fun `handle when event1 sent`() = runTest {
        testOnceHandleTemplate(input = TestEvent.Event1()) { action ->
            handled {
                event<TestEvent.Event1> {
                    anyState { action() }
                }
            }
        }
    }

    @Test
    fun `never handled when event excluded event1 sent`() = runTest {
        testNeverHandleTemplate(input = TestEvent.Event2()) { action ->
            handled {
                event<TestEvent.Event1> {
                    anyState { action() }
                }
            }
        }
    }

    @Test
    fun `handle when event excluded event1 sent`() = runTest {
        testOnceHandleTemplate(input = TestEvent.Event2()) { action ->
            handled {
                eventNot<TestEvent.Event1> {
                    anyState { action() }
                }
            }
        }
    }

    @Test
    fun `never handled when event1 sent`() = runTest {
        testNeverHandleTemplate(input = TestEvent.Event1()) { action ->
            handled {
                eventNot<TestEvent.Event1> {
                    anyState { action() }
                }
            }
        }
    }

    @Test
    fun `handle when current was any state`() = runTest {
        testOnceHandleTemplate { action ->
            handled {
                anyState {
                    anyEvent { action() }
                }
            }
        }
    }

    @Test
    fun `handle when current was specific state`() = runTest {
        val fixedState: TestState = TestState.genState()
        testOnceHandleTemplate(initState = fixedState) { action ->
            handled {
                filteredState(predicate = { state -> state === fixedState }) {
                    anyEvent { action() }
                }
            }
        }
    }

    @Test
    fun `never handled when current was specific state`() = runTest {
        val fixedState: TestState = TestState.genState()
        testNeverHandleTemplate(initState = fixedState) { action ->
            handled {
                filteredState(predicate = { state -> state !== fixedState }) {
                    anyEvent { action() }
                }
            }
        }
    }

    @Test
    fun `handle when current was init`() = runTest {
        testOnceHandleTemplate(initState = TestState.StateInit()) { action ->
            handled {
                state<TestState.StateInit> {
                    anyEvent { action() }
                }
            }
        }
    }

    @Test
    fun `never handled when current was not init`() = runTest {
        testNeverHandleTemplate(initState = TestState.State1()) { action ->
            handled {
                state<TestState.StateInit> {
                    anyEvent { action() }
                }
            }
        }
    }

    @Test
    fun `handle when current was not init`() = runTest {
        testOnceHandleTemplate(initState = TestState.State1()) { action ->
            handled {
                stateNot<TestState.StateInit> {
                    anyEvent { action() }
                }
            }
        }
    }

    @Test
    fun `never handled when current was init`() = runTest {
        testNeverHandleTemplate(initState = TestState.StateInit()) { action ->
            handled {
                stateNot<TestState.StateInit> {
                    anyEvent { action() }
                }
            }
        }
    }
}