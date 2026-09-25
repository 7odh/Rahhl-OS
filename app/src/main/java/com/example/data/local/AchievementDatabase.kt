package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        Habit::class,
        HabitCompletion::class,
        TaskItem::class,
        GoalItem::class,
        GoalMilestone::class,
        FocusSession::class,
        DailyReview::class,
        RestDay::class,
        TimelineEvent::class,
        UserSettings::class,
        MotivationalQuote::class,
        VaultItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AchievementDatabase : RoomDatabase() {

    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AchievementDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AchievementDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AchievementDatabase::class.java,
                    "achievement_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.achievementDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: AchievementDao) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = dateFormat.format(Date())

            // Default Settings
            dao.insertSettings(
                UserSettings(
                    id = 1,
                    startOfWeek = 7, // Saturday
                    restDaysOfWeek = "6", // Friday
                    defaultFocusDuration = 25,
                    defaultBreakDuration = 5,
                    soundAlerts = true,
                    isDarkMode = true
                )
            )

            // Initial Goals
            val goal1 = GoalItem(
                id = "G001",
                title = "تعلم اللغة الإنجليزية B1",
                description = "الوصول إلى مستوى B1 في اللغة الإنجليزية للمحادثة والعمل",
                category = "لغات",
                targetDate = "2026-12-31",
                colorHex = "#3B82F6",
                iconName = "language"
            )
            val goal2 = GoalItem(
                id = "G002",
                title = "تعلم البرمجة والتطوير",
                description = "بناء تطبيقات ومشاريع تقنية متقدمة",
                category = "تقنية",
                targetDate = "2027-01-01",
                colorHex = "#8B5CF6",
                iconName = "code"
            )
            dao.insertGoal(goal1)
            dao.insertGoal(goal2)

            // Goal Milestones
            dao.insertMilestone(GoalMilestone("M001", "G001", "A1", 100, true, 0))
            dao.insertMilestone(GoalMilestone("M002", "G001", "A2", 70, false, 1))
            dao.insertMilestone(GoalMilestone("M003", "G001", "B1", 40, false, 2))

            dao.insertMilestone(GoalMilestone("M004", "G002", "مستوى مبتدئ", 100, true, 0))
            dao.insertMilestone(GoalMilestone("M005", "G002", "مستوى متوسط", 50, false, 1))
            dao.insertMilestone(GoalMilestone("M006", "G002", "مستوى متقدم", 20, false, 2))

            // Initial Habits
            val habits = listOf(
                Habit("H001", "الصلاة", "prayer", "#10B981", "DAILY", "1,2,3,4,5,6,7", 7, null, null, true, true),
                Habit("H002", "قراءة القرآن", "book", "#F59E0B", "DAILY", "1,2,3,4,5,6,7", 7, "بعد الفجر", null, true, true),
                Habit("H003", "التمرين", "fitness", "#EC4899", "X_TIMES_WEEK", "1,2,3,4,5,6,7", 3, null, null, true, false),
                Habit("H004", "Busuu", "language", "#3B82F6", "DAILY", "1,2,3,4,5,6,7", 7, "20:00", "G001", true, false),
                Habit("H005", "المذاكرة", "study", "#10B981", "DAILY", "1,2,3,4,5,6,7", 7, null, "G002", true, false)
            )
            dao.insertHabits(habits)

            // Seed completions for today
            dao.insertCompletion(HabitCompletion(0, "H001", todayStr, System.currentTimeMillis() - 7200000, true))
            dao.insertCompletion(HabitCompletion(0, "H002", todayStr, System.currentTimeMillis() - 5400000, true))
            dao.insertCompletion(HabitCompletion(0, "H003", todayStr, System.currentTimeMillis() - 3600000, true))

            // Initial Tasks
            val tasks = listOf(
                TaskItem("T001", "دراسة English", "مراجعة قواعد الدرس الخامس في Busuu", todayStr, "09:00", 25, "HIGH", "G001", "TODAY", false),
                TaskItem("T002", "تطوير الواجهة", "إكمال تصميم شاشة الإحصائيات", todayStr, "11:00", 60, "MEDIUM", "G002", "TODAY", false),
                TaskItem("T003", "شراء كابل USB", "من المتجر القريب", todayStr, "17:00", 10, "LOW", null, "TODAY", false),
                TaskItem("T004", "مراجعة الدرس", "تلخيص النقاط الأساسية", todayStr, "21:00", 30, "MEDIUM", "G001", "TODAY", false)
            )
            dao.insertTasks(tasks)

            // Initial Focus Session
            val session1 = FocusSession(
                id = "FS001",
                title = "جلسة تركيز English",
                date = todayStr,
                durationMinutes = 25,
                targetDurationMinutes = 25,
                status = "COMPLETED",
                relatedType = "GOAL",
                relatedId = "G001",
                relatedName = "تعلم اللغة الإنجليزية B1",
                startedAt = System.currentTimeMillis() - 10000000,
                endedAt = System.currentTimeMillis() - 10000000 + 1500000
            )
            dao.insertFocusSession(session1)

            // Initial Timeline Events
            dao.insertEvent(TimelineEvent(0, todayStr, System.currentTimeMillis() - 7200000, "HABIT_COMPLETED", "الصلاة", "تمت بنجاح", "prayer", "#10B981", "H001"))
            dao.insertEvent(TimelineEvent(0, todayStr, System.currentTimeMillis() - 6000000, "FOCUS_SESSION", "جلسة تركيز English", "25 دقيقة • تعلم اللغة الإنجليزية B1", "timer", "#F97316", "FS001"))
            dao.insertEvent(TimelineEvent(0, todayStr, System.currentTimeMillis() - 5400000, "HABIT_COMPLETED", "قراءة القرآن", "بعد الفجر", "book", "#F59E0B", "H002"))
            dao.insertEvent(TimelineEvent(0, todayStr, System.currentTimeMillis() - 3600000, "HABIT_COMPLETED", "التمرين", "تمت بنجاح", "fitness", "#EC4899", "H003"))

            // Initial Motivational Quotes
            val quotes = listOf(
                MotivationalQuote("Q001", "خطوتك الصغيرة اليوم تصنع إنجازك الكبير غداً.", "حكمة إنجاز", true, 0),
                MotivationalQuote("Q002", "الانضباط هو الجسر بين الأهداف وتحقيقها.", "جيم رون", true, 1),
                MotivationalQuote("Q003", "ركز على اليوم فقط، واجعل خطواتك ثابتة ومستمرة.", "حكمة يومية", true, 2),
                MotivationalQuote("Q004", "الاستمرار والتراكم يصنعان المعجزات التي يعجز عنها الحماس المؤقت.", "عادات ذرية", true, 3),
                MotivationalQuote("Q005", "كل يوم جديد هو فرصة ثانية لكتابة قصة إنجازك.", "تحفيز", true, 4)
            )
            dao.insertQuotes(quotes)

            // Initial Vault Markdown Notes (Obsidian-Style)
            val vaultItems = listOf(
                VaultItem(
                    id = "V001",
                    title = "خريطة تعلم الإنجليزية B1",
                    folder = "Goals",
                    fileName = "english_b1_roadmap.md",
                    contentMarkdown = """
                    # خريطة تعلم الإنجليزية B1
                    
                    - [x] إنهاء مستوى A1 بنجاح
                    - [x] إتمام 70% من مستوى A2
                    - [ ] الوصول إلى مستوى B1
                    
                    ## الاستراتيجية اليومية
                    - 20 دقيقة تطبيق Busuu
                    - الاستماع إلى بودكاست إنجليزي يومياً
                    
                    #أهداف #لغات #تطوير_ذاتي
                    """.trimIndent(),
                    tags = "أهداف,لغات,تطوير_ذاتي",
                    isPinned = true
                ),
                VaultItem(
                    id = "V002",
                    title = "ملاحظات وتأملات الأسبوع",
                    folder = "Notes",
                    fileName = "weekly_reflection.md",
                    contentMarkdown = """
                    # تأملات الأسبوع الحالي
                    
                    - التركيز على عادة واحدة يعطي نتائج أفضل بكثير من تشتيت النفس في 10 عادات دفعة واحدة.
                    - وضع الهاتف في غرفة أخرى أثناء جلسات Pomodoro ضاعف الإنتاجية.
                    
                    > "القليل الدائم خير من الكثير المنقطع."
                    """.trimIndent(),
                    tags = "تأملات,إنتاجية",
                    isPinned = false
                )
            )
            dao.insertVaultItems(vaultItems)
        }
    }
}
