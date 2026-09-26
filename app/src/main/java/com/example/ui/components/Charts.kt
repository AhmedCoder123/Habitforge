package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.DailySummaryEntity
import com.example.data.model.HabitCategory
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray600
import com.example.ui.theme.Gray700
import com.example.ui.theme.Gray800
import com.example.ui.theme.PureWhite

@Composable
fun Rolling30DayChart(
    summaries: List<DailySummaryEntity>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rolling_30_day_chart"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                        text = "Rolling 30-Day Adherence",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Daily checklist completion rates • 80% target line",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray400
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Gray800
                ) {
                    Text(
                        text = "30 Days",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray300,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Day Inspector Tooltip
            val selectedSummary = selectedIndex?.let { summaries.getOrNull(it) }
            if (selectedSummary != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, Gray700),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Date: ${selectedSummary.dateKey}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${selectedSummary.completedCount} of ${selectedSummary.totalScheduled} habits completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray400
                            )
                        }
                        Text(
                            text = "${selectedSummary.completionRate.toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }

            // Canvas Bar Chart
            val outlineColor = Gray700

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(summaries) {
                            detectTapGestures { offset ->
                                val barWidthTotal = size.width / (if (summaries.isNotEmpty()) summaries.size else 30)
                                val index = (offset.x / barWidthTotal).toInt().coerceIn(0, (summaries.size - 1).coerceAtLeast(0))
                                selectedIndex = if (selectedIndex == index) null else index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height - 24f // leave room for bottom labels
                    val count = summaries.size.coerceAtLeast(1)
                    val barSlotWidth = width / count
                    val barWidth = (barSlotWidth * 0.65f).coerceAtLeast(3f)

                    // Draw 80% Target Benchmark Line (Minimalist dashed line)
                    val targetY = height * (1f - 0.80f)
                    drawLine(
                        color = Gray500,
                        start = Offset(0f, targetY),
                        end = Offset(width, targetY),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )

                    // Draw baseline
                    drawLine(
                        color = outlineColor,
                        start = Offset(0f, height),
                        end = Offset(width, height),
                        strokeWidth = 1f
                    )

                    // Draw 50% line
                    val midY = height * 0.5f
                    drawLine(
                        color = outlineColor.copy(alpha = 0.3f),
                        start = Offset(0f, midY),
                        end = Offset(width, midY),
                        strokeWidth = 1f
                    )

                    // Draw bars
                    summaries.forEachIndexed { i, summary ->
                        val barHeight = (summary.completionRate / 100f * height).coerceIn(4f, height)
                        val x = (i * barSlotWidth) + ((barSlotWidth - barWidth) / 2f)
                        val y = height - barHeight

                        val barColor = when {
                            summary.completionRate >= 80f -> PureWhite
                            summary.completionRate >= 60f -> Gray300
                            else -> Gray600
                        }

                        val isSelected = selectedIndex == i

                        drawRoundRect(
                            color = if (isSelected) PureWhite else barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(3f, 3f)
                        )

                        if (isSelected) {
                            drawCircle(
                                color = PureWhite,
                                radius = barWidth * 0.6f,
                                center = Offset(x + barWidth / 2f, y - 8f)
                            )
                        }
                    }
                }
            }

            // Chart Axis Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "30 days ago",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Gray500, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "80% Target",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray400
                    )
                }
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConsistencyHeatmapGrid(
    summaries: List<DailySummaryEntity>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("consistency_heatmap_grid"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "30-Day Activity Heatmap",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Visual consistency matrix over the rolling period",
                style = MaterialTheme.typography.bodySmall,
                color = Gray400
            )

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                maxItemsInEachRow = 10
            ) {
                summaries.forEach { item ->
                    val color = when {
                        item.completionRate >= 90f -> PureWhite
                        item.completionRate >= 75f -> Gray300
                        item.completionRate >= 50f -> Gray500
                        item.completionRate > 0f -> Gray700
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                            .border(1.dp, Gray800, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.completionRate >= 90f) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(Color.Black, CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Minimalist Heatmap Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(10.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(2.dp)).border(1.dp, Gray800, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(3.dp))
                Box(modifier = Modifier.size(10.dp).background(Gray700, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(3.dp))
                Box(modifier = Modifier.size(10.dp).background(Gray500, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(3.dp))
                Box(modifier = Modifier.size(10.dp).background(Gray300, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(3.dp))
                Box(modifier = Modifier.size(10.dp).background(PureWhite, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "More",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    categoryRates: Map<HabitCategory, Float>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_breakdown_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Adherence by Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "30-day performance across core routine pillars",
                style = MaterialTheme.typography.bodySmall,
                color = Gray400
            )

            Spacer(modifier = Modifier.height(14.dp))

            HabitCategory.entries.forEach { category ->
                val rate = categoryRates[category] ?: 0f

                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${rate.toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { rate / 100f },
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
    }
}
