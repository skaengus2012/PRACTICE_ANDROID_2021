package com.nlab.reminder.core.domain

import com.nlab.reminder.core.data.model.Link
import com.nlab.reminder.core.data.model.LinkMetadataTable
import com.nlab.reminder.core.data.model.genLink
import com.nlab.reminder.core.data.model.genLinkMetadata
import com.nlab.reminder.core.data.model.genSchedule
import com.nlab.reminder.core.data.model.genSchedules
import com.nlab.reminder.core.data.repository.LinkMetadataTableRepository
import com.nlab.reminder.core.data.repository.ScheduleCompleteMarkRepository
import com.nlab.reminder.core.data.repository.SchedulesStreamRepository
import com.nlab.testkit.genBoolean
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * @author Doohyun
 */
internal class GetScheduleItemUseCaseTest {
    @Test
    fun `Given schedules, Then return scheduleItem including schedules`() = runTest {
        val schedules = genSchedules()
        val useCase = genGetScheduleItemUseCase(
            schedulesStreamRepository = mock {
                whenever(mock.getStream()) doReturn flowOf(schedules)
            }
        )
        assertThat(
            useCase.invoke()
                .take(1)
                .first()
                .map { it.schedule },
            equalTo(schedules)
        )
    }

    @Test
    fun `Given complete marked, Then scheduleItem has marked`() = runTest {
        val schedule = genSchedule(isComplete = genBoolean())
        val useCase = genGetScheduleItemUseCase(
            schedulesStreamRepository = mock {
                whenever(mock.getStream()) doReturn flowOf(listOf(schedule))
            },
            completeMarkRepository = mock {
                whenever(mock.get()) doReturn MutableStateFlow(
                    persistentHashMapOf(
                        schedule.scheduleId to schedule.isComplete.not()
                    )
                )
            }
        )
        assertThat(
            useCase.invoke()
                .take(1)
                .first()
                .first()
                .isCompleteMarked,
            equalTo(schedule.isComplete.not())
        )
    }

    @Test
    fun `Given linkMetadataTable, Then scheduleItem has linkMetadata`() = runTest {
        val link = genLink()
        val linkMetadata = genLinkMetadata()
        val schedule = genSchedule(link = link)
        val useCase = genGetScheduleItemUseCase(
            schedulesStreamRepository = mock {
                whenever(mock.getStream()) doReturn flowOf(listOf(schedule))
            },
            linkMetadataTableRepository = mock {
                whenever(mock.get()) doReturn flowOf(LinkMetadataTable(mapOf(link to linkMetadata)))
            }
        )
        assertThat(
            useCase.invoke()
                .take(1)
                .first()
                .first()
                .linkMetadata,
            equalTo(linkMetadata)
        )
    }

    @Test
    fun `Given schedule link was empty, Then scheduleItem linkMetadata was null`() = runTest {
        val schedule = genSchedule(link = Link.EMPTY)
        val useCase = genGetScheduleItemUseCase(
            schedulesStreamRepository = mock {
                whenever(mock.getStream()) doReturn flowOf(listOf(schedule))
            }
        )
        assertThat(
            useCase.invoke()
                .take(1)
                .first()
                .first()
                .linkMetadata,
            equalTo(null)
        )
    }
}

private fun genGetScheduleItemUseCase(
    schedulesStreamRepository: SchedulesStreamRepository,
    completeMarkRepository: ScheduleCompleteMarkRepository = mock {
        whenever(mock.get()) doReturn MutableStateFlow(persistentHashMapOf())
    },
    linkMetadataTableRepository: LinkMetadataTableRepository = mock {
        whenever(mock.get()) doReturn MutableStateFlow(LinkMetadataTable(emptyMap())) // TODO stateFlow 로 바꾸자..
    }
) = GetScheduleItemUseCase(schedulesStreamRepository, completeMarkRepository, linkMetadataTableRepository)