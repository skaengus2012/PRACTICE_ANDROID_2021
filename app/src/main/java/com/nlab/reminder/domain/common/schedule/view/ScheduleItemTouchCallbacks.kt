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

package com.nlab.reminder.domain.common.schedule.view

import android.content.Context
import com.nlab.reminder.R

/**
 * @author thalys
 */
fun ScheduleItemTouchCallback(
    context: Context,
    swipeAnimateDuration: Long = 100L,
    onItemMoved: (fromPosition: Int, toPosition: Int) -> Boolean,
    onItemMoveEnded: () -> Unit
): ScheduleItemTouchCallback = ScheduleItemTouchCallback(
    clampWidth = context.resources.getDimension(R.dimen.schedule_clamp_width),
    swipeAnimateDuration = swipeAnimateDuration,
    onItemMoved = onItemMoved,
    onItemMoveEnded = onItemMoveEnded
)