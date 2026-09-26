package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.HabitEntity
import com.example.data.model.HabitFrequencyType
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray800
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.DayForecast
import com.example.ui.viewmodel.HabitViewModel
import com.example.util.KarachiTimeHelper

@Composable
fun ScheduleScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val forecast by viewModel.weekForecast.collectAsStateWithLifecycle()
    val allHabits by viewModel.allHabits.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("schedule_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        item {
            Column {
                Text(
                    text = "Recurring Schedule",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Dynamic scheduling rules aligned to Karachi GMT+5",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray400
                )
            }
        }

        // Variable Scheduling Rules Cards (Minimalist Monochrome)
        item {
            Text(
                text = "Cadence Rules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RuleItem(
                    title = "Running",
                    rule = "Active on Saturday & Sunday mornings",
                    icon = Icons.Default.DirectionsRun
                )
                RuleItem(
                    title = "Eyebrows Setting",
                    rule = "Every 3 days based on Karachi calendar anchor",
                    icon = Icons.Default.EventRepeat
                )
                RuleItem(
                    title = "Keep Beard Trimmed",
                    rule = "Every 3 days for grooming consistency",
                    icon = Icons.Default.Face
                )
                RuleItem(
                    title = "Haircut",
                    rule = "Weekly scheduled cycle",
                    icon = Icons.Default.ContentCut
                )
            }
        }

        // 7-Day Visual Forecast
        item {
            Text(
                text = "7-Day Forecast",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Upcoming scheduled habits by day",
                style = MaterialTheme.typography.bodySmall,
                color = Gray400
            )
        }

        items(forecast, key = { it.date.toString() }) { day ->
            ForecastDayCard(forecast = day)
        }

        // All Habits & Custom Rules Management
        item {
            Text(
                text = "Habit Registry (${allHabits.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(allHabits, key = { it.id }) { habit ->
            HabitRegistryItem(
                habit = habit,
                onDelete = if (!habit.isStandard) { { viewModel.deleteHabit(habit) } } else null
            )
        }
    }
}

@Composable
private fun RuleItem(
    title: String,
    rule: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gray800),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = rule,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
            }
        }
    }
}

@Composable
private fun ForecastDayCard(forecast: DayForecast) {
    val dateString = KarachiTimeHelper.formatDisplayDate(forecast.date)
    val isToday = forecast.dayName == "Today"

    val specialRecurringHabits = forecast.scheduledHabits.filter {
        it.frequencyType != HabitFrequencyType.DAILY
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            1.dp,
            if (isToday) PureWhite else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isToday) PureWhite else Gray800
                    ) {
                        Text(
                            text = forecast.dayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) PureBlack else Gray300,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray400
                    )
                }

                Text(
                    text = "${forecast.scheduledHabits.size} Habits",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite
                )
            }

            if (specialRecurringHabits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(specialRecurringHabits) { habit ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Gray800,
                            border = BorderStroke(1.dp, Gray700)
                        ) {
                            Text(
                                text = "• ${habit.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Normal,
                                color = Gray300,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitRegistryItem(
    habit: HabitEntity,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${habit.category.displayName} • ${habit.frequencyType.label} ${if (habit.frequencyType == HabitFrequencyType.EVERY_N_DAYS) "(${habit.intervalDays}d)" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }

            if (habit.isStandard) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Gray800
                ) {
                    Text(
                        text = "Standard",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray400,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else if (onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Habit",
                        tint = Gray400
                    )
                }
            }
        }
    }
}
