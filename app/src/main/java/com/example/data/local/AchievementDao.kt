package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyReview
import com.example.data.model.FocusSession
import com.example.data.model.GoalItem
import com.example.data.model.GoalMilestone
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.MotivationalQuote
import com.example.data.model.RestDay
import com.example.data.model.TaskItem
import com.example.data.model.TimelineEvent
import com.example.data.model.UserSettings
import com.example.data.model.VaultItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    // Habits
    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt ASC")
    fun getActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun getHabitById(id: String): Flow<Habit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // Habit Completions
    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions ORDER BY completedAt DESC")
    fun getAllCompletions(): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE date >= :startDate AND date <= :endDate")
    fun getCompletionsBetween(startDate: String, endDate: String): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: String, date: String)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY priority DESC")
    fun getTasksForDate(date: String): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getIncompleteTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: String): Flow<TaskItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    // Goals
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalItem>>

    @Query("SELECT * FROM goals WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveGoals(): Flow<List<GoalItem>>

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    fun getGoalById(id: String): Flow<GoalItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalItem)

    @Update
    suspend fun updateGoal(goal: GoalItem)

    @Delete
    suspend fun deleteGoal(goal: GoalItem)

    // Goal Milestones
    @Query("SELECT * FROM goal_milestones WHERE goalId = :goalId ORDER BY orderIndex ASC")
    fun getMilestonesForGoal(goalId: String): Flow<List<GoalMilestone>>

    @Query("SELECT * FROM goal_milestones ORDER BY orderIndex ASC")
    fun getAllMilestones(): Flow<List<GoalMilestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: GoalMilestone)

    @Update
    suspend fun updateMilestone(milestone: GoalMilestone)

    @Delete
    suspend fun deleteMilestone(milestone: GoalMilestone)

    @Query("DELETE FROM goal_milestones WHERE goalId = :goalId")
    suspend fun deleteMilestonesForGoal(goalId: String)

    // Focus Sessions
    @Query("SELECT * FROM focus_sessions ORDER BY endedAt DESC")
    fun getAllFocusSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY endedAt DESC")
    fun getFocusSessionsForDate(date: String): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE date >= :startDate AND date <= :endDate ORDER BY endedAt DESC")
    fun getFocusSessionsBetween(startDate: String, endDate: String): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession)

    @Delete
    suspend fun deleteFocusSession(session: FocusSession)

    // Daily Review
    @Query("SELECT * FROM daily_reviews WHERE date = :date LIMIT 1")
    fun getDailyReview(date: String): Flow<DailyReview?>

    @Query("SELECT * FROM daily_reviews ORDER BY date DESC")
    fun getAllDailyReviews(): Flow<List<DailyReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReview(review: DailyReview)

    // Rest Days
    @Query("SELECT * FROM rest_days WHERE date = :date LIMIT 1")
    fun getRestDay(date: String): Flow<RestDay?>

    @Query("SELECT * FROM rest_days ORDER BY date DESC")
    fun getAllRestDays(): Flow<List<RestDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestDay(restDay: RestDay)

    @Query("DELETE FROM rest_days WHERE date = :date")
    suspend fun deleteRestDay(date: String)

    // Timeline Events
    @Query("SELECT * FROM timeline_events WHERE date = :date ORDER BY timestamp ASC")
    fun getEventsForDate(date: String): Flow<List<TimelineEvent>>

    @Query("SELECT * FROM timeline_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<TimelineEvent>>

    @Query("SELECT * FROM timeline_events WHERE date >= :startDate AND date <= :endDate ORDER BY timestamp ASC")
    fun getEventsBetween(startDate: String, endDate: String): Flow<List<TimelineEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TimelineEvent)

    @Query("DELETE FROM timeline_events WHERE referenceId = :refId AND date = :date")
    suspend fun deleteEventByReference(refId: String, date: String)

    // Settings
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings)

    // Raw full queries for export/backup
    @Query("SELECT * FROM habits")
    suspend fun getHabitsList(): List<Habit>

    @Query("SELECT * FROM habit_completions")
    suspend fun getCompletionsList(): List<HabitCompletion>

    @Query("SELECT * FROM tasks")
    suspend fun getTasksList(): List<TaskItem>

    @Query("SELECT * FROM goals")
    suspend fun getGoalsList(): List<GoalItem>

    @Query("SELECT * FROM goal_milestones")
    suspend fun getMilestonesList(): List<GoalMilestone>

    @Query("SELECT * FROM focus_sessions")
    suspend fun getFocusSessionsList(): List<FocusSession>

    @Query("SELECT * FROM daily_reviews")
    suspend fun getDailyReviewsList(): List<DailyReview>

    @Query("SELECT * FROM rest_days")
    suspend fun getRestDaysList(): List<RestDay>

    @Query("SELECT * FROM timeline_events")
    suspend fun getTimelineEventsList(): List<TimelineEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(items: List<Habit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletions(items: List<HabitCompletion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(items: List<TaskItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(items: List<GoalItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(items: List<GoalMilestone>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSessions(items: List<FocusSession>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReviews(items: List<DailyReview>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestDays(items: List<RestDay>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineEvents(items: List<TimelineEvent>)

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("DELETE FROM habit_completions")
    suspend fun clearCompletions()

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Query("DELETE FROM goals")
    suspend fun clearGoals()

    @Query("DELETE FROM goal_milestones")
    suspend fun clearMilestones()

    @Query("DELETE FROM focus_sessions")
    suspend fun clearFocusSessions()

    @Query("DELETE FROM daily_reviews")
    suspend fun clearDailyReviews()

    @Query("DELETE FROM rest_days")
    suspend fun clearRestDays()

    @Query("DELETE FROM timeline_events")
    suspend fun clearTimelineEvents()

    // Quotes
    @Query("SELECT * FROM motivational_quotes ORDER BY orderIndex ASC, createdAt DESC")
    fun getAllQuotes(): Flow<List<MotivationalQuote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: MotivationalQuote)

    @Update
    suspend fun updateQuote(quote: MotivationalQuote)

    @Delete
    suspend fun deleteQuote(quote: MotivationalQuote)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<MotivationalQuote>)

    @Query("DELETE FROM motivational_quotes")
    suspend fun clearQuotes()

    // Vault Items
    @Query("SELECT * FROM vault_items ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE folder = :folder ORDER BY isPinned DESC, updatedAt DESC")
    fun getVaultItemsByFolder(folder: String): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE id = :id LIMIT 1")
    fun getVaultItemById(id: String): Flow<VaultItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultItem)

    @Update
    suspend fun updateVaultItem(item: VaultItem)

    @Delete
    suspend fun deleteVaultItem(item: VaultItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItems(items: List<VaultItem>)

    @Query("DELETE FROM vault_items")
    suspend fun clearVaultItems()

    // TickTick Calendar & Date Filtering
    @Query("SELECT * FROM tasks WHERE dueDate >= :startDate AND dueDate <= :endDate ORDER BY dueDate ASC, priority DESC")
    fun getTasksBetween(startDate: String, endDate: String): Flow<List<TaskItem>>

    // Reset Today Action
    @Query("DELETE FROM habit_completions WHERE date = :date")
    suspend fun resetTodayHabitCompletions(date: String)

    @Query("UPDATE tasks SET isCompleted = 0, completedAt = NULL WHERE dueDate = :date")
    suspend fun resetTodayTasks(date: String)
}
