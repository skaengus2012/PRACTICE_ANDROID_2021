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

package com.nlab.reminder.internal.common.data.repository

import com.nlab.reminder.core.kotlin.collection.filter
import com.nlab.reminder.core.kotlin.coroutine.flow.map
import com.nlab.reminder.core.kotlin.util.onSuccess
import com.nlab.reminder.domain.common.data.model.Link
import com.nlab.reminder.domain.common.data.model.LinkMetadata
import com.nlab.reminder.domain.common.data.model.LinkMetadataTable
import com.nlab.reminder.domain.common.data.repository.LinkMetadataRepository
import com.nlab.reminder.domain.common.data.repository.LinkMetadataTableRepository
import com.nlab.reminder.domain.common.data.repository.TimestampRepository
import com.nlab.reminder.internal.common.android.database.LinkMetadataDao
import com.nlab.reminder.internal.common.data.model.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author thalys
 */
internal class LocalLinkMetadataTableRepository @Inject constructor(
    private val linkMetadataDao: LinkMetadataDao,
    private val linkMetadataRepository: LinkMetadataRepository,
    private val coroutineScope: CoroutineScope,
    private val timestampRepository: TimestampRepository
) : LinkMetadataTableRepository {
    override fun fetch(links: List<Link>) {
        links.filter(Link::isValid).forEach { link ->
            coroutineScope.launch { cachingLink(link) }
        }
    }

    private suspend fun cachingLink(link: Link) {
        linkMetadataRepository.get(link).onSuccess { linkMetadata ->
            if (linkMetadata.isCacheable()) {
                linkMetadataDao.insertAndClearOldData(
                    linkMetadata.toEntity(link, timestampRepository.get())
                )
            }
        }
    }

    override fun get(): Flow<LinkMetadataTable> =
        linkMetadataDao.findAsStream().map { entities ->
            LinkMetadataTable(entities.associateBy(
                keySelector = { Link(it.link) },
                valueTransform = { LinkMetadata(it.title, it.imageUrl) }
            ))
        }
}

private fun Link.isValid(): Boolean = value.isNotBlank()
private fun LinkMetadata.isCacheable(): Boolean = title.isNotBlank() || imageUrl.isNotBlank()