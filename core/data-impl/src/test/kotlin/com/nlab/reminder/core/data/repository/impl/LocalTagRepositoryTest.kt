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

package com.nlab.reminder.core.data.repository.impl

import com.nlab.reminder.core.data.local.database.toEntities
import com.nlab.reminder.core.data.model.Tag
import com.nlab.reminder.core.data.model.genTag
import com.nlab.reminder.core.data.model.genTagId
import com.nlab.reminder.core.data.model.genTags
import com.nlab.reminder.core.data.repository.TagRepository
import com.nlab.reminder.core.kotlin.Result
import com.nlab.reminder.core.local.database.ScheduleTagListDao
import com.nlab.reminder.core.local.database.TagDao
import com.nlab.reminder.core.local.database.TagEntity
import com.nlab.testkit.faker.genBothify
import com.nlab.testkit.faker.genLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.once
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * @author Doohyun
 */
internal class LocalTagRepositoryTest {
    @Test
    fun `Get tags from dao`() {
        val tagDao: TagDao = mock {
            whenever(mock.getAsStream()) doReturn emptyFlow()
        }
        genTagRepository(tagDao = tagDao).getStream()
        verify(tagDao, once()).getAsStream()
    }

    @Test
    fun `Notify tag list when dao updated`() = runTest {
        val firstTags: List<Tag> = listOf(genTag())
        val secondTags: List<Tag> = genTags().sortedBy { it.name }.reversed()
        val tagDao: TagDao = mock {
            val mockFlow = flow {
                emit(firstTags.toEntities())
                emit(secondTags.toEntities())
            }
            whenever(mock.getAsStream()) doReturn mockFlow
        }
        val tagFlow: Flow<List<Tag>> = genTagRepository(tagDao = tagDao).getStream()
        val actualTags = buildList<List<Tag>> {
            repeat(times = 2) { index ->
                this += tagFlow.drop(count = index).first()
            }
        }
        assertThat(
            actualTags,
            equalTo(listOf(firstTags, secondTags))
        )
    }

    @Test
    fun `Repository get usage count from scheduleTagListDao`() = runTest {
        val id = genTagId()
        val usageCount: Long = genLong()
        val scheduleTagListDao: ScheduleTagListDao = mock {
            whenever(mock.findTagUsageCount(id.value)) doReturn usageCount
        }
        val result = genTagRepository(scheduleTagListDao = scheduleTagListDao).getUsageCount(id)

        assertThat(
            result,
            equalTo(Result.Success(usageCount))
        )
    }

    @Test
    fun `Repository return error when scheduleTagListDao occurred error while find by usage count`() = runTest {
        val exception = RuntimeException()
        val scheduleTagListDao: ScheduleTagListDao = mock { whenever(mock.findTagUsageCount(any())) doThrow exception }
        val result = genTagRepository(scheduleTagListDao = scheduleTagListDao).getUsageCount(genTagId())

        assertThat(
            result,
            equalTo(Result.Failure(exception))
        )
    }

    @Test
    fun `TagDao updated when repository update name requested`() = runTest {
        val id = genTagId()
        val name = genBothify()
        val tagDao: TagDao = mock()

        genTagRepository(tagDao = tagDao).updateName(id, name)
        verify(tagDao, once()).update(TagEntity(id.value, name))
    }

    @Test
    fun `TagDao return error when repository update name requested`() = runTest {
        val exception = RuntimeException()
        val tagDao: TagDao = mock { whenever(mock.update(any())) doThrow exception }
        val result = genTagRepository(tagDao = tagDao).updateName(genTagId(), genBothify())

        assertThat(
            result,
            equalTo(Result.Failure(exception))
        )
    }

    @Test
    fun `TagDao delete tag when repository deleting requested`() = runTest {
        val id = genTagId()
        val tagDao: TagDao = mock()

        genTagRepository(tagDao = tagDao).delete(id)
        verify(tagDao, once()).deleteById(id.value)
    }

    @Test
    fun `TagDao return error when repository deleting requested`() = runTest {
        val exception = RuntimeException()
        val tagDao: TagDao = mock { whenever(mock.deleteById(any())) doThrow exception }
        val result = genTagRepository(tagDao = tagDao).delete(genTagId())

        assertThat(
            result,
            equalTo(Result.Failure(exception))
        )
    }
}

private fun genTagRepository(
    tagDao: TagDao = mock(),
    scheduleTagListDao: ScheduleTagListDao = mock()
): TagRepository = LocalTagRepository(tagDao, scheduleTagListDao)