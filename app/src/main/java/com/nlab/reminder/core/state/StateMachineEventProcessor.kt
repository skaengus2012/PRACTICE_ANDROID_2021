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

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.plus

/**
 * @author thalys
 */
class StateMachineEventProcessor<E : Event, S : State> internal constructor(
    scope: CoroutineScope,
    stateReduce: StateReduce<E, S>,
    exceptionHandlers: List<(Throwable) -> Unit>,
    eventHandler: suspend (EventProcessor<E>).(UpdateSource<E, S>) -> Unit,
) : EventProcessor<E> {
    private val internalActionProcessor = EventProcessorImpl<E>(
        scope = scope + CoroutineExceptionHandler { _, e -> exceptionHandlers.forEach { it(e) } },
        onEventReceived = { event -> eventHandler(stateReduce.getSourceAndUpdate(event)) }
    )

    override fun send(event: E): Job = internalActionProcessor.send(event)
}