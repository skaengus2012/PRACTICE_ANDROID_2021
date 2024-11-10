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

package com.nlab.reminder.core.data.model

import com.nlab.reminder.core.local.database.model.LinkMetadataEntity
import com.nlab.testkit.faker.genLongGreaterThanZero

/**
 * @author Doohyun
 */
fun genLinkAndMetadataAndEntity(
    link: Link = genLink(),
    linkMetadata: LinkMetadata = genLinkMetadata(),
    timestamp: Long = genLongGreaterThanZero()
): Triple<Link, LinkMetadata, LinkMetadataEntity> = Triple(
    link,
    linkMetadata,
    LinkMetadataEntity(
        link = link.value,
        title = linkMetadata.title,
        imageUrl = linkMetadata.imageUrl,
        timestamp = timestamp
    )
)