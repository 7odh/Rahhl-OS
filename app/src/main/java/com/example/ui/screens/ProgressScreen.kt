package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GoalItem
import com.example.data.model.GoalMilestone
import com.example.data.model.Habit
import com.example.data.model.TaskItem
import com.example.ui.AchievementViewModel
import com.example.ui.QuickAddType
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
fun ProgressScreen(
    viewModel: AchievementViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Habits, 1: Goals, 2: Tasks

    val habits by viewModel.allHabits.collectAsStateWithLifecycle()
    val goals by viewModel.allGoals.collectAsStateWithLifecycle()
    val milestones by viewModel.allMilestones.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Section Header Tabs
        Surface(
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "الإنجازات والتقدم",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Custom Pill Tab Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceVariantDark)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("العادات", "الأهداف", "المهام")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedSection == index
                        val color = when (index) {
                            0 -> PrimaryEmerald
                            1 -> GoalPurple
                            else -> TaskBlue
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable { selectedSection = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) color else TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        // Section Content
        when (selectedSection) {
            0 -> HabitsSection(
                habits = habits,
                onAddClick = { viewModel.setQuickAddType(QuickAddType.HABIT) },
                onToggleActive = { viewModel.toggleHabitActiveState(it) }
            )
            1 -> GoalsSection(
                goals = goals,
                milestones = milestones,
                onAddClick = { viewModel.setQuickAddType(QuickAddType.GOAL) },
                onMilestoneProgressChange = { m, p -> viewModel.updateMilestoneProgress(m, p) },
                onMoveGoalListType = { goal, newType -> viewModel.moveGoalListType(goal, newType) }
            )
            2 -> TasksSection(
                tasks = tasks,
                onAddClick = { viewModel.setQuickAddType(QuickAddType.TASK) },
                onToggleTask = { viewModel.toggleTask(it) },
                onRescheduleTask = { task, newDate -> viewModel.rescheduleTask(task, newDate) }
            )
        }
    }
}

@Composable
fun HabitsSection(
    habits: List<Habit>,
    onAddClick: () -> Unit,
    onToggleActive: (Habit) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        item {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_habit_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "أضف عادة", tint = PrimaryEmerald)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ إضافة عادة", color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Active Habits
        val activeHabits = habits.filter { it.isActive }
        val inactiveHabits = habits.filter { !it.isActive }

        item {
            Text(
                text = "العادات النشطة (${activeHabits.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(activeHabits, key = { it.id }) { habit ->
            HabitManagementCard(
                habit = habit,
                onToggleActive = { onToggleActive(habit) }
            )
        }

        // Inactive / Paused Habits (History preserved!)
        if (inactiveHabits.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "العادات المتوقفة مؤقتًا (${inactiveHabits.size}) — تاريخها محفوظ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMutedDark,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(inactiveHabits, key = { it.id }) { habit ->
                HabitManagementCard(
                    habit = habit,
                    onToggleActive = { onToggleActive(habit) }
                )
            }
        }
    }
}

@Composable
fun HabitManagementCard(
    habit: Habit,
    onToggleActive: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isActive) SurfaceDark else SurfaceDark.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (habit.isActive) BorderDark else BorderDark.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryEmerald.copy(alpha = if (habit.isActive) 0.18f else 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getHabitIcon(habit.iconName),
                    contentDescription = habit.name,
                    tint = if (habit.isActive) PrimaryEmerald else TextMutedDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (habit.isActive) TextPrimaryDark else TextMutedDark
                    )
                    if (habit.isEssential) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentGold.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("أساسية", fontSize = 9.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                val freqText = when (habit.frequencyType) {
                    "DAILY" -> "يوميًا"
                    "X_TIMES_WEEK" -> "${habit.targetCountPerWeek} مرات أسبوعيًا"
                    else -> "أيام محددة"
                }
                Text(
                    text = freqText + (habit.preferredTime?.let { " • $it" } ?: ""),
                    fontSize = 12.sp,
                    color = TextMutedDark
                )
            }

            // Switch Active/Pause
            Switch(
                checked = habit.isActive,
                onCheckedChange = { onToggleActive() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryEmerald,
                    uncheckedThumbColor = TextMutedDark,
                    uncheckedTrackColor = SurfaceVariantDark
                )
            )
        }
    }
}

@Composable
fun GoalsSection(
    goals: List<GoalItem>,
    milestones: List<GoalMilestone>,
    onAddClick: () -> Unit,
    onMilestoneProgressChange: (GoalMilestone, Int) -> Unit,
    onMoveGoalListType: (GoalItem, String) -> Unit = { _, _ -> }
) {
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    val categories = listOf(
        "ALL" to "الكل",
        "WEEKLY" to "أهداف الأسبوع",
        "MONTHLY" to "أهداف الشهر",
        "SHORT_TERM" to "قريبة المدى",
        "LONG_TERM" to "مستقبلية"
    )

    val filteredGoals = if (selectedCategoryFilter == "ALL") {
        goals
    } else {
        goals.filter { it.listType == selectedCategoryFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        item {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoalPurple.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoalPurple),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_goal_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "أضف هدف", tint = GoalPurple)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ إضافة هدف", color = GoalPurple, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // List Filter Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(categories) { (key, title) ->
                    FilterChip(
                        selected = selectedCategoryFilter == key,
                        onClick = { selectedCategoryFilter = key },
                        label = { Text(title, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoalPurple,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceVariantDark,
                            labelColor = TextSecondaryDark
                        )
                    )
                }
            }
        }

        items(filteredGoals, key = { it.id }) { goal ->
            val goalMilestones = milestones.filter { it.goalId == goal.id }
            val avgProgress = if (goalMilestones.isNotEmpty()) {
                goalMilestones.map { it.progressPercent }.average().toInt()
            } else 0

            GoalDetailCard(
                goal = goal,
                progressPercent = avgProgress,
                milestones = goalMilestones,
                onMilestoneProgressChange = onMilestoneProgressChange,
                onMoveListType = { newType -> onMoveGoalListType(goal, newType) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun GoalDetailCard(
    goal: GoalItem,
    progressPercent: Int,
    milestones: List<GoalMilestone>,
    onMilestoneProgressChange: (GoalMilestone, Int) -> Unit,
    onMoveListType: (String) -> Unit = {}
) {
    val listTypeName = when (goal.listType) {
        "WEEKLY" -> "أسبوعي 📅"
        "MONTHLY" -> "شهري 🗓️"
        "SHORT_TERM" -> "قريب 🎯"
        "LONG_TERM" -> "مستقبلي 🚀"
        else -> "عام"
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoalPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = goal.title,
                            tint = GoalPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = goal.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = goal.category + (goal.description?.let { " • $it" } ?: ""),
                            fontSize = 11.sp,
                            color = TextMutedDark
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category / List chip (Clickable to move between lists)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceVariantDark)
                            .border(1.dp, GoalPurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable {
                                val nextType = when (goal.listType) {
                                    "WEEKLY" -> "MONTHLY"
                                    "MONTHLY" -> "SHORT_TERM"
                                    "SHORT_TERM" -> "LONG_TERM"
                                    else -> "WEEKLY"
                                }
                                onMoveListType(nextType)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = listTypeName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoalPurple
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Progress Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoalPurple.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$progressPercent%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoalPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { progressPercent / 100f },
                color = GoalPurple,
                trackColor = SurfaceVariantDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            if (milestones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "مراحل الهدف (Milestones):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                milestones.forEach { milestone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = milestone.title,
                            fontSize = 13.sp,
                            color = TextPrimaryDark,
                            modifier = Modifier.width(60.dp)
                        )

                        Slider(
                            value = milestone.progressPercent.toFloat(),
                            onValueChange = { onMilestoneProgressChange(milestone, it.toInt()) },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = GoalPurple,
                                activeTrackColor = GoalPurple,
                                inactiveTrackColor = SurfaceVariantDark
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        Text(
                            text = "${milestone.progressPercent}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (milestone.progressPercent >= 100) PrimaryEmerald else GoalPurple,
                            modifier = Modifier.width(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TasksSection(
    tasks: List<TaskItem>,
    onAddClick: () -> Unit,
    onToggleTask: (TaskItem) -> Unit,
    onRescheduleTask: (TaskItem, String) -> Unit = { _, _ -> }
) {
    var selectedPriorityFilter by remember { mutableStateOf("ALL") }
    var sortByPriority by remember { mutableStateOf(true) }

    val priorities = listOf(
        "ALL" to "الكل",
        "HIGH" to "عالي 🔴",
        "MEDIUM" to "متوسط 🟡",
        "LOW" to "منخفض 🟢"
    )

    val filtered = if (selectedPriorityFilter == "ALL") {
        tasks
    } else {
        tasks.filter { it.priority == selectedPriorityFilter }
    }

    val sorted = if (sortByPriority) {
        filtered.sortedWith(compareByDescending<TaskItem> {
            when (it.priority) {
                "HIGH" -> 3
                "MEDIUM" -> 2
                "LOW" -> 1
                else -> 0
            }
        }.thenBy { it.dueDate })
    } else {
        filtered.sortedBy { it.dueDate }
    }

    val incomplete = sorted.filter { !it.isCompleted }
    val completed = sorted.filter { it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        item {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = TaskBlue.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, TaskBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_task_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "أضف مهمة", tint = TaskBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ إضافة مهمة", color = TaskBlue, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Priority Filter & Sort row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(priorities) { (key, label) ->
                        FilterChip(
                            selected = selectedPriorityFilter == key,
                            onClick = { selectedPriorityFilter = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TaskBlue,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceVariantDark,
                                labelColor = TextSecondaryDark
                            )
                        )
                    }
                }

                TextButton(
                    onClick = { sortByPriority = !sortByPriority }
                ) {
                    Text(
                        text = if (sortByPriority) "الأولوية 🔺" else "التاريخ 📅",
                        fontSize = 11.sp,
                        color = TaskBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Text(
                text = "قائمة المهام (${incomplete.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(incomplete, key = { it.id }) { task ->
            TaskRowCard(
                task = task,
                onToggle = { onToggleTask(task) },
                onReschedule = { date -> onRescheduleTask(task, date) }
            )
        }

        if (completed.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "المهام المنجزة (${completed.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(completed, key = { it.id }) { task ->
                TaskRowCard(
                    task = task,
                    onToggle = { onToggleTask(task) },
                    onReschedule = { date -> onRescheduleTask(task, date) }
                )
            }
        }
    }
}

@Composable
fun TaskRowCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onReschedule: (String) -> Unit = {}
) {
    val prioColor = when (task.priority) {
        "HIGH" -> Color(0xFFEF4444)
        "MEDIUM" -> AccentGold
        "LOW" -> PrimaryEmerald
        else -> TextMutedDark
    }
    val prioLabel = when (task.priority) {
        "HIGH" -> "عالية 🔴"
        "LOW" -> "منخفضة 🟢"
        else -> "متوسطة 🟡"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) SurfaceDark.copy(alpha = 0.5f) else SurfaceDark
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) TaskBlue else Color.Transparent)
                    .border(
                        2.dp,
                        if (task.isCompleted) TaskBlue else TextMutedDark,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "تم",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) TextMutedDark else TextPrimaryDark,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${task.dueDate} • ${task.estimatedMinutes} دقيقة" +
                                (task.description?.let { " • $it" } ?: ""),
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )
                }
            }

            // Priority badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(prioColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = prioLabel, fontSize = 10.sp, color = prioColor, fontWeight = FontWeight.Bold)
            }

            if (!task.isCompleted) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceVariantDark)
                        .clickable {
                            val cal = java.util.Calendar.getInstance()
                            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                            val tomorrow = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)
                            onReschedule(tomorrow)
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("لغداً ➡️", fontSize = 9.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
