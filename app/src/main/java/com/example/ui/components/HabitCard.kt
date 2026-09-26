package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitFrequencyType
import com.example.data.model.HabitInputType
import com.example.data.model.HabitWithProgress
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray800
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite

@Composable
fun HabitCard(
    item: HabitWithProgress,
    onToggle: () -> Unit,
    onProgressChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = item.isGoalReached
    val habit = item.habit

    val cardBg by animateColorAsState(
        targetValue = if (isCompleted) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardBg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(
            1.dp,
            if (isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Info Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = habit.inputType == HabitInputType.CHECKBOX) { onToggle() }
                ) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (isCompleted) Gray500 else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isCompleted && habit.inputType == HabitInputType.CHECKBOX) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (habit.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray500
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Minimal Metadata Badges (Monochrome)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = habit.category.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Gray400,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        val freqLabel = when (habit.frequencyType) {
                            HabitFrequencyType.DAILY -> "DAILY"
                            HabitFrequencyType.EVERY_N_DAYS -> "EVERY ${habit.intervalDays}D"
                            HabitFrequencyType.WEEKEND_ONLY -> "SAT & SUN"
                            HabitFrequencyType.WEEKLY -> "WEEKLY"
                            HabitFrequencyType.SPECIFIC_DAYS -> "CUSTOM"
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = freqLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Gray500,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (habit.reminderTime != null) {
                            Text(
                                text = habit.reminderTime,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Gray500
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Action Area: Minimal Checkbox or Stepper
                if (habit.inputType == HabitInputType.CHECKBOX) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) PureWhite else Color.Transparent)
                            .border(
                                width = 1.5.dp,
                                color = if (isCompleted) PureWhite else Gray500,
                                shape = CircleShape
                            )
                            .clickable { onToggle() }
                            .testTag("habit_toggle_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = PureBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    // Minimal Counter
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                                .clickable(enabled = item.currentProgress > 0) {
                                    onProgressChange(item.currentProgress - 1)
                                }
                                .testTag("habit_minus_${habit.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Minus",
                                tint = if (item.currentProgress > 0) MaterialTheme.colorScheme.onSurface else Gray500,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "${item.currentProgress}/${habit.targetCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) PureWhite else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                                .clickable {
                                    onProgressChange(item.currentProgress + 1)
                                }
                                .testTag("habit_plus_${habit.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Minimalist Progress bar for counter habits
            if (habit.inputType == HabitInputType.COUNTER) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { item.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = PureWhite,
                    trackColor = Gray800
                )
            }
        }
    }
}
