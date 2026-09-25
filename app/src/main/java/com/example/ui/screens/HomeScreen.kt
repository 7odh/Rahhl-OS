package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import com.example.ui.dialogs.ResetTodayConfirmationDialog
import com.example.ui.dialogs.QuotesManagementDialog
import com.example.ui.dialogs.ObsidianVaultDialog
import com.example.ui.dialogs.TickTickCalendarDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Habit
import com.example.data.model.TaskItem
import com.example.ui.AchievementViewModel
import com.example.ui.NavigationTab
import com.example.ui.QuickAddType
import com.example.ui.components.AppHeader
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

@Composable
fun HomeScreen(
    viewModel: AchievementViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.allHabits.collectAsStateWithLifecycle()
    val completions by viewModel.todayCompletions.collectAsStateWithLifecycle()
    val tasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val summary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
    val dailyReview by viewModel.todayDailyReview.collectAsStateWithLifecycle()

    val quotes by viewModel.allQuotes.collectAsStateWithLifecycle()
    val quoteIndex by viewModel.quoteIndex.collectAsStateWithLifecycle()
    val isResetDialogOpen by viewModel.isResetDialogOpen.collectAsStateWithLifecycle()
    val isQuotesDialogOpen by viewModel.isQuotesDialogOpen.collectAsStateWithLifecycle()
    val isVaultDialogOpen by viewModel.isVaultDialogOpen.collectAsStateWithLifecycle()
    val isCalendarDialogOpen by viewModel.isCalendarDialogOpen.collectAsStateWithLifecycle()

    val currentQuote = if (quotes.isNotEmpty()) {
        quotes[quoteIndex % quotes.size].text
    } else {
        "يوم جديد .. فرصة جديدة"
    }

    val completedHabitIds = completions.map { it.habitId }.toSet()

    // Filter habits according to rest day
    val displayHabits = if (summary.isRestDay) {
        habits.filter { it.isActive && it.isEssential }
    } else {
        habits.filter { it.isActive }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        // 1. Header
        item {
            AppHeader(
                dateText = viewModel.displayDateFormat.format(viewModel.dateFormat.parse(currentDate) ?: java.util.Date()),
                greetingTitle = "صباح الخير",
                greetingSubtitle = currentQuote,
                isRestDay = summary.isRestDay,
                onRestClick = { viewModel.toggleRestDay() },
                onResetClick = { viewModel.isResetDialogOpen.value = true },
                onQuoteClick = { viewModel.cycleNextQuote(quotes.size) },
                onCalendarClick = { viewModel.isCalendarDialogOpen.value = true },
                onVaultClick = { viewModel.isVaultDialogOpen.value = true },
                onQuickAddClick = { viewModel.setQuickAddType(QuickAddType.MENU) }
            )
        }

        // 2. Today's Achievement Card (إنجاز اليوم)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("today_achievement_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "إنجاز اليوم",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = summary.motivationalMessage,
                                fontSize = 12.sp,
                                color = if (summary.isRestDay) RestCyan else TextSecondaryDark
                            )
                        }

                        // Flag icon badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Goal",
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3 Donut/Circular Stats matching screenshot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CircularProgressIndicatorItem(
                            label = "العادات",
                            value = "${summary.habitsCompleted}/${summary.habitsTotal}",
                            percent = if (summary.habitsTotal > 0) (summary.habitsCompleted * 100) / summary.habitsTotal else 0,
                            color = PrimaryEmerald
                        )

                        CircularProgressIndicatorItem(
                            label = "المهام",
                            value = "${summary.tasksCompleted}/${summary.tasksTotal}",
                            percent = if (summary.tasksTotal > 0) (summary.tasksCompleted * 100) / summary.tasksTotal else 0,
                            color = TaskBlue
                        )

                        val hours = summary.focusMinutes / 60
                        val mins = summary.focusMinutes % 60
                        val focusText = if (hours > 0) "${hours}س ${mins}د" else "${mins}د"
                        CircularProgressIndicatorItem(
                            label = "التركيز",
                            value = focusText,
                            percent = (summary.focusMinutes.coerceAtMost(120) * 100) / 120, // baseline 2h
                            color = FocusOrange
                        )
                    }
                }
            }
        }

        // Rest Day Banner if active
        if (summary.isRestDay) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RestCyan.copy(alpha = 0.12f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RestCyan.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BeachAccess,
                            contentDescription = "Rest",
                            tint = RestCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "وضع الراحة مفعّل لليوم 🏖️",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = RestCyan
                            )
                            Text(
                                text = "تم إخفاء العادات غير الضرورية ولن يُحسب عدم إنجازها كتقصير في إحصائياتك.",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // 3. What should I do now? (ماذا عليّ أن أفعل الآن؟)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PrimaryEmerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ماذا عليّ أن أفعل الآن؟",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }

                Text(
                    text = "${displayHabits.size + tasks.size} عناصر",
                    fontSize = 12.sp,
                    color = TextMutedDark
                )
            }
        }

        // Action Items: Habits
        items(displayHabits, key = { "habit_${it.id}" }) { habit ->
            val isCompleted = completedHabitIds.contains(habit.id)
            ActionItemCard(
                title = habit.name,
                subtitle = habit.preferredTime ?: if (habit.isEssential) "عادة أساسية" else "عادة يومية",
                typeLabel = "عادة",
                typeColor = PrimaryEmerald,
                icon = getHabitIcon(habit.iconName),
                isCompleted = isCompleted,
                onToggle = { viewModel.toggleHabit(habit) },
                testTag = "action_item_habit_${habit.id}"
            )
        }

        // Action Items: Tasks
        items(tasks, key = { "task_${it.id}" }) { task ->
            ActionItemCard(
                title = task.title,
                subtitle = "${task.estimatedMinutes} دقيقة" + if (task.priority == "HIGH") " • أولوية عالية" else "",
                typeLabel = "مهمة",
                typeColor = TaskBlue,
                icon = Icons.Default.Assignment,
                isCompleted = task.isCompleted,
                onToggle = { viewModel.toggleTask(task) },
                testTag = "action_item_task_${task.id}"
            )
        }

        // Quick Focus Suggestion
        item {
            QuickFocusCard(
                title = "جلسة تركيز مقترحة",
                subtitle = "25 دقيقة • Pomodoro",
                onStartClick = { viewModel.selectTab(NavigationTab.FOCUS) }
            )
        }

        // 4. Quick Add Button
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { viewModel.setQuickAddType(QuickAddType.MENU) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("home_add_new_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة",
                        tint = PrimaryEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+ إضافة جديدة",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryEmerald
                    )
                }
            }
        }

        // 5. Daily Note / Review section
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "ملاحظة",
                                tint = AccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ملاحظة ومراجعة اليوم",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentGold.copy(alpha = 0.15f))
                                .clickable { viewModel.setQuickAddType(QuickAddType.NOTE) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (dailyReview?.dailyNote.isNullOrBlank()) "+ تدوين" else "تعديل",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!dailyReview?.dailyNote.isNullOrBlank()) {
                        Text(
                            text = dailyReview?.dailyNote ?: "",
                            fontSize = 13.sp,
                            color = TextPrimaryDark,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(
                            text = "لا توجد ملاحظة مسجلة بعد. دوّن ملخصًا سريعًا ليومك هنا...",
                            fontSize = 12.sp,
                            color = TextMutedDark
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (isResetDialogOpen) {
        ResetTodayConfirmationDialog(
            onConfirm = { viewModel.resetTodayProgress() },
            onDismiss = { viewModel.isResetDialogOpen.value = false }
        )
    }

    if (isQuotesDialogOpen) {
        QuotesManagementDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.isQuotesDialogOpen.value = false }
        )
    }

    if (isVaultDialogOpen) {
        ObsidianVaultDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.isVaultDialogOpen.value = false }
        )
    }

    if (isCalendarDialogOpen) {
        TickTickCalendarDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.isCalendarDialogOpen.value = false }
        )
    }
}

@Composable
fun ActionItemCard(
    title: String,
    subtitle: String,
    typeLabel: String,
    typeColor: Color,
    icon: ImageVector,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    testTag: String
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX, label = "swipe_offset")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Swipe Background
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    when {
                        offsetX > 40f -> PrimaryEmerald.copy(alpha = 0.25f)
                        offsetX < -40f -> FocusOrange.copy(alpha = 0.25f)
                        else -> Color.Transparent
                    }
                )
                .padding(horizontal = 18.dp),
            contentAlignment = if (offsetX > 0) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            if (offsetX > 40f) {
                Text(
                    text = "إنجاز ✓",
                    color = PrimaryEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            } else if (offsetX < -40f) {
                Text(
                    text = "تبديل ⚡",
                    color = FocusOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Foreground Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCompleted) SurfaceDark.copy(alpha = 0.5f) else SurfaceDark
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isCompleted) BorderDark.copy(alpha = 0.4f) else BorderDark
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > 100f || offsetX < -100f) {
                                onToggle()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = { offsetX = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(-140f, 140f)
                        }
                    )
                }
                .clickable(onClick = onToggle)
                .testTag(testTag)
        ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Icon
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) PrimaryEmerald else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (isCompleted) PrimaryEmerald else TextMutedDark,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "مكتمل",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Icon circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = typeLabel,
                    tint = typeColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) TextMutedDark else TextPrimaryDark,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMutedDark
                )
            }

            // Type Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(typeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = typeLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = typeColor
                )
            }
        }
    }
}
}

@Composable
fun QuickFocusCard(
    title: String,
    subtitle: String,
    onStartClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onStartClick)
            .testTag("action_item_focus_suggestion")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(FocusOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "ابدأ",
                    tint = FocusOrange,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FocusOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "تركيز",
                    tint = FocusOrange,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMutedDark
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(FocusOrange.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "تركيز",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = FocusOrange
                )
            }
        }
    }
}

fun getHabitIcon(iconName: String): ImageVector {
    return when (iconName) {
        "prayer" -> Icons.Default.SelfImprovement
        "book" -> Icons.Default.Book
        "fitness" -> Icons.Default.FitnessCenter
        "language" -> Icons.Default.Language
        "study" -> Icons.Default.School
        "code" -> Icons.Default.Code
        else -> Icons.Default.Star
    }
}
