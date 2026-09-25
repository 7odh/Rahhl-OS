package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TimelineEvent
import com.example.ui.AchievementViewModel
import com.example.ui.components.CircularProgressIndicatorItem
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BorderDark
import com.example.ui.theme.FocusOrange
import com.example.ui.theme.GoalPurple
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.RestCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TaskBlue
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: AchievementViewModel,
    modifier: Modifier = Modifier
) {
    var rangeIndex by remember { mutableIntStateOf(0) } // 0: Weekly, 1: 2 Weeks, 2: Monthly, 3: Custom
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
    val todayEvents by viewModel.todayEvents.collectAsStateWithLifecycle()
    val allFocusSessions by viewModel.allFocusSessions.collectAsStateWithLifecycle()
    val allHabits by viewModel.allHabits.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val allGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val allRestDays by viewModel.allRestDays.collectAsStateWithLifecycle()

    val totalFocusMins = allFocusSessions.sumOf { it.durationMinutes }
    val focusHours = totalFocusMins / 60
    val focusMins = totalFocusMins % 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Range Filter bar matching screenshot 4
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariantDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("أسبوعي", "أسبوعين", "شهري", "مخصص").forEachIndexed { idx, label ->
                    val isSelected = rangeIndex == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PrimaryEmerald.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { rangeIndex = idx }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PrimaryEmerald else TextSecondaryDark
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Overview Card (نظرة عامة)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "نظرة عامة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CircularProgressIndicatorItem(
                            label = "إكمال العادات",
                            value = "78%",
                            percent = 78,
                            color = PrimaryEmerald
                        )

                        CircularProgressIndicatorItem(
                            label = "إكمال المهام",
                            value = "64%",
                            percent = 64,
                            color = TaskBlue
                        )

                        CircularProgressIndicatorItem(
                            label = "وقت التركيز",
                            value = "${focusHours}h ${focusMins}m",
                            percent = 75,
                            color = FocusOrange
                        )

                        CircularProgressIndicatorItem(
                            label = "الأهداف النشطة",
                            value = "${allGoals.count { it.isActive }}",
                            percent = 100,
                            color = GoalPurple
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Where did your time go? (أين ذهب وقتك؟)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "أين ذهب وقتك؟",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val categories = listOf(
                        Triple("English", "3h 20m (48%)", TaskBlue to 0.48f),
                        Triple("Programming", "1h 10m (21%)", GoalPurple to 0.21f),
                        Triple("Other / حر", "1h 05m (16%)", FocusOrange to 0.16f)
                    )

                    categories.forEach { (name, stats, colorWeight) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colorWeight.first)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryDark,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stats,
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                        LinearProgressIndicator(
                            progress = { colorWeight.second },
                            color = colorWeight.first,
                            trackColor = SurfaceVariantDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Habits Consistency Weekly Bars
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تقدم العادات (هذا الأسبوع)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf(
                        "السبت" to 0.85f,
                        "الأحد" to 0.70f,
                        "الاثنين" to 0.90f,
                        "الثلاثاء" to 0.80f,
                        "الأربعاء" to 0.65f,
                        "الخميس" to 0.75f,
                        "الجمعة" to 0.50f
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEach { (day, fraction) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height((80 * fraction).dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(if (day == "الجمعة") RestCyan else PrimaryEmerald)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day.take(3),
                                    fontSize = 10.sp,
                                    color = TextMutedDark
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Timeline & History (السجل التاريخي والتقويم)
        item {
            Text(
                text = "السجل التاريخي والتقويم",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Day Selector Strip
            val cal = Calendar.getInstance()
            val daysList = (0..6).map { offset ->
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_YEAR, offset - 3)
                val dStr = viewModel.dateFormat.format(c.time)
                val dayNum = SimpleDateFormat("d", Locale.getDefault()).format(c.time)
                val dayName = SimpleDateFormat("E", Locale("ar")).format(c.time)
                Triple(dStr, dayNum, dayName)
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysList) { (dStr, dayNum, dayName) ->
                    val isSelected = currentDate == dStr
                    val isRest = allRestDays.any { it.date == dStr && it.isRest }

                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) PrimaryEmerald
                                else if (isRest) RestCyan.copy(alpha = 0.2f)
                                else SurfaceDark
                            )
                            .border(
                                1.dp,
                                if (isSelected) PrimaryEmerald else BorderDark,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.setDate(dStr) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayName,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else TextMutedDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dayNum,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimaryDark
                            )
                            if (isRest) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("🏖️", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Timeline Events List
        if (todayEvents.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "لا توجد أحداث مسجلة في هذا التاريخ",
                            fontSize = 12.sp,
                            color = TextMutedDark
                        )
                    }
                }
            }
        } else {
            items(todayEvents, key = { it.id }) { event ->
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeStr = timeFormat.format(Date(event.timestamp))

                TimelineEventRow(
                    time = timeStr,
                    title = event.title,
                    subtitle = event.subtitle ?: "",
                    eventType = event.eventType
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TimelineEventRow(
    time: String,
    title: String,
    subtitle: String,
    eventType: String
) {
    val (icon, color) = when (eventType) {
        "HABIT_COMPLETED" -> Icons.Default.Check to PrimaryEmerald
        "TASK_COMPLETED" -> Icons.Default.Task to TaskBlue
        "FOCUS_SESSION" -> Icons.Default.Timer to FocusOrange
        "GOAL_PROGRESS" -> Icons.Default.Flag to GoalPurple
        "REST_DAY" -> Icons.Default.BeachAccess to RestCyan
        else -> Icons.Default.EditNote to AccentGold
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondaryDark,
            modifier = Modifier.width(44.dp)
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = TextMutedDark
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "تم",
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
