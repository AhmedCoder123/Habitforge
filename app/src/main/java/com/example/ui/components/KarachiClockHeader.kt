package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray800
import com.example.ui.theme.PureWhite
import com.example.util.KarachiTimeHelper
import java.time.LocalDate
import java.time.ZonedDateTime

@Composable
fun KarachiClockHeader(
    karachiTime: ZonedDateTime,
    currentDate: LocalDate,
    countdownSeconds: Long,
    onSimulateReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val secondsInDay = (karachiTime.hour * 3600) + (karachiTime.minute * 60) + karachiTime.second
    val dayFraction = (secondsInDay.toFloat() / 86400f).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("karachi_clock_header"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Karachi GMT+5 and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(PureWhite)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "KARACHI (GMT+5)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray400,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = KarachiTimeHelper.formatDisplayDate(currentDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Clock & Countdown Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = KarachiTimeHelper.formatDisplayTime(karachiTime),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RESET IN",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray500,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = KarachiTimeHelper.formatCountdown(countdownSeconds),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Gray300
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Minimalist day progress line
            LinearProgressIndicator(
                progress = { dayFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PureWhite,
                trackColor = Gray800
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily 12:00 AM Reset",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
                Text(
                    text = "${(dayFraction * 100).toInt()}% day elapsed",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }
        }
    }
}
