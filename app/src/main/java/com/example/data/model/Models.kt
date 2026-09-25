package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String = "star",
    val colorHex: String = "#10B981",
    val frequencyType: String = "DAILY", // DAILY, SPECIFIC_DAYS, X_TIMES_WEEK
    val daysOfWeek: String = "1,2,3,4,5,6,7", // 1=Sat..7=Fri or custom
    val targetCountPerWeek: Int = 7,
    val preferredTime: String? = null,
    val goalId: String? = null,
    val isActive: Boolean = true,
    val isEssential: Boolean = false, // Essential habits (like Prayer) remain during Rest Day
    val createdAt: Long = System.currentTimeMillis(),
    val pausedAt: Long? = null
)

@Entity(tableName = "habit_completions")
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: String,
    val date: String, // YYYY-MM-DD
    val completedAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = true
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val dueDate: String, // YYYY-MM-DD
    val dueTime: String? = null,
    val estimatedMinutes: Int = 25,
    val priority: String = "MEDIUM", // NONE, LOW, MEDIUM, HIGH
    val goalId: String? = null,
    val listCategory: String = "TODAY", // TODAY, THIS_WEEK, THIS_MONTH, SOMEDAY
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = null,
    val category: String = "عام",
    val listType: String = "MONTHLY", // WEEKLY, MONTHLY, SHORT_TERM, LONG_TERM
    val targetDate: String? = null,
    val colorHex: String = "#8B5CF6",
    val iconName: String = "goal",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goal_milestones")
data class GoalMilestone(
    @PrimaryKey val id: String,
    val goalId: String,
    val title: String,
    val progressPercent: Int = 0, // 0..100
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey val id: String,
    val title: String,
    val date: String, // YYYY-MM-DD
    val durationMinutes: Int, // minutes focused
    val targetDurationMinutes: Int = 25,
    val status: String = "COMPLETED", // COMPLETED, PARTIAL
    val relatedType: String = "NONE", // NONE, TASK, HABIT, GOAL
    val relatedId: String? = null,
    val relatedName: String? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_reviews")
data class DailyReview(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val moodRating: String = "HAPPY", // SAD, NEUTRAL, HAPPY, EXCITED
    val biggestAchievement: String = "",
    val whatWasNotDone: String = "",
    val dailyNote: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rest_days")
data class RestDay(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val isRest: Boolean = true,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "timeline_events")
data class TimelineEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // HABIT_COMPLETED, TASK_COMPLETED, FOCUS_SESSION, GOAL_PROGRESS, REST_DAY, DAILY_NOTE, DAILY_REVIEW
    val title: String,
    val subtitle: String? = null,
    val iconName: String = "check",
    val colorHex: String = "#10B981",
    val referenceId: String? = null
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val startOfWeek: Int = 7, // 7=Sat, 1=Sun, 2=Mon
    val restDaysOfWeek: String = "6", // 6=Fri
    val defaultFocusDuration: Int = 25,
    val defaultBreakDuration: Int = 5,
    val soundAlerts: Boolean = true,
    val autoStartBreak: Boolean = false,
    val isDarkMode: Boolean = true,
    val showHabitProgress: Boolean = true,
    val showTaskProgress: Boolean = true,
    val showGoalProgress: Boolean = true,
    val showFocusTime: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String = "20:00",
    val vaultPin: String? = null,
    val isVaultLocked: Boolean = false
)

@Entity(tableName = "motivational_quotes")
data class MotivationalQuote(
    @PrimaryKey val id: String,
    val text: String,
    val author: String = "حكمة إنجاز",
    val isActive: Boolean = true,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey val id: String,
    val title: String,
    val folder: String = "Notes", // Notes, Habits, Goals, Daily
    val fileName: String,
    val contentMarkdown: String = "",
    val tags: String = "",
    val isPinned: Boolean = false,
    val isLocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
