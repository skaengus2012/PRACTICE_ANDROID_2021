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

package com.nlab.reminder.internal.common.di

import com.nlab.reminder.core.kotlin.coroutine.util.Delay
import com.nlab.reminder.core.util.transaction.TransactionIdGenerator
import com.nlab.reminder.domain.common.schedule.CompleteMarkRepository
import com.nlab.reminder.domain.common.schedule.ScheduleRepository
import com.nlab.reminder.domain.common.schedule.ScheduleUiStateFlowFactory
import com.nlab.reminder.domain.common.schedule.UpdateCompleteUseCase
import com.nlab.reminder.domain.common.schedule.impl.DefaultScheduleUiStateFlowFactory
import com.nlab.reminder.domain.common.schedule.impl.DefaultUpdateCompleteUseCase
import com.nlab.reminder.domain.common.schedule.impl.ScopedCompleteMarkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers

/**
 * @author Doohyun
 */
@Module
@InstallIn(ViewModelComponent::class)
class ScheduleModule {
    @ViewModelScoped
    @Provides
    fun provideCompleteMarkRepository(): CompleteMarkRepository = ScopedCompleteMarkRepository()

    @Provides
    fun provideScheduleUiStateFlowFactory(
        completeMarkRepository: CompleteMarkRepository
    ): ScheduleUiStateFlowFactory = DefaultScheduleUiStateFlowFactory(completeMarkRepository)

    @Provides
    fun provideUpdateScheduleCompleteUseCase(
        transactionIdGenerator: TransactionIdGenerator,
        scheduleRepository: ScheduleRepository,
        completeMarkRepository: CompleteMarkRepository
    ): UpdateCompleteUseCase = DefaultUpdateCompleteUseCase(
        transactionIdGenerator,
        scheduleRepository,
        completeMarkRepository,
        delayUntilTransactionPeriod = Delay(timeMillis = 1_000),
        dispatcher = Dispatchers.Default
    )
}