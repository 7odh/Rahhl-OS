package com.example.data.repository

import com.example.data.local.AchievementDao
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
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AchievementRepository(private val dao: AchievementDao) {

    // Habits
    val allHabits: Flow<List<Habit>> = dao.getAllHabits()
    val activeHabits: Flow<List<Habit>> = dao.getActiveHabits()
    fun getHabitById(id: String): Flow<Habit?> = dao.getHabitById(id)

    suspend fun saveHabit(habit: Habit) = dao.insertHabit(habit)
    suspend fun toggleHabitActive(habit: Habit) {
        val updated = habit.copy(
            isActive = !habit.isActive,
            pausedAt = if (habit.isActive) System.currentTimeMillis() else null
        )
        dao.updateHabit(updated)
    }
    suspend fun deleteHabit(habit: Habit) = dao.deleteHabit(habit)

    // Quotes
    val allQuotes: Flow<List<MotivationalQuote>> = dao.getAllQuotes()
    suspend fun saveQuote(quote: MotivationalQuote) = dao.insertQuote(quote)
    suspend fun updateQuote(quote: MotivationalQuote) = dao.updateQuote(quote)
    suspend fun deleteQuote(quote: MotivationalQuote) = dao.deleteQuote(quote)

    // Vault (Obsidian Markdown Items)
    val allVaultItems: Flow<List<VaultItem>> = dao.getAllVaultItems()
    fun getVaultItemsByFolder(folder: String): Flow<List<VaultItem>> = dao.getVaultItemsByFolder(folder)
    suspend fun saveVaultItem(item: VaultItem) = dao.insertVaultItem(item)
    suspend fun updateVaultItem(item: VaultItem) = dao.updateVaultItem(item)
    suspend fun deleteVaultItem(item: VaultItem) = dao.deleteVaultItem(item)

    // Reset Day Progress
    suspend fun resetTodayProgress(date: String) {
        dao.resetTodayHabitCompletions(date)
        dao.resetTodayTasks(date)
    }

    // TickTick Task Filtering & Rescheduling
    fun getTasksBetween(startDate: String, endDate: String): Flow<List<TaskItem>> =
        dao.getTasksBetween(startDate, endDate)

    suspend fun rescheduleTask(task: TaskItem, newDueDate: String) {
        dao.updateTask(task.copy(dueDate = newDueDate))
    }

    suspend fun moveTaskToCategory(task: TaskItem, newCategory: String) {
        dao.updateTask(task.copy(listCategory = newCategory))
    }

    suspend fun moveGoalToListType(goal: GoalItem, newListType: String) {
        dao.updateGoal(goal.copy(listType = newListType))
    }

    // Habit Completions
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletion>> = dao.getCompletionsForDate(date)
    fun getAllCompletions(): Flow<List<HabitCompletion>> = dao.getAllCompletions()
    fun getCompletionsBetween(startDate: String, endDate: String): Flow<List<HabitCompletion>> =
        dao.getCompletionsBetween(startDate, endDate)

    suspend fun toggleHabitCompletion(habit: Habit, date: String, isCurrentlyCompleted: Boolean) {
        if (isCurrentlyCompleted) {
            dao.deleteCompletion(habit.id, date)
            dao.deleteEventByReference(habit.id, date)
        } else {
            val completion = HabitCompletion(
                habitId = habit.id,
                date = date,
                completedAt = System.currentTimeMillis(),
                isCompleted = true
            )
            dao.insertCompletion(completion)

            // Add timeline event
            val event = TimelineEvent(
                date = date,
                timestamp = System.currentTimeMillis(),
                eventType = "HABIT_COMPLETED",
                title = habit.name,
                subtitle = "عادة مكتملة ✓",
                iconName = habit.iconName,
                colorHex = habit.colorHex,
                referenceId = habit.id
            )
            dao.insertEvent(event)
        }
    }

    // Tasks
    val allTasks: Flow<List<TaskItem>> = dao.getAllTasks()
    val incompleteTasks: Flow<List<TaskItem>> = dao.getIncompleteTasks()
    fun getTasksForDate(date: String): Flow<List<TaskItem>> = dao.getTasksForDate(date)

    suspend fun saveTask(task: TaskItem) = dao.insertTask(task)
    suspend fun deleteTask(task: TaskItem) = dao.deleteTask(task)
    suspend fun toggleTaskCompletion(task: TaskItem) {
        val newStatus = !task.isCompleted
        val updated = task.copy(
            isCompleted = newStatus,
            completedAt = if (newStatus) System.currentTimeMillis() else null
        )
        dao.updateTask(updated)

        if (newStatus) {
            dao.insertEvent(
                TimelineEvent(
                    date = task.dueDate,
                    timestamp = System.currentTimeMillis(),
                    eventType = "TASK_COMPLETED",
                    title = task.title,
                    subtitle = "مهمة منجزة ✓",
                    iconName = "task",
                    colorHex = "#3B82F6",
                    referenceId = task.id
                )
            )
        } else {
            dao.deleteEventByReference(task.id, task.dueDate)
        }
    }

    // Goals & Milestones
    val allGoals: Flow<List<GoalItem>> = dao.getAllGoals()
    val activeGoals: Flow<List<GoalItem>> = dao.getActiveGoals()
    val allMilestones: Flow<List<GoalMilestone>> = dao.getAllMilestones()
    fun getMilestonesForGoal(goalId: String): Flow<List<GoalMilestone>> = dao.getMilestonesForGoal(goalId)

    suspend fun saveGoal(goal: GoalItem, milestones: List<GoalMilestone>) {
        dao.insertGoal(goal)
        for (m in milestones) {
            dao.insertMilestone(m)
        }
    }
    suspend fun updateGoal(goal: GoalItem) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalItem) {
        dao.deleteGoal(goal)
        dao.deleteMilestonesForGoal(goal.id)
    }

    suspend fun saveMilestone(milestone: GoalMilestone) {
        dao.insertMilestone(milestone)
        dao.insertEvent(
            TimelineEvent(
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                timestamp = System.currentTimeMillis(),
                eventType = "GOAL_PROGRESS",
                title = milestone.title,
                subtitle = "تقدم في الهدف: ${milestone.progressPercent}%",
                iconName = "goal",
                colorHex = "#8B5CF6",
                referenceId = milestone.id
            )
        )
    }
    suspend fun deleteMilestone(milestone: GoalMilestone) = dao.deleteMilestone(milestone)

    // Focus Sessions
    val allFocusSessions: Flow<List<FocusSession>> = dao.getAllFocusSessions()
    fun getFocusSessionsForDate(date: String): Flow<List<FocusSession>> = dao.getFocusSessionsForDate(date)
    fun getFocusSessionsBetween(start: String, end: String): Flow<List<FocusSession>> =
        dao.getFocusSessionsBetween(start, end)

    suspend fun saveFocusSession(session: FocusSession) {
        dao.insertFocusSession(session)
        val subtitleText = "${session.durationMinutes} دقيقة" +
                if (!session.relatedName.isNullOrEmpty()) " • ${session.relatedName}" else ""
        dao.insertEvent(
            TimelineEvent(
                date = session.date,
                timestamp = session.endedAt,
                eventType = "FOCUS_SESSION",
                title = session.title,
                subtitle = subtitleText,
                iconName = "timer",
                colorHex = "#F97316",
                referenceId = session.id
            )
        )
    }

    // Daily Review & Note
    fun getDailyReview(date: String): Flow<DailyReview?> = dao.getDailyReview(date)
    val allDailyReviews: Flow<List<DailyReview>> = dao.getAllDailyReviews()

    suspend fun saveDailyReview(review: DailyReview) {
        dao.insertDailyReview(review)
        dao.insertEvent(
            TimelineEvent(
                date = review.date,
                timestamp = System.currentTimeMillis(),
                eventType = "DAILY_REVIEW",
                title = "مراجعة اليوم",
                subtitle = if (review.biggestAchievement.isNotBlank()) review.biggestAchievement else "تم تسجيل المراجعة",
                iconName = "note",
                colorHex = "#06B6D4",
                referenceId = "review_${review.date}"
            )
        )
    }

    suspend fun saveDailyNoteOnly(date: String, note: String) {
        val existing = dao.getDailyReview(date).first()
        val updated = existing?.copy(dailyNote = note, updatedAt = System.currentTimeMillis())
            ?: DailyReview(date = date, dailyNote = note)
        dao.insertDailyReview(updated)

        dao.insertEvent(
            TimelineEvent(
                date = date,
                timestamp = System.currentTimeMillis(),
                eventType = "DAILY_NOTE",
                title = "ملاحظة اليوم",
                subtitle = note.take(50),
                iconName = "edit",
                colorHex = "#F59E0B",
                referenceId = "note_$date"
            )
        )
    }

    // Rest Day
    fun getRestDay(date: String): Flow<RestDay?> = dao.getRestDay(date)
    val allRestDays: Flow<List<RestDay>> = dao.getAllRestDays()

    suspend fun toggleRestDay(date: String, isRest: Boolean, note: String? = null) {
        if (isRest) {
            dao.insertRestDay(RestDay(date = date, isRest = true, note = note))
            dao.insertEvent(
                TimelineEvent(
                    date = date,
                    timestamp = System.currentTimeMillis(),
                    eventType = "REST_DAY",
                    title = "يوم راحة 🏖️",
                    subtitle = note ?: "أخذ قسط من الراحة لشحن الطاقة",
                    iconName = "beach",
                    colorHex = "#06B6D4",
                    referenceId = "rest_$date"
                )
            )
        } else {
            dao.deleteRestDay(date)
            dao.deleteEventByReference("rest_$date", date)
        }
    }

    // Timeline Events
    fun getEventsForDate(date: String): Flow<List<TimelineEvent>> = dao.getEventsForDate(date)
    val allEvents: Flow<List<TimelineEvent>> = dao.getAllEvents()

    // Settings
    val settings: Flow<UserSettings?> = dao.getSettings()
    suspend fun saveSettings(userSettings: UserSettings) = dao.insertSettings(userSettings)

    // Export & Backup
    suspend fun exportFullBackupJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "Achievement")
        root.put("exportedAt", System.currentTimeMillis())

        val habitsArr = JSONArray()
        dao.getHabitsList().forEach { h ->
            val obj = JSONObject()
            obj.put("id", h.id)
            obj.put("name", h.name)
            obj.put("iconName", h.iconName)
            obj.put("colorHex", h.colorHex)
            obj.put("frequencyType", h.frequencyType)
            obj.put("daysOfWeek", h.daysOfWeek)
            obj.put("targetCountPerWeek", h.targetCountPerWeek)
            obj.put("preferredTime", h.preferredTime ?: "")
            obj.put("goalId", h.goalId ?: "")
            obj.put("isActive", h.isActive)
            obj.put("isEssential", h.isEssential)
            obj.put("createdAt", h.createdAt)
            obj.put("pausedAt", h.pausedAt ?: 0L)
            habitsArr.put(obj)
        }
        root.put("habits", habitsArr)

        val completionsArr = JSONArray()
        dao.getCompletionsList().forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("habitId", c.habitId)
            obj.put("date", c.date)
            obj.put("completedAt", c.completedAt)
            obj.put("isCompleted", c.isCompleted)
            completionsArr.put(obj)
        }
        root.put("completions", completionsArr)

        val tasksArr = JSONArray()
        dao.getTasksList().forEach { t ->
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("description", t.description ?: "")
            obj.put("dueDate", t.dueDate)
            obj.put("dueTime", t.dueTime ?: "")
            obj.put("estimatedMinutes", t.estimatedMinutes)
            obj.put("priority", t.priority)
            obj.put("goalId", t.goalId ?: "")
            obj.put("isCompleted", t.isCompleted)
            obj.put("completedAt", t.completedAt ?: 0L)
            obj.put("createdAt", t.createdAt)
            tasksArr.put(obj)
        }
        root.put("tasks", tasksArr)

        val goalsArr = JSONArray()
        dao.getGoalsList().forEach { g ->
            val obj = JSONObject()
            obj.put("id", g.id)
            obj.put("title", g.title)
            obj.put("description", g.description ?: "")
            obj.put("category", g.category)
            obj.put("targetDate", g.targetDate ?: "")
            obj.put("colorHex", g.colorHex)
            obj.put("iconName", g.iconName)
            obj.put("isActive", g.isActive)
            obj.put("createdAt", g.createdAt)
            goalsArr.put(obj)
        }
        root.put("goals", goalsArr)

        val milestonesArr = JSONArray()
        dao.getMilestonesList().forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("goalId", m.goalId)
            obj.put("title", m.title)
            obj.put("progressPercent", m.progressPercent)
            obj.put("isCompleted", m.isCompleted)
            obj.put("orderIndex", m.orderIndex)
            milestonesArr.put(obj)
        }
        root.put("milestones", milestonesArr)

        val focusArr = JSONArray()
        dao.getFocusSessionsList().forEach { f ->
            val obj = JSONObject()
            obj.put("id", f.id)
            obj.put("title", f.title)
            obj.put("date", f.date)
            obj.put("durationMinutes", f.durationMinutes)
            obj.put("targetDurationMinutes", f.targetDurationMinutes)
            obj.put("status", f.status)
            obj.put("relatedType", f.relatedType)
            obj.put("relatedId", f.relatedId ?: "")
            obj.put("relatedName", f.relatedName ?: "")
            obj.put("startedAt", f.startedAt)
            obj.put("endedAt", f.endedAt)
            focusArr.put(obj)
        }
        root.put("focusSessions", focusArr)

        val reviewsArr = JSONArray()
        dao.getDailyReviewsList().forEach { r ->
            val obj = JSONObject()
            obj.put("date", r.date)
            obj.put("moodRating", r.moodRating)
            obj.put("biggestAchievement", r.biggestAchievement)
            obj.put("whatWasNotDone", r.whatWasNotDone)
            obj.put("dailyNote", r.dailyNote)
            obj.put("updatedAt", r.updatedAt)
            reviewsArr.put(obj)
        }
        root.put("dailyReviews", reviewsArr)

        val restArr = JSONArray()
        dao.getRestDaysList().forEach { rd ->
            val obj = JSONObject()
            obj.put("date", rd.date)
            obj.put("isRest", rd.isRest)
            obj.put("note", rd.note ?: "")
            obj.put("createdAt", rd.createdAt)
            restArr.put(obj)
        }
        root.put("restDays", restArr)

        val eventsArr = JSONArray()
        dao.getTimelineEventsList().forEach { te ->
            val obj = JSONObject()
            obj.put("id", te.id)
            obj.put("date", te.date)
            obj.put("timestamp", te.timestamp)
            obj.put("eventType", te.eventType)
            obj.put("title", te.title)
            obj.put("subtitle", te.subtitle ?: "")
            obj.put("iconName", te.iconName)
            obj.put("colorHex", te.colorHex)
            obj.put("referenceId", te.referenceId ?: "")
            eventsArr.put(obj)
        }
        root.put("timelineEvents", eventsArr)

        return root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)

            if (root.has("habits")) {
                dao.clearHabits()
                val habitsArr = root.getJSONArray("habits")
                val habits = mutableListOf<Habit>()
                for (i in 0 until habitsArr.length()) {
                    val o = habitsArr.getJSONObject(i)
                    habits.add(
                        Habit(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            iconName = o.optString("iconName", "star"),
                            colorHex = o.optString("colorHex", "#10B981"),
                            frequencyType = o.optString("frequencyType", "DAILY"),
                            daysOfWeek = o.optString("daysOfWeek", "1,2,3,4,5,6,7"),
                            targetCountPerWeek = o.optInt("targetCountPerWeek", 7),
                            preferredTime = o.optString("preferredTime").takeIf { it.isNotBlank() },
                            goalId = o.optString("goalId").takeIf { it.isNotBlank() },
                            isActive = o.optBoolean("isActive", true),
                            isEssential = o.optBoolean("isEssential", false),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                            pausedAt = if (o.has("pausedAt") && o.getLong("pausedAt") > 0) o.getLong("pausedAt") else null
                        )
                    )
                }
                dao.insertHabits(habits)
            }

            if (root.has("completions")) {
                dao.clearCompletions()
                val compArr = root.getJSONArray("completions")
                val comps = mutableListOf<HabitCompletion>()
                for (i in 0 until compArr.length()) {
                    val o = compArr.getJSONObject(i)
                    comps.add(
                        HabitCompletion(
                            id = o.optLong("id", 0),
                            habitId = o.getString("habitId"),
                            date = o.getString("date"),
                            completedAt = o.optLong("completedAt", System.currentTimeMillis()),
                            isCompleted = o.optBoolean("isCompleted", true)
                        )
                    )
                }
                dao.insertCompletions(comps)
            }

            if (root.has("tasks")) {
                dao.clearTasks()
                val tasksArr = root.getJSONArray("tasks")
                val tasks = mutableListOf<TaskItem>()
                for (i in 0 until tasksArr.length()) {
                    val o = tasksArr.getJSONObject(i)
                    tasks.add(
                        TaskItem(
                            id = o.getString("id"),
                            title = o.getString("title"),
                            description = o.optString("description").takeIf { it.isNotBlank() },
                            dueDate = o.getString("dueDate"),
                            dueTime = o.optString("dueTime").takeIf { it.isNotBlank() },
                            estimatedMinutes = o.optInt("estimatedMinutes", 25),
                            priority = o.optString("priority", "MEDIUM"),
                            goalId = o.optString("goalId").takeIf { it.isNotBlank() },
                            isCompleted = o.optBoolean("isCompleted", false),
                            completedAt = if (o.has("completedAt") && o.getLong("completedAt") > 0) o.getLong("completedAt") else null,
                            createdAt = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                dao.insertTasks(tasks)
            }

            if (root.has("goals")) {
                dao.clearGoals()
                val goalsArr = root.getJSONArray("goals")
                val goals = mutableListOf<GoalItem>()
                for (i in 0 until goalsArr.length()) {
                    val o = goalsArr.getJSONObject(i)
                    goals.add(
                        GoalItem(
                            id = o.getString("id"),
                            title = o.getString("title"),
                            description = o.optString("description").takeIf { it.isNotBlank() },
                            category = o.optString("category", "عام"),
                            targetDate = o.optString("targetDate").takeIf { it.isNotBlank() },
                            colorHex = o.optString("colorHex", "#8B5CF6"),
                            iconName = o.optString("iconName", "goal"),
                            isActive = o.optBoolean("isActive", true),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                dao.insertGoals(goals)
            }

            if (root.has("milestones")) {
                dao.clearMilestones()
                val milestonesArr = root.getJSONArray("milestones")
                val milestones = mutableListOf<GoalMilestone>()
                for (i in 0 until milestonesArr.length()) {
                    val o = milestonesArr.getJSONObject(i)
                    milestones.add(
                        GoalMilestone(
                            id = o.getString("id"),
                            goalId = o.getString("goalId"),
                            title = o.getString("title"),
                            progressPercent = o.optInt("progressPercent", 0),
                            isCompleted = o.optBoolean("isCompleted", false),
                            orderIndex = o.optInt("orderIndex", i)
                        )
                    )
                }
                dao.insertMilestones(milestones)
            }

            if (root.has("focusSessions")) {
                dao.clearFocusSessions()
                val focusArr = root.getJSONArray("focusSessions")
                val list = mutableListOf<FocusSession>()
                for (i in 0 until focusArr.length()) {
                    val o = focusArr.getJSONObject(i)
                    list.add(
                        FocusSession(
                            id = o.getString("id"),
                            title = o.getString("title"),
                            date = o.getString("date"),
                            durationMinutes = o.getInt("durationMinutes"),
                            targetDurationMinutes = o.optInt("targetDurationMinutes", 25),
                            status = o.optString("status", "COMPLETED"),
                            relatedType = o.optString("relatedType", "NONE"),
                            relatedId = o.optString("relatedId").takeIf { it.isNotBlank() },
                            relatedName = o.optString("relatedName").takeIf { it.isNotBlank() },
                            startedAt = o.optLong("startedAt", System.currentTimeMillis()),
                            endedAt = o.optLong("endedAt", System.currentTimeMillis())
                        )
                    )
                }
                dao.insertFocusSessions(list)
            }

            if (root.has("dailyReviews")) {
                dao.clearDailyReviews()
                val revArr = root.getJSONArray("dailyReviews")
                val list = mutableListOf<DailyReview>()
                for (i in 0 until revArr.length()) {
                    val o = revArr.getJSONObject(i)
                    list.add(
                        DailyReview(
                            date = o.getString("date"),
                            moodRating = o.optString("moodRating", "HAPPY"),
                            biggestAchievement = o.optString("biggestAchievement", ""),
                            whatWasNotDone = o.optString("whatWasNotDone", ""),
                            dailyNote = o.optString("dailyNote", ""),
                            updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
                dao.insertDailyReviews(list)
            }

            if (root.has("restDays")) {
                dao.clearRestDays()
                val restArr = root.getJSONArray("restDays")
                val list = mutableListOf<RestDay>()
                for (i in 0 until restArr.length()) {
                    val o = restArr.getJSONObject(i)
                    list.add(
                        RestDay(
                            date = o.getString("date"),
                            isRest = o.optBoolean("isRest", true),
                            note = o.optString("note").takeIf { it.isNotBlank() },
                            createdAt = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                dao.insertRestDays(list)
            }

            if (root.has("timelineEvents")) {
                dao.clearTimelineEvents()
                val eventsArr = root.getJSONArray("timelineEvents")
                val list = mutableListOf<TimelineEvent>()
                for (i in 0 until eventsArr.length()) {
                    val o = eventsArr.getJSONObject(i)
                    list.add(
                        TimelineEvent(
                            id = o.optLong("id", 0),
                            date = o.getString("date"),
                            timestamp = o.optLong("timestamp", System.currentTimeMillis()),
                            eventType = o.getString("eventType"),
                            title = o.getString("title"),
                            subtitle = o.optString("subtitle").takeIf { it.isNotBlank() },
                            iconName = o.optString("iconName", "check"),
                            colorHex = o.optString("colorHex", "#10B981"),
                            referenceId = o.optString("referenceId").takeIf { it.isNotBlank() }
                        )
                    )
                }
                dao.insertTimelineEvents(list)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // AI Markdown Report Generation
    suspend fun generateAiMarkdownReport(startDate: String, endDate: String): String {
        val habits = dao.getHabitsList()
        val completions = dao.getCompletionsList().filter { it.date in startDate..endDate }
        val tasks = dao.getTasksList().filter { it.dueDate in startDate..endDate }
        val goals = dao.getGoalsList()
        val milestones = dao.getMilestonesList()
        val focusSessions = dao.getFocusSessionsList().filter { it.date in startDate..endDate }
        val reviews = dao.getDailyReviewsList().filter { it.date in startDate..endDate }
        val restDays = dao.getRestDaysList().filter { it.date in startDate..endDate }

        val totalHabitsCount = habits.size
        val completedCompletionsCount = completions.size
        val totalExpectedCompletions = (habits.size * 7).coerceAtLeast(1)
        val habitPercent = (completedCompletionsCount * 100) / totalExpectedCompletions

        val completedTasks = tasks.count { it.isCompleted }
        val totalTasks = tasks.size

        val totalFocusMinutes = focusSessions.sumOf { it.durationMinutes }
        val focusHours = totalFocusMinutes / 60
        val focusMins = totalFocusMinutes % 60

        val sb = StringBuilder()
        sb.appendLine("# 📱 ACHIEVEMENT REPORT — تقرير نظام الإنجاز")
        sb.appendLine("")
        sb.appendLine("## 📅 Period / الفترة")
        sb.appendLine("**$startDate → $endDate**")
        sb.appendLine("")
        sb.appendLine("---")
        sb.appendLine("")
        sb.appendLine("## 📊 SUMMARY / نظرة عامة")
        sb.appendLine("- **Habit Completion / إكمال العادات:** $habitPercent% ($completedCompletionsCount إنجازات مسجلة)")
        sb.appendLine("- **Tasks / المهام:** $completedTasks / $totalTasks منجزة")
        sb.appendLine("- **Focus Time / وقت التركيز:** ${focusHours}h ${focusMins}m ($totalFocusMinutes دقيقة)")
        sb.appendLine("- **Rest Days / أيام الراحة:** ${restDays.size} يوم")
        sb.appendLine("")
        sb.appendLine("---")
        sb.appendLine("")
        sb.appendLine("## 🔁 HABITS / العادات والتكرار")
        if (habits.isEmpty()) {
            sb.appendLine("_لا توجد عادات مسجلة_")
        } else {
            habits.forEach { h ->
                val count = completions.count { it.habitId == h.id }
                val status = if (h.isActive) "نشطة" else "متوقفة مؤقتًا"
                sb.appendLine("- **${h.name}**: $count مرات خلال الفترة | الحالة: $status | نوع التكرار: ${h.frequencyType}")
            }
        }
        sb.appendLine("")
        sb.appendLine("---")
        sb.appendLine("")
        sb.appendLine("## 🎯 GOALS & MILESTONES / الأهداف والمراحل")
        if (goals.isEmpty()) {
            sb.appendLine("_لا توجد أهداف مسجلة_")
        } else {
            goals.forEach { g ->
                val gMilestones = milestones.filter { it.goalId == g.id }
                val avgProgress = if (gMilestones.isNotEmpty()) gMilestones.map { it.progressPercent }.average().toInt() else 0
                sb.appendLine("### 🎯 ${g.title} (${g.category}) — إجمالي الإنجاز: $avgProgress%")
                if (gMilestones.isNotEmpty()) {
                    gMilestones.forEach { m ->
                        val check = if (m.isCompleted || m.progressPercent == 100) "✅" else "⏳"
                        sb.appendLine("  - $check ${m.title}: ${m.progressPercent}%")
                    }
                }
            }
        }
        sb.appendLine("")
        sb.appendLine("---")
        sb.appendLine("")
        sb.appendLine("## ⏱️ FOCUS SESSIONS / أين ذهب وقتك؟")
        if (focusSessions.isEmpty()) {
            sb.appendLine("_لا توجد جلسات تركيز مسجلة خلال الفترة_")
        } else {
            val groupedByRelated = focusSessions.groupBy { it.relatedName ?: it.title }
            groupedByRelated.forEach { (cat, sessions) ->
                val mins = sessions.sumOf { it.durationMinutes }
                val h = mins / 60
                val m = mins % 60
                sb.appendLine("- **$cat**: ${h}h ${m}m (${sessions.size} جلسة)")
            }
        }
        sb.appendLine("")
        sb.appendLine("---")
        sb.appendLine("")
        sb.appendLine("## 📝 DAILY REVIEWS & NOTES / مراجعات وملاحظات الأيام")
        if (reviews.isEmpty()) {
            sb.appendLine("_لا توجد مراجعات يومية مسجلة في هذه الفترة_")
        } else {
            reviews.forEach { r ->
                val moodEmoji = when (r.moodRating) {
                    "EXCITED" -> "😄 متحمس وممتاز"
                    "HAPPY" -> "🙂 راضٍ وإيجابي"
                    "NEUTRAL" -> "😐 عادي"
                    else -> "😞 متعب أو محبط"
                }
                sb.appendLine("### 🗓️ ${r.date} — المزاج: $moodEmoji")
                if (r.biggestAchievement.isNotBlank()) sb.appendLine("- **أكبر إنجاز:** ${r.biggestAchievement}")
                if (r.whatWasNotDone.isNotBlank()) sb.appendLine("- **ما لم يتم:** ${r.whatWasNotDone}")
                if (r.dailyNote.isNotBlank()) sb.appendLine("- **ملاحظة:** ${r.dailyNote}")
                sb.appendLine("")
            }
        }
        sb.appendLine("")
        sb.appendLine("---")
        sb.appendLine("")
        sb.appendLine("## 🤖 PROMPT FOR AI COACH / للتحليل مع الذكاء الاصطناعي")
        sb.appendLine("> انسخ هذا النص وشاركه مع ChatGPT أو Claude أو Gemini:")
        sb.appendLine("> \"أنا أستخدم تطبيق Achievement لإدارة يومي وعاداتي وأهدافي. إليك تقرير إنجازي للفترة من $startDate إلى $endDate.")
        sb.appendLine("> أرجو تحليله معي والإجابة على:")
        sb.appendLine("> 1. أين كان الالتزام جيدًا وأين نقاط القوة؟")
        sb.appendLine("> 2. ما هي الثغرات أو الأماكن التي ظهر فيها تراجع؟")
        sb.appendLine("> 3. كيف توزع وقت التركيز وهل يخدم أهدافي الكبرى؟")
        sb.appendLine("> 4. ما هي خطة التحسين العملية الموصى بها للأسبوع القادم؟\"")

        return sb.toString()
    }

    // CSV Exports
    suspend fun generateCsvData(): Map<String, String> {
        val habitsCsv = buildString {
            appendLine("ID,Name,Frequency,IsActive,CreatedAt")
            dao.getHabitsList().forEach {
                appendLine("${it.id},\"${it.name}\",${it.frequencyType},${it.isActive},${it.createdAt}")
            }
        }

        val tasksCsv = buildString {
            appendLine("ID,Title,DueDate,Priority,IsCompleted,EstimatedMinutes")
            dao.getTasksList().forEach {
                appendLine("${it.id},\"${it.title}\",${it.dueDate},${it.priority},${it.isCompleted},${it.estimatedMinutes}")
            }
        }

        val focusCsv = buildString {
            appendLine("ID,Title,Date,DurationMinutes,Status,Related")
            dao.getFocusSessionsList().forEach {
                appendLine("${it.id},\"${it.title}\",${it.date},${it.durationMinutes},${it.status},\"${it.relatedName ?: ""}\"")
            }
        }

        return mapOf(
            "habits.csv" to habitsCsv,
            "tasks.csv" to tasksCsv,
            "focus_sessions.csv" to focusCsv
        )
    }
}
