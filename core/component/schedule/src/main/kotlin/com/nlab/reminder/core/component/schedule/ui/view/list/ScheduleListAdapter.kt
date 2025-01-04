/*
 * Copyright (C) 2025 The N's lab Open Source Project
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

package com.nlab.reminder.core.component.schedule.ui.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.nlab.reminder.core.component.schedule.databinding.LayoutScheduleAdapterItemContentBinding
import com.nlab.reminder.core.component.schedule.databinding.LayoutScheduleAdapterItemHeadlineBinding

private const val ITEM_VIEW_TYPE_HEADLINE = 1
private const val ITEM_VIEW_TYPE_CONTENT = 2

/**
 * @author Thalys
 */
class ScheduleListAdapter(
    private val theme: ScheduleListTheme
) : ListAdapter<ScheduleAdapterItem, ScheduleAdapterItemViewHolder>(ScheduleAdapterItemDiffCallback()) {
    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ScheduleAdapterItem.Headline -> ITEM_VIEW_TYPE_HEADLINE
        is ScheduleAdapterItem.Content -> ITEM_VIEW_TYPE_CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapterItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADLINE -> {
                ScheduleHeadlineViewHolder(
                    binding = LayoutScheduleAdapterItemHeadlineBinding.inflate(
                        layoutInflater,
                        parent,
                        /* attachToParent = */ false
                    ),
                    theme = theme
                )
            }

            ITEM_VIEW_TYPE_CONTENT -> {
                ScheduleContentViewHolder(
                    binding = LayoutScheduleAdapterItemContentBinding.inflate(
                        layoutInflater,
                        parent,
                        /* attachToParent = */ false
                    ),
                    theme = theme
                )
            }

            else -> {
                throw IllegalArgumentException("Unknown view type: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: ScheduleAdapterItemViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is ScheduleHeadlineViewHolder -> holder.bind(item as ScheduleAdapterItem.Headline)
            is ScheduleContentViewHolder -> holder.bind(item as ScheduleAdapterItem.Content)
        }
    }
}