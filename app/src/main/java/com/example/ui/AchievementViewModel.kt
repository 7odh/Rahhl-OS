package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AchievementDatabase
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
import com.example.data.repository.AchievementRepository
import com.example.data.vault.VaultManager
import com.example.util.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class NavigationTab {
    HOME,
    PROGRESS,
    FOCUS,
    ANALYTICS,
    MORE
}

enum class QuickAddType {
    NONE,
    MENU,
    TASK,
    HABIT,
    GOAL,
    FOCUS,
    NOTE
}

data class TodayProgressSummary(
    val habitsCompleted: Int = 0,
    val habitsTotal: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksTotal: Int = 0,
    val focusMinutes: Int = 0,
    val activeGoalsCount: Int = 0,
    val overallPercent: Int = 0,
    val motivationalMessage: String = "يوم جديد .. فرصة جديدة",
    val isRestDay: Boolean = false
)

class AchievementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AchievementRepository

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))

    private val _currentDate = MutableStateFlow(dateFormat.format(Date()))
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _quickAddType = MutableStateFlow(QuickAddType.NONE)
    val quickAddType: StateFlow<QuickAddType> = _quickAddType.asStateFlow()

    // Timer state
    private val _timerTargetMinutes = MutableStateFlow(25)
    val timerTargetMinutes: StateFlow<Int> = _timerTargetMinutes.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(25 * 60)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerRelatedType = MutableStateFlow("NONE")
    val timerRelatedType: StateFlow<String> = _timerRelatedType.asStateFlow()

    private val _timerRelatedId = MutableStateFlow<String?>(null)
    val timerRelatedId: StateFlow<String?> = _timerRelatedId.asStateFlow()

    private val _timerRelatedName = MutableStateFlow<String?>("دراسة English")
    val timerRelatedName: StateFlow<String?> = _timerRelatedName.asStateFlow()

    private var timerJob: Job? = null
    private var timerStartTimestamp: Long = 0
    private var initialSessionTargetSeconds: Int = 25 * 60

    // Database flows
    val allHabits: StateFlow<List<Habit>>
    val allTasks: StateFlow<List<TaskItem>>
    val allGoals: StateFlow<List<GoalItem>>
    val allMilestones: StateFlow<List<GoalMilestone>>
    val allFocusSessions: StateFlow<List<FocusSession>>
    val allRestDays: StateFlow<List<RestDay>>
    val allDailyReviews: StateFlow<List<DailyReview>>
    val userSettings: StateFlow<UserSettings?>

    // Date dependent flows
    val todayCompletions: StateFlow<List<HabitCompletion>>
    val todayTasks: StateFlow<List<TaskItem>>
    val todayFocusSessions: StateFlow<List<FocusSession>>
    val todayRestDay: StateFlow<RestDay?>
    val todayDailyReview: StateFlow<DailyReview?>
    val todayEvents: StateFlow<List<TimelineEvent>>

    // Combined summary for Today card
    val todaySummary: StateFlow<TodayProgressSummary>

    // Vault & Quotes
    val vaultManager: VaultManager
    val allQuotes: StateFlow<List<MotivationalQuote>>
    val allVaultItems: StateFlow<List<VaultItem>>

    private val _quoteIndex = MutableStateFlow(0)
    val quoteIndex: StateFlow<Int> = _quoteIndex.asStateFlow()

    // TickTick Calendar
    private val _calendarSelectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val calendarSelectedDate: StateFlow<String> = _calendarSelectedDate.asStateFlow()
    val calendarTasks: StateFlow<List<TaskItem>>

    // Filters & Sorting
    val taskPriorityFilter = MutableStateFlow("ALL") // ALL, HIGH, MEDIUM, LOW
    val taskSortOrder = MutableStateFlow("PRIORITY") // PRIORITY, DUE_DATE, COMPLETED
    val goalListFilter = MutableStateFlow("ALL") // ALL, WEEKLY, MONTHLY, SHORT_TERM, LONG_TERM
    val taskCategoryFilter = MutableStateFlow("ALL") // ALL, TODAY, THIS_WEEK, THIS_MONTH, SOMEDAY

    // Dialog state controls
    val isResetDialogOpen = MutableStateFlow(false)
    val isQuotesDialogOpen = MutableStateFlow(false)
    val isVaultDialogOpen = MutableStateFlow(false)
    val isCalendarDialogOpen = MutableStateFlow(false)
    val isVaultUnlocked = MutableStateFlow(true)

    // Backup & Export status messages
    private val _actionFeedback = MutableStateFlow<String?>(null)
    val actionFeedback: StateFlow<String?> = _actionFeedback.asStateFlow()

    private val _exportedContent = MutableStateFlow<String?>(null)
    val exportedContent: StateFlow<String?> = _exportedContent.asStateFlow()

    init {
        val database = AchievementDatabase.getDatabase(application, viewModelScope)
        repository = AchievementRepository(database.achievementDao())
        vaultManager = VaultManager(application, database.achievementDao())

        allQuotes = repository.allQuotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allVaultItems = repository.allVaultItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        calendarTasks = _calendarSelectedDate.flatMapLatest { date ->
            repository.getTasksForDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allHabits = repository.allHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allTasks = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allGoals = repository.allGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allMilestones = repository.allMilestones.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allFocusSessions = repository.allFocusSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allRestDays = repository.allRestDays.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allDailyReviews = repository.allDailyReviews.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        userSettings = repository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        todayCompletions = _currentDate.flatMapLatest { date ->
            repository.getCompletionsForDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        todayTasks = _currentDate.flatMapLatest { date ->
            repository.getTasksForDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        todayFocusSessions = _currentDate.flatMapLatest { date ->
            repository.getFocusSessionsForDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        todayRestDay = _currentDate.flatMapLatest { date ->
            repository.getRestDay(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        todayDailyReview = _currentDate.flatMapLatest { date ->
            repository.getDailyReview(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        todayEvents = _currentDate.flatMapLatest { date ->
            repository.getEventsForDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val habitsAndTasksFlow = combine(allHabits, todayCompletions, todayTasks) { habits, completions, tasks ->
            Triple(habits, completions, tasks)
        }
        val focusRestGoalsFlow = combine(todayFocusSessions, todayRestDay, allGoals) { focus, restDay, goals ->
            Triple(focus, restDay, goals)
        }

        todaySummary = combine(habitsAndTasksFlow, focusRestGoalsFlow) { (habits, completions, tasks), (focus, restDay, goals) ->
            val isRest = restDay?.isRest == true
            // If rest day, non-essential habits don't count towards pressure
            val relevantHabits = if (isRest) habits.filter { it.isActive && it.isEssential } else habits.filter { it.isActive }
            val completedHabitIds = completions.map { it.habitId }.toSet()
            val habitsDone = relevantHabits.count { completedHabitIds.contains(it.id) }
            val habitsTotal = relevantHabits.size

            val tasksDone = tasks.count { it.isCompleted }
            val tasksTotal = tasks.size

            val focusMins = focus.sumOf { it.durationMinutes }

            val totalItems = (habitsTotal + tasksTotal).coerceAtLeast(1)
            val doneItems = habitsDone + tasksDone
            val percent = (doneItems * 100) / totalItems

            val message = when {
                isRest -> "اليوم راحة 🏖️ شحن الطاقة جزء أساسي من النجاح"
                percent >= 90 -> "إنجاز مبهر اليوم! واصل بهذا التألق ✨"
                percent >= 60 -> "أحسنت! أنت على الطريق الصحيح 🚀"
                percent >= 30 -> "خطوات جيدة اليوم.. استمر بتركيزك 💪"
                else -> "لسه اليوم فيه وقت.. ابدأ بخطوة صغيرة الآن 🌱"
            }

            TodayProgressSummary(
                habitsCompleted = habitsDone,
                habitsTotal = habitsTotal,
                tasksCompleted = tasksDone,
                tasksTotal = tasksTotal,
                focusMinutes = focusMins,
                activeGoalsCount = goals.count { it.isActive },
                overallPercent = percent,
                motivationalMessage = message,
                isRestDay = isRest
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayProgressSummary())
    }

    fun setDate(date: String) {
        _currentDate.value = date
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setQuickAddType(type: QuickAddType) {
        _quickAddType.value = type
    }

    fun clearFeedback() {
        _actionFeedback.value = null
    }

    fun clearExportedContent() {
        _exportedContent.value = null
    }

    // Toggle Habit
    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val isDone = todayCompletions.value.any { it.habitId == habit.id }
            repository.toggleHabitCompletion(habit, _currentDate.value, isDone)
        }
    }

    // Toggle Task
    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    // Toggle Rest Day
    fun toggleRestDay(note: String? = null) {
        viewModelScope.launch {
            val currentlyRest = todayRestDay.value?.isRest == true
            repository.toggleRestDay(_currentDate.value, !currentlyRest, note)
            _actionFeedback.value = if (!currentlyRest) "تم تفعيل وضع الراحة لهذا اليوم 🏖️" else "تم إلغاء وضع الراحة"
        }
    }

    // Quick Add Actions
    fun addNewHabit(
        name: String,
        iconName: String,
        colorHex: String,
        frequencyType: String,
        daysOfWeek: String,
        targetCount: Int,
        preferredTime: String?,
        goalId: String?,
        isEssential: Boolean
    ) {
        viewModelScope.launch {
            val id = "H" + UUID.randomUUID().toString().take(6).uppercase()
            val habit = Habit(
                id = id,
                name = name,
                iconName = iconName,
                colorHex = colorHex,
                frequencyType = frequencyType,
                daysOfWeek = daysOfWeek,
                targetCountPerWeek = targetCount,
                preferredTime = preferredTime?.takeIf { it.isNotBlank() },
                goalId = goalId?.takeIf { it.isNotBlank() },
                isActive = true,
                isEssential = isEssential
            )
            repository.saveHabit(habit)
            _actionFeedback.value = "تمت إضافة العادة بنجاح"
            _quickAddType.value = QuickAddType.NONE
        }
    }

    fun addNewTask(
        title: String,
        description: String?,
        dueDate: String,
        dueTime: String?,
        estimatedMinutes: Int,
        priority: String,
        goalId: String?
    ) {
        viewModelScope.launch {
            val id = "T" + UUID.randomUUID().toString().take(6).uppercase()
            val task = TaskItem(
                id = id,
                title = title,
                description = description?.takeIf { it.isNotBlank() },
                dueDate = dueDate,
                dueTime = dueTime?.takeIf { it.isNotBlank() },
                estimatedMinutes = estimatedMinutes,
                priority = priority,
                goalId = goalId?.takeIf { it.isNotBlank() }
            )
            repository.saveTask(task)
            _actionFeedback.value = "تمت إضافة المهمة بنجاح"
            _quickAddType.value = QuickAddType.NONE
        }
    }

    fun addNewGoal(
        title: String,
        description: String?,
        category: String,
        targetDate: String?,
        colorHex: String,
        iconName: String,
        milestones: List<String>
    ) {
        viewModelScope.launch {
            val goalId = "G" + UUID.randomUUID().toString().take(6).uppercase()
            val goal = GoalItem(
                id = goalId,
                title = title,
                description = description?.takeIf { it.isNotBlank() },
                category = category,
                targetDate = targetDate?.takeIf { it.isNotBlank() },
                colorHex = colorHex,
                iconName = iconName,
                isActive = true
            )
            val milestoneEntities = milestones.mapIndexed { index, mTitle ->
                GoalMilestone(
                    id = "M" + UUID.randomUUID().toString().take(6).uppercase(),
                    goalId = goalId,
                    title = mTitle,
                    progressPercent = 0,
                    isCompleted = false,
                    orderIndex = index
                )
            }
            repository.saveGoal(goal, milestoneEntities)
            _actionFeedback.value = "تم إنشاء الهدف والمراحل بنجاح"
            _quickAddType.value = QuickAddType.NONE
        }
    }

    fun updateMilestoneProgress(milestone: GoalMilestone, newPercent: Int) {
        viewModelScope.launch {
            val updated = milestone.copy(
                progressPercent = newPercent.coerceIn(0, 100),
                isCompleted = newPercent >= 100
            )
            repository.saveMilestone(updated)
        }
    }

    fun toggleHabitActiveState(habit: Habit) {
        viewModelScope.launch {
            repository.toggleHabitActive(habit)
            _actionFeedback.value = if (habit.isActive) "تم إيقاف العادة مؤقتًا (تاريخها محفوظ)" else "تمت إعادة تفعيل العادة"
        }
    }

    fun saveDailyReview(mood: String, biggestAch: String, notDone: String, note: String) {
        viewModelScope.launch {
            val review = DailyReview(
                date = _currentDate.value,
                moodRating = mood,
                biggestAchievement = biggestAch,
                whatWasNotDone = notDone,
                dailyNote = note,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveDailyReview(review)
            _actionFeedback.value = "تم حفظ مراجعة اليوم بنجاح ✓"
            _quickAddType.value = QuickAddType.NONE
        }
    }

    fun saveQuickNote(note: String) {
        viewModelScope.launch {
            repository.saveDailyNoteOnly(_currentDate.value, note)
            _actionFeedback.value = "تم حفظ ملاحظة اليوم ✓"
            _quickAddType.value = QuickAddType.NONE
        }
    }

    // Pomodoro Timer Logic
    fun setTimerDuration(minutes: Int) {
        if (!_isTimerRunning.value) {
            _timerTargetMinutes.value = minutes
            _timerRemainingSeconds.value = minutes * 60
            initialSessionTargetSeconds = minutes * 60
        }
    }

    fun setTimerLink(type: String, id: String?, name: String?) {
        _timerRelatedType.value = type
        _timerRelatedId.value = id
        _timerRelatedName.value = name
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerStartTimestamp = System.currentTimeMillis()

        timerJob = viewModelScope.launch {
            while (_timerRemainingSeconds.value > 0 && _isTimerRunning.value) {
                delay(1000L)
                _timerRemainingSeconds.value -= 1
            }

            if (_timerRemainingSeconds.value <= 0) {
                // Completed session!
                finishSession(isCompleted = true)
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun stopTimerAndSave() {
        _isTimerRunning.value = false
        timerJob?.cancel()

        val elapsedSeconds = initialSessionTargetSeconds - _timerRemainingSeconds.value
        val elapsedMinutes = elapsedSeconds / 60

        if (elapsedMinutes >= 1) {
            finishSession(isCompleted = false, elapsedMinutes = elapsedMinutes)
        } else {
            // Reset timer
            _timerRemainingSeconds.value = _timerTargetMinutes.value * 60
            _actionFeedback.value = "تم إلغاء الجلسة (أقل من دقيقة)"
        }
    }

    private fun finishSession(isCompleted: Boolean, elapsedMinutes: Int? = null) {
        val finalMinutes = elapsedMinutes ?: _timerTargetMinutes.value
        val sessionId = "FS" + UUID.randomUUID().toString().take(6).uppercase()
        val title = if (!timerRelatedName.value.isNullOrBlank()) {
            "جلسة تركيز ${timerRelatedName.value}"
        } else {
            "جلسة تركيز"
        }

        val session = FocusSession(
            id = sessionId,
            title = title,
            date = _currentDate.value,
            durationMinutes = finalMinutes,
            targetDurationMinutes = _timerTargetMinutes.value,
            status = if (isCompleted) "COMPLETED" else "PARTIAL",
            relatedType = _timerRelatedType.value,
            relatedId = _timerRelatedId.value,
            relatedName = _timerRelatedName.value,
            startedAt = timerStartTimestamp,
            endedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.saveFocusSession(session)
            _actionFeedback.value = if (isCompleted) {
                "جلسة تركيز مكتملة بنجاح ✅ ($finalMinutes دقيقة)"
            } else {
                "تم حفظ $finalMinutes دقيقة وقت تركيز (جلسة جزئية) ⏱️"
            }
            // Reset timer
            _timerRemainingSeconds.value = _timerTargetMinutes.value * 60
            _isTimerRunning.value = false
        }
    }

    // Export & Backup
    fun exportBackupJson() {
        viewModelScope.launch {
            val json = repository.exportFullBackupJson()
            _exportedContent.value = json
            _actionFeedback.value = "تم تجهيز النسخة الاحتياطية JSON بنجاح"
        }
    }

    fun importBackup(jsonString: String) {
        viewModelScope.launch {
            val success = repository.importBackupJson(jsonString)
            _actionFeedback.value = if (success) "تمت استعادة البيانات بنجاح!" else "خطأ في ملف النسخة الاحتياطية"
        }
    }

    fun exportAiReport(daysBack: Int = 7) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val endStr = dateFormat.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, -daysBack)
            val startStr = dateFormat.format(cal.time)

            val report = repository.generateAiMarkdownReport(startStr, endStr)
            _exportedContent.value = report
            _actionFeedback.value = "تم توليد تقرير AI بنجاح 🤖"
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            repository.saveSettings(settings)
            _actionFeedback.value = "تم حفظ الإعدادات"
        }
    }

    // Quotes Management
    fun cycleNextQuote(quotesSize: Int) {
        if (quotesSize > 0) {
            _quoteIndex.value = (_quoteIndex.value + 1) % quotesSize
        }
    }

    fun addQuote(text: String, author: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val quote = MotivationalQuote(
                id = "Q_${UUID.randomUUID().toString().take(8)}",
                text = text.trim(),
                author = author.trim().ifBlank { "حكمة إنجاز" },
                isActive = true
            )
            repository.saveQuote(quote)
            _actionFeedback.value = "تمت إضافة العبارة التحفيزية بنجاح ✨"
        }
    }

    fun updateQuote(quote: MotivationalQuote) {
        viewModelScope.launch {
            repository.updateQuote(quote)
            _actionFeedback.value = "تم تحديث العبارة"
        }
    }

    fun deleteQuote(quote: MotivationalQuote) {
        viewModelScope.launch {
            repository.deleteQuote(quote)
            _actionFeedback.value = "تم حذف العبارة"
        }
    }

    // Reset Day Progress
    fun resetTodayProgress() {
        viewModelScope.launch {
            val date = _currentDate.value
            repository.resetTodayProgress(date)
            _actionFeedback.value = "تمت إعادة ضبط إنجاز اليوم بنجاح 🔄"
            isResetDialogOpen.value = false
        }
    }

    // TickTick Calendar & Rescheduling
    fun setCalendarDate(date: String) {
        _calendarSelectedDate.value = date
    }

    fun rescheduleTask(task: TaskItem, newDueDate: String) {
        viewModelScope.launch {
            repository.rescheduleTask(task, newDueDate)
            _actionFeedback.value = "تم نقل المهمة إلى: $newDueDate 🗓️"
        }
    }

    fun moveTaskCategory(task: TaskItem, newCategory: String) {
        viewModelScope.launch {
            repository.moveTaskToCategory(task, newCategory)
            _actionFeedback.value = "تم نقل المهمة إلى القائمة"
        }
    }

    fun moveGoalListType(goal: GoalItem, newListType: String) {
        viewModelScope.launch {
            repository.moveGoalToListType(goal, newListType)
            _actionFeedback.value = "تم نقل الهدف بنجاح 🎯"
        }
    }

    // Obsidian Vault
    fun syncVaultToMarkdown() {
        viewModelScope.launch {
            val result = vaultManager.syncAllToMarkdown()
            result.onSuccess { count ->
                _actionFeedback.value = "تمت مزامنة وتصدير $count ملف إلى خزنة Obsidian بنجاح 📂"
            }.onFailure { err ->
                _actionFeedback.value = "خطأ في مزامنة الخزنة: ${err.message}"
            }
        }
    }

    fun addVaultNote(title: String, folder: String, content: String, tags: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val item = VaultItem(
                id = "V_${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                folder = folder.trim().ifBlank { "Notes" },
                fileName = "${title.replace(' ', '_')}.md",
                contentMarkdown = content,
                tags = tags
            )
            repository.saveVaultItem(item)
            _actionFeedback.value = "تم حفظ المذكرة في الخزنة 📝"
        }
    }

    fun deleteVaultNote(item: VaultItem) {
        viewModelScope.launch {
            repository.deleteVaultItem(item)
            _actionFeedback.value = "تم حذف المذكرة من الخزنة"
        }
    }

    // Notifications
    fun testSendNotification() {
        NotificationHelper.showNotification(
            context = getApplication(),
            id = 9999,
            channelId = NotificationHelper.CHANNEL_REMINDERS,
            title = "🔔 تجربة نظام التنبيهات",
            content = "التنبيهات تعمل بنجاح في تطبيق Achievement! خطوتك الصغيرة تصنع إنجازك الكبير."
        )
        _actionFeedback.value = "تم إرسال إشعار تجريبي بنجاح 🔔"
    }
}
