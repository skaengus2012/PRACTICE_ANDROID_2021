package com.nlab.reminder.core.data.model/*
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

import com.nlab.testkit.faker.genBlank
import com.nlab.testkit.faker.genBothify
import com.nlab.testkit.faker.genNumerify
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

/**
 * @author thalys
 */
class LinkKtTest {
    @Test
    fun testIsEmpty() {
        val emptyLink = Link(genBlank())
        assert(emptyLink.isEmpty())

        val notEmptyLink = Link(genNumerify())
        assert(notEmptyLink.isEmpty().not())
    }

    @Test
    fun testIsNotEmpty() {
        val emptyLink = Link(genBlank())
        assert(emptyLink.isNotEmpty().not())

        val notEmptyLink = Link(genNumerify())
        assert(notEmptyLink.isNotEmpty())
    }

    @Test
    fun testOrEmpty() {
        val nullInstance: Link? = null
        assertThat(nullInstance.orEmpty(), equalTo(Link.EMPTY))

        val notNullInstance = Link(genBothify())
        assertThat(notNullInstance.orEmpty(), equalTo(notNullInstance))
    }
}