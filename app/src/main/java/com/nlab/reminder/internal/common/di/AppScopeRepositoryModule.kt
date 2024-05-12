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

package com.nlab.reminder.internal.common.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.nlab.reminder.core.data.repository.AllScheduleData
import com.nlab.reminder.core.data.repository.CompletedScheduleShownRepository
import com.nlab.reminder.core.data.repository.ScheduleRepository
import com.nlab.reminder.core.data.repository.TagRepository
import com.nlab.reminder.internal.common.android.datastore.PreferenceKeys
import com.nlab.reminder.internal.data.repository.LocalCompletedScheduleShownRepository
import com.nlab.reminder.internal.data.repository.LocalScheduleRepository
import com.nlab.reminder.internal.data.repository.LocalTagRepository
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author thalys
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AppScopeRepositoryModule {
    @Reusable
    @Binds
    abstract fun bindScheduleRepository(repository: LocalScheduleRepository): ScheduleRepository

    @Reusable
    @Binds
    abstract fun bindTagRepository(repository: LocalTagRepository): TagRepository

    companion object {

        @AllScheduleData
        @Reusable
        @Provides
        fun provideCompletedScheduleShownAllScopeRepository(
            dataStore: DataStore<Preferences>
        ): CompletedScheduleShownRepository = LocalCompletedScheduleShownRepository(
            dataStore,
            booleanPreferencesKey(PreferenceKeys.PREFERENCE_KEY_ALL_SCHEDULE_COMPLETE_SHOWN)
        )
    }
}