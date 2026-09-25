package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AchievementBottomBar
import com.example.ui.dialogs.AddDailyNoteDialog
import com.example.ui.dialogs.AddGoalDialog
import com.example.ui.dialogs.AddHabitDialog
import com.example.ui.dialogs.AddTaskDialog
import com.example.ui.dialogs.ExportPreviewDialog
import com.example.ui.dialogs.QuickAddMenuDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.theme.AchievementTheme

@Composable
fun AchievementApp(
    viewModel: AchievementViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val quickAddType by viewModel.quickAddType.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.actionFeedback.collectAsStateWithLifecycle()
    val exportedContent by viewModel.exportedContent.collectAsStateWithLifecycle()
    val activeGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    AchievementTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                AchievementBottomBar(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                    when (tab) {
                        NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                        NavigationTab.PROGRESS -> ProgressScreen(viewModel = viewModel)
                        NavigationTab.FOCUS -> FocusScreen(viewModel = viewModel)
                        NavigationTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                        NavigationTab.MORE -> MoreScreen(viewModel = viewModel)
                    }
                }
            }

            // Quick Add Dialogs
            when (quickAddType) {
                QuickAddType.MENU -> {
                    QuickAddMenuDialog(
                        onDismiss = { viewModel.setQuickAddType(QuickAddType.NONE) },
                        onSelectOption = { viewModel.setQuickAddType(it) }
                    )
                }
                QuickAddType.TASK -> {
                    AddTaskDialog(
                        currentDate = currentDate,
                        activeGoals = activeGoals.filter { it.isActive },
                        onDismiss = { viewModel.setQuickAddType(QuickAddType.NONE) },
                        onConfirm = { title, desc, dueDate, dueTime, estMins, prio, goalId ->
                            viewModel.addNewTask(title, desc, dueDate, dueTime, estMins, prio, goalId)
                        }
                    )
                }
                QuickAddType.HABIT -> {
                    AddHabitDialog(
                        activeGoals = activeGoals.filter { it.isActive },
                        onDismiss = { viewModel.setQuickAddType(QuickAddType.NONE) },
                        onConfirm = { name, icon, color, freq, days, target, time, goalId, isEss ->
                            viewModel.addNewHabit(name, icon, color, freq, days, target, time, goalId, isEss)
                        }
                    )
                }
                QuickAddType.GOAL -> {
                    AddGoalDialog(
                        onDismiss = { viewModel.setQuickAddType(QuickAddType.NONE) },
                        onConfirm = { title, desc, cat, targetDate, color, icon, milestones ->
                            viewModel.addNewGoal(title, desc, cat, targetDate, color, icon, milestones)
                        }
                    )
                }
                QuickAddType.FOCUS -> {
                    viewModel.setQuickAddType(QuickAddType.NONE)
                    viewModel.selectTab(NavigationTab.FOCUS)
                }
                QuickAddType.NOTE -> {
                    AddDailyNoteDialog(
                        currentDate = currentDate,
                        onDismiss = { viewModel.setQuickAddType(QuickAddType.NONE) },
                        onConfirm = { note ->
                            viewModel.saveQuickNote(note)
                        }
                    )
                }
                QuickAddType.NONE -> {
                    // Do nothing
                }
            }

            // Export / AI Report Preview Dialog
            exportedContent?.let { content ->
                ExportPreviewDialog(
                    title = if (content.startsWith("#")) "تقرير الذكاء الاصطناعي (AI Analysis)" else "نسخة احتياطية (JSON)",
                    content = content,
                    onDismiss = { viewModel.clearExportedContent() }
                )
            }
        }
    }
}
