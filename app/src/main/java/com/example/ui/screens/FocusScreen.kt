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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AchievementViewModel
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BorderDark
import com.example.ui.theme.FocusOrange
import com.example.ui.theme.GoalPurple
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TaskBlue
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun FocusScreen(
    viewModel: AchievementViewModel,
    modifier: Modifier = Modifier
) {
    val targetMinutes by viewModel.timerTargetMinutes.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.timerRemainingSeconds.collectAsStateWithLifecycle()
    val isRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val relatedType by viewModel.timerRelatedType.collectAsStateWithLifecycle()
    val relatedName by viewModel.timerRelatedName.collectAsStateWithLifecycle()
    val todaySessions by viewModel.todayFocusSessions.collectAsStateWithLifecycle()
    val activeGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val incompleteTasks by viewModel.allTasks.collectAsStateWithLifecycle()

    var showLinkDropdown by remember { mutableStateOf(false) }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val totalTargetSecs = (targetMinutes * 60).coerceAtLeast(1)
    val progress = (remainingSeconds.toFloat() / totalTargetSecs).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        item {
            Text(
                text = "جلسة تركيز (Pomodoro)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Circular Timer Display
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Outer background circle
                    drawArc(
                        color = FocusOrange.copy(alpha = 0.15f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Active countdown arc
                    drawArc(
                        color = FocusOrange,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "وقت التركيز ⏱️" else "جاهز للبدء",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                    if (!relatedName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = relatedName ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FocusOrange
                        )
                    }
                }
            }
        }

        // Controls (Play / Pause / Stop)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRunning) {
                    // Pause Button
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(FocusOrange)
                            .clickable { viewModel.pauseTimer() }
                            .testTag("pause_timer_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "إيقاف مؤقت",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    // Play Button
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(FocusOrange)
                            .clickable { viewModel.startTimer() }
                            .testTag("start_timer_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "بدء الجلسة",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                // Stop & Save button (always preserves partial focus time!)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariantDark)
                        .border(1.dp, BorderDark, CircleShape)
                        .clickable { viewModel.stopTimerAndSave() }
                        .testTag("stop_timer_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "إنهاء وحفظ",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Link Focus Session To (ربط الجلسة بـ)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ربط الجلسة بـ:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("TASK", "مهمة", TaskBlue),
                            Triple("GOAL", "هدف", GoalPurple),
                            Triple("NONE", "بدون ربط", TextSecondaryDark)
                        ).forEach { (type, label, color) ->
                            FilterChip(
                                selected = relatedType == type,
                                onClick = {
                                    if (type == "NONE") {
                                        viewModel.setTimerLink("NONE", null, "جلسة حرة")
                                    } else if (type == "TASK" && incompleteTasks.isNotEmpty()) {
                                        viewModel.setTimerLink("TASK", incompleteTasks.first().id, incompleteTasks.first().title)
                                    } else if (type == "GOAL" && activeGoals.isNotEmpty()) {
                                        viewModel.setTimerLink("GOAL", activeGoals.first().id, activeGoals.first().title)
                                    }
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = color.copy(alpha = 0.2f),
                                    selectedLabelColor = color
                                )
                            )
                        }
                    }

                    if (relatedType != "NONE") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceVariantDark)
                                .clickable { showLinkDropdown = true }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "العنصر المختار: ${relatedName ?: "اضغط للاختيار"}",
                                fontSize = 13.sp,
                                color = TextPrimaryDark
                            )

                            DropdownMenu(
                                expanded = showLinkDropdown,
                                onDismissRequest = { showLinkDropdown = false }
                            ) {
                                if (relatedType == "TASK") {
                                    incompleteTasks.forEach { task ->
                                        DropdownMenuItem(
                                            text = { Text(task.title) },
                                            onClick = {
                                                viewModel.setTimerLink("TASK", task.id, task.title)
                                                showLinkDropdown = false
                                            }
                                        )
                                    }
                                } else if (relatedType == "GOAL") {
                                    activeGoals.forEach { goal ->
                                        DropdownMenuItem(
                                            text = { Text(goal.title) },
                                            onClick = {
                                                viewModel.setTimerLink("GOAL", goal.id, goal.title)
                                                showLinkDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Duration Selection Presets (15, 25, 30, 45, 60)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "اختيار مدة الجلسة:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(15, 25, 30, 45, 60).forEach { mins ->
                            val isSelected = targetMinutes == mins
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) FocusOrange else SurfaceVariantDark)
                                    .clickable { viewModel.setTimerDuration(mins) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$mins د",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Today's Focus History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل تركيز اليوم (${todaySessions.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                val totalMins = todaySessions.sumOf { it.durationMinutes }
                val h = totalMins / 60
                val m = totalMins % 60
                Text(
                    text = "المجموع: ${if (h > 0) "${h}س ${m}د" else "${m}د"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FocusOrange
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (todaySessions.isEmpty()) {
            item {
                Text(
                    text = "لا توجد جلسات مكتملة اليوم بعد. ابدأ جلستك الأولى الآن!",
                    fontSize = 12.sp,
                    color = TextMutedDark,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(todaySessions, key = { it.id }) { session ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(FocusOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "جلسة",
                                tint = FocusOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${session.durationMinutes} دقيقة تركيز" +
                                        (if (session.status == "PARTIAL") " (جزئية)" else " (مكتملة ✓)"),
                                fontSize = 11.sp,
                                color = if (session.status == "PARTIAL") AccentGold else PrimaryEmerald
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariantDark)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${session.durationMinutes} د",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }
                    }
                }
            }
        }
    }
}
