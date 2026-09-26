package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.HabitCategory
import com.example.data.model.HabitFrequencyType
import com.example.data.model.HabitInputType
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray800
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        category: HabitCategory,
        frequencyType: HabitFrequencyType,
        intervalDays: Int,
        inputType: HabitInputType,
        targetCount: Int,
        unit: String,
        iconName: String,
        reminderTime: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(HabitCategory.CUSTOM) }
    var frequencyType by remember { mutableStateOf(HabitFrequencyType.DAILY) }
    var intervalDays by remember { mutableIntStateOf(3) }
    var inputType by remember { mutableStateOf(HabitInputType.CHECKBOX) }
    var targetCount by remember { mutableIntStateOf(1) }
    var unit by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("08:00") }

    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Habit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorText = null
                    },
                    label = { Text("Habit Title") },
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_habit_title_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = Gray700
                    )
                )

                // Category Selection
                Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Gray300)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HabitCategory.entries.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PureWhite else Gray800,
                            border = BorderStroke(1.dp, if (isSelected) PureWhite else Gray700),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { category = cat }
                        ) {
                            Text(
                                text = cat.displayName.split(" ").first(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PureBlack else Gray300,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Frequency Selection
                Text("Schedule Frequency", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Gray300)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        HabitFrequencyType.DAILY to "Every Day",
                        HabitFrequencyType.EVERY_N_DAYS to "Every N Days",
                        HabitFrequencyType.WEEKEND_ONLY to "Saturday & Sunday",
                        HabitFrequencyType.WEEKLY to "Weekly"
                    ).forEach { (freq, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { frequencyType = freq }
                        ) {
                            RadioButton(
                                selected = frequencyType == freq,
                                onClick = { frequencyType = freq },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PureWhite,
                                    unselectedColor = Gray500
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                if (frequencyType == HabitFrequencyType.EVERY_N_DAYS) {
                    OutlinedTextField(
                        value = intervalDays.toString(),
                        onValueChange = { intervalDays = it.toIntOrNull()?.coerceIn(2, 30) ?: 3 },
                        label = { Text("Interval in days") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PureWhite,
                            unfocusedBorderColor = Gray700
                        )
                    )
                }

                // Input Type: Checkbox vs Counter
                Text("Tracking Style", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Gray300)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (inputType == HabitInputType.CHECKBOX) PureWhite else Gray800,
                        border = BorderStroke(1.dp, if (inputType == HabitInputType.CHECKBOX) PureWhite else Gray700),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { inputType = HabitInputType.CHECKBOX }
                    ) {
                        Text(
                            text = "Checkmark",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (inputType == HabitInputType.CHECKBOX) PureBlack else Gray300,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (inputType == HabitInputType.COUNTER) PureWhite else Gray800,
                        border = BorderStroke(1.dp, if (inputType == HabitInputType.COUNTER) PureWhite else Gray700),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { inputType = HabitInputType.COUNTER }
                    ) {
                        Text(
                            text = "Counter",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (inputType == HabitInputType.COUNTER) PureBlack else Gray300,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (inputType == HabitInputType.COUNTER) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = targetCount.toString(),
                            onValueChange = { targetCount = it.toIntOrNull()?.coerceIn(1, 100) ?: 1 },
                            label = { Text("Target") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PureWhite,
                                unfocusedBorderColor = Gray700
                            )
                        )

                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PureWhite,
                                unfocusedBorderColor = Gray700
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = { reminderTime = it },
                    label = { Text("Reminder (e.g. 09:00)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = Gray700
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorText = "Title cannot be empty"
                        return@Button
                    }
                    onConfirm(
                        title,
                        category,
                        frequencyType,
                        intervalDays,
                        inputType,
                        if (inputType == HabitInputType.CHECKBOX) 1 else targetCount,
                        unit,
                        "check",
                        reminderTime.ifBlank { null }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_add_habit_button")
            ) {
                Text("Add", color = PureBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = Gray400)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
