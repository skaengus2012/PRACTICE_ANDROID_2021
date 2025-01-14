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

package com.nlab.reminder.core.component.usermessage.handle.impl

import com.nlab.reminder.core.component.usermessage.FeedbackPriority
import com.nlab.reminder.core.component.usermessage.UserMessage
import com.nlab.reminder.core.text.genUiText
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.assertFlowEmissionsLazy
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * @author Thalys
 */
class UserMessageBroadcastMonitorTest {
    @Test
    fun `Given user message, When send after subscribe, Then monitor send user message`() = runTest {
        val userMessage = UserMessage(
            message = genUiText(),
            priority = FeedbackPriority.HIGH
        )
        val userMessageProvider = UserMessageBroadcastMonitor()
        val assertion = assertFlowEmissionsLazy(userMessageProvider.message, listOf(userMessage))
        userMessageProvider.send(userMessage)
        assertion()
    }

    @Test
    fun `Given user message, When send before subscribe, Then monitor not send user message`() = runTest {
        val userMessage = UserMessage(
            message = genUiText(),
            priority = FeedbackPriority.URGENT
        )
        val userMessageProvider = UserMessageBroadcastMonitor()
        userMessageProvider.send(userMessage)

        val assertion = assertFlowEmissionsLazy(userMessageProvider.message, emptyList())
        advanceUntilIdle()
        assertion()
    }
}