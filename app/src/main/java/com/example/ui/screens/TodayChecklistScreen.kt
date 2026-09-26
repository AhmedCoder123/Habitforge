package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HabitCategory
import com.example.ui.components.AddHabitDialog
import com.example.ui.components.CloudSyncCard
import com.example.ui.components.HabitCard
import com.example.ui.components.KarachiClockHeader
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray800
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.HabitViewModel

@Composable
fun TodayChecklistScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val karachiTime by viewModel.karachiTime.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
    val countdownSeconds by viewModel.countdownSeconds.collectAsStateWithLifecycle()
    val todayHabits by viewModel.todayHabits.collectAsStateWithLifecycle()

    var selectedCategoryFilter by remember { mutableStateOf<HabitCategory?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showUnscheduledSection by remember { mutableStateOf(false) }

    val scheduledList = todayHabits.filter { it.isScheduledToday }
    val unscheduledList = todayHabits.filter { !it.isScheduledToday }

    val filteredScheduled = if (selectedCategoryFilter != null) {
        scheduledList.filter { it.habit.category == selectedCategoryFilter }
    } else {
        scheduledList
    }

    val completedCount = scheduledList.count { it.isGoalReached }
    val totalCount = scheduledList.size
    val completionFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val completionPercent = (completionFraction * 100).toInt()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Karachi Live Clock & Countdown Header
            item {
                KarachiClockHeader(
                    karachiTime = karachiTime,
                    currentDate = currentDate,
                    countdownSeconds = countdownSeconds,
                    onSimulateReset = { viewModel.simulateResetNow() }
                )
            }

            // 2. Today's Adherence Scorecard (Monochrome Minimalist)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("today_adherence_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S PROGRESS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray400,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "$completedCount of $totalCount Done",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = Gray800,
                                border = BorderStroke(1.5.dp, if (completionPercent >= 80) PureWhite else Gray700)
                            ) {
                                Box(
                                    modifier = Modifier.size(52.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$completionPercent%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { completionFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PureWhite,
                            trackColor = Gray800
                        )
                    }
                }
            }

            // Cloud Firestore Sync Card
            item {
                CloudSyncCard(viewModel = viewModel)
            }

            // 3. Category Filter Chips (Monochrome Minimalist)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selectedCategoryFilter == null) PureWhite else Gray800,
                            border = BorderStroke(1.dp, if (selectedCategoryFilter == null) PureWhite else Gray700),
                            modifier = Modifier.testTag("filter_all"),
                            onClick = { selectedCategoryFilter = null }
                        ) {
                            Text(
                                text = "All (${scheduledList.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedCategoryFilter == null) PureBlack else Gray300,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }

                    items(HabitCategory.entries.toTypedArray()) { cat ->
                        val count = scheduledList.count { it.habit.category == cat }
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PureWhite else Gray800,
                            border = BorderStroke(1.dp, if (isSelected) PureWhite else Gray700),
                            modifier = Modifier.testTag("filter_${cat.name.lowercase()}"),
                            onClick = { selectedCategoryFilter = if (isSelected) null else cat }
                        ) {
                            Text(
                                text = "${cat.displayName} ($count)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) PureBlack else Gray300,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Section Header: Scheduled Today
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scheduled Today",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${filteredScheduled.size} items",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gray400
                    )
                }
            }

            // Active Today's Habit Cards
            items(filteredScheduled, key = { it.habit.id }) { item ->
                HabitCard(
                    item = item,
                    onToggle = { viewModel.toggleHabit(item.habit.id) },
                    onProgressChange = { progress -> viewModel.updateProgress(item.habit.id, progress) }
                )
            }

            // Section: Variable & Recurring Habits Not Scheduled for Today
            if (unscheduledList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        onClick = { showUnscheduledSection = !showUnscheduledSection }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Resting Habits (${unscheduledList.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Recurring tasks resting today per interval",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Gray500
                                )
                            }
                            Icon(
                                imageVector = if (showUnscheduledSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle unscheduled",
                                tint = Gray400
                            )
                        }
                    }
                }

                if (showUnscheduledSection) {
                    items(unscheduledList, key = { "unscheduled_${it.habit.id}" }) { item ->
                        HabitCard(
                            item = item,
                            onToggle = { viewModel.toggleHabit(item.habit.id) },
                            onProgressChange = { progress -> viewModel.updateProgress(item.habit.id, progress) },
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }

        // Minimalist Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_habit_fab"),
            containerColor = PureWhite,
            contentColor = PureBlack,
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit", tint = PureBlack)
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, frequencyType, intervalDays, inputType, targetCount, unit, iconName, reminderTime ->
                viewModel.addCustomHabit(
                    title = title,
                    category = category,
                    frequencyType = frequencyType,
                    intervalDays = intervalDays,
                    inputType = inputType,
                    targetCount = targetCount,
                    unit = unit,
                    iconName = iconName,
                    reminderTime = reminderTime
                )
                showAddDialog = false
            }
        )
    }
}
