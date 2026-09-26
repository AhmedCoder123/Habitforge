package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CloudSyncCard
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray800
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.HabitViewModel
import com.example.util.KarachiTimeHelper

@Composable
fun NotificationsScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val countdownSeconds by viewModel.countdownSeconds.collectAsStateWithLifecycle()
    val notifEnabled by viewModel.notifEnabled.collectAsStateWithLifecycle()
    val midnightAlertEnabled by viewModel.midnightAlertEnabled.collectAsStateWithLifecycle()
    val morningBriefEnabled by viewModel.morningBriefEnabled.collectAsStateWithLifecycle()
    val weekendRunAlert by viewModel.weekendRunAlert.collectAsStateWithLifecycle()
    val groomingAlert by viewModel.groomingAlert.collectAsStateWithLifecycle()

    var statusMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        statusMessage = if (isGranted) "Notification permission granted" else "Notification permission denied"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("notifications_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Description
        item {
            Column {
                Text(
                    text = "Alerts & Reset",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Configure reminders and strict 12:00 AM reset triggers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray400
                )
            }
        }

        // Status banner if any
        if (statusMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Gray800,
                    border = BorderStroke(1.dp, Gray700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Karachi Reset Engine Card (Monochrome Minimalist)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Gray800),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = "Reset Engine",
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Midnight Reset",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Gray800
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Every night at exactly 12:00 AM Karachi Time (GMT+5), the dynamic checklist clears today's status, persists completion into the 30-day log, and generates the new schedule.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray400
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Next Reset Countdown:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray500
                        )
                        Text(
                            text = KarachiTimeHelper.formatCountdown(countdownSeconds),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // Notification Permission Button (for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "System Notification Permission",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Enable alerts on your device",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray400
                            )
                        }
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = PureBlack
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Grant", color = PureBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Cloud Firestore Synchronization Card
        item {
            CloudSyncCard(viewModel = viewModel)
        }

        // Notification Preferences Switches
        item {
            Text(
                text = "Alert Preferences",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NotificationToggleCard(
                    title = "Master Habit Notifications",
                    description = "Allow scheduled reminders and daily triggers",
                    isChecked = notifEnabled,
                    onCheckedChange = { viewModel.toggleNotification(it) },
                    icon = Icons.Default.NotificationsActive
                )

                NotificationToggleCard(
                    title = "12:00 AM Reset Warning",
                    description = "Alerts 30 mins before midnight reset to finish pending tasks",
                    isChecked = midnightAlertEnabled,
                    onCheckedChange = { viewModel.toggleMidnightAlert(it) },
                    icon = Icons.Default.Alarm,
                    enabled = notifEnabled
                )

                NotificationToggleCard(
                    title = "Morning Briefing (08:00 AM PKT)",
                    description = "Summary of scheduled habits when Karachi day starts",
                    isChecked = morningBriefEnabled,
                    onCheckedChange = { viewModel.toggleMorningBrief(it) },
                    icon = Icons.Default.WbSunny,
                    enabled = notifEnabled
                )

                NotificationToggleCard(
                    title = "Weekend Running Reminder",
                    description = "Morning cardio reminder on Saturday and Sunday",
                    isChecked = weekendRunAlert,
                    onCheckedChange = { viewModel.toggleWeekendRun(it) },
                    icon = Icons.Default.Notifications,
                    enabled = notifEnabled
                )

                NotificationToggleCard(
                    title = "Grooming Cadence Alert",
                    description = "Reminds on 3-day beard/eyebrows and weekly haircut days",
                    isChecked = groomingAlert,
                    onCheckedChange = { viewModel.toggleGroomingAlert(it) },
                    icon = Icons.Default.Notifications,
                    enabled = notifEnabled
                )
            }
        }

        // Quick Actions & Testing Tools
        item {
            Text(
                text = "Testing Triggers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Verify notification delivery and test the reset engine",
                style = MaterialTheme.typography.bodySmall,
                color = Gray400
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        viewModel.testHabitNotification(
                            title = "Creatine & Hydration Alert",
                            body = "Remember to take 5g Creatine and hit your 3-liter water target!"
                        )
                        statusMessage = "Test habit notification dispatched."
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_habit_notif_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = PureBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Test Habit Notification", color = PureBlack, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.testResetNotification()
                        statusMessage = "Test midnight reset alert dispatched."
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_reset_notif_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gray800,
                        contentColor = PureWhite
                    ),
                    border = BorderStroke(1.dp, Gray700),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Test 12:00 AM Reset Alert", color = PureWhite, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.simulateResetNow()
                        statusMessage = "Today's Karachi checklist has been reset."
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulate_reset_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Gray700)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simulate Karachi Midnight Reset Now", color = PureWhite)
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleCard(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PureBlack,
                    checkedTrackColor = PureWhite,
                    uncheckedThumbColor = Gray500,
                    uncheckedTrackColor = Gray800
                )
            )
        }
    }
}
