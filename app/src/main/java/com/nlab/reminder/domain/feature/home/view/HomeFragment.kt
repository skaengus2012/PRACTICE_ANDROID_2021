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

package com.nlab.reminder.domain.feature.home.view

import android.os.Bundle
import androidx.navigation.fragment.findNavController

import dagger.hilt.android.AndroidEntryPoint
import com.nlab.reminder.core.android.designsystem.theme.ReminderTheme
import com.nlab.reminder.core.android.fragment.ComponentFragment
import com.nlab.reminder.core.android.fragment.setContent
import com.nlab.reminder.domain.common.android.navigation.navigateToAllScheduleEnd

/**
 * @author Doohyun
 */
@AndroidEntryPoint
internal class HomeFragment : ComponentFragment() {
    override fun onViewCreated(savedInstanceState: Bundle?) {
        val navController = findNavController()
        setContent {
            ReminderTheme {
                HomeRoot(
                    navigateToAllScheduleEnd = navController::navigateToAllScheduleEnd
                )
            }
        }
    }
}