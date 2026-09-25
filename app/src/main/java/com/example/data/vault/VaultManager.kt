package com.example.data.vault

import android.content.Context
import com.example.data.local.AchievementDao
import com.example.data.model.Habit
import com.example.data.model.TaskItem
import com.example.data.model.GoalItem
import com.example.data.model.FocusSession
import com.example.data.model.DailyReview
import com.example.data.model.VaultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VaultManager(
    private val context: Context,
    private val dao: AchievementDao
) {
    private val vaultDir: File
        get() = File(context.filesDir, "achievement_vault").apply { if (!exists()) mkdirs() }

    fun getVaultPath(): String = vaultDir.absolutePath

    /**
     * Synchronizes and writes all current database content into Obsidian-compatible Markdown files
     */
    suspend fun syncAllToMarkdown(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val habitsDir = File(vaultDir, "habits").apply { mkdirs() }
            val tasksDir = File(vaultDir, "tasks").apply { mkdirs() }
            val goalsDir = File(vaultDir, "goals").apply { mkdirs() }
            val dailyDir = File(vaultDir, "daily").apply { mkdirs() }
            val notesDir = File(vaultDir, "notes").apply { mkdirs() }

            val habits = dao.getHabitsList()
            val tasks = dao.getTasksList()
            val goals = dao.getGoalsList()
            val milestonesList = dao.getMilestonesList()
            val reviews = dao.getDailyReviewsList()
            val focusSessions = dao.getFocusSessionsList()
            val completions = dao.getCompletionsList()

            var count = 0

            // 1. Export Habits
            for (h in habits) {
                val file = File(habitsDir, "${sanitizeFileName(h.name)}.md")
                val habitCompletions = completions.filter { it.habitId == h.id }
                val content = buildString {
                    appendLine("---")
                    appendLine("id: \"${h.id}\"")
                    appendLine("name: \"${h.name}\"")
                    appendLine("type: habit")
                    appendLine("frequency: ${h.frequencyType}")
                    appendLine("targetPerWeek: ${h.targetCountPerWeek}")
                    appendLine("isActive: ${h.isActive}")
                    appendLine("isEssential: ${h.isEssential}")
                    appendLine("color: \"${h.colorHex}\"")
                    appendLine("---")
                    appendLine()
                    appendLine("# ${h.name}")
                    appendLine()
                    appendLine("## 📊 سجل الإنجاز")
                    if (habitCompletions.isEmpty()) {
                        appendLine("- لا يوجد إنجازات مسجلة بعد.")
                    } else {
                        habitCompletions.sortedByDescending { it.date }.forEach { comp ->
                            appendLine("- [[daily/${comp.date}|${comp.date}]]: تم الإنجاز ✅")
                        }
                    }
                }
                file.writeText(content)
                count++
            }

            // 2. Export Goals
            for (g in goals) {
                val file = File(goalsDir, "${sanitizeFileName(g.title)}.md")
                val milestones = milestonesList.filter { it.goalId == g.id }
                val content = buildString {
                    appendLine("---")
                    appendLine("id: \"${g.id}\"")
                    appendLine("title: \"${g.title}\"")
                    appendLine("type: goal")
                    appendLine("category: \"${g.category}\"")
                    appendLine("listType: \"${g.listType}\"")
                    appendLine("isActive: ${g.isActive}")
                    appendLine("targetDate: \"${g.targetDate ?: ""}\"")
                    appendLine("---")
                    appendLine()
                    appendLine("# ${g.title}")
                    appendLine()
                    if (!g.description.isNullOrBlank()) {
                        appendLine("> ${g.description}")
                        appendLine()
                    }
                    appendLine("## 🎯 المراحل (Milestones)")
                    if (milestones.isEmpty()) {
                        appendLine("- لم يتم تحديد مراحل فرعية بعد.")
                    } else {
                        milestones.forEach { m ->
                            val check = if (m.isCompleted) "[x]" else "[ ]"
                            appendLine("- $check ${m.title} (${m.progressPercent}%)")
                        }
                    }
                }
                file.writeText(content)
                count++
            }

            // 3. Export Tasks (Grouped by Category / Due Date)
            val tasksFile = File(tasksDir, "tasks_master.md")
            val tasksContent = buildString {
                appendLine("---")
                appendLine("type: task_master")
                appendLine("updatedAt: \"${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\"")
                appendLine("---")
                appendLine()
                appendLine("# قائمة المهام الرئيسية")
                appendLine()
                val grouped = tasks.groupBy { it.listCategory }
                grouped.forEach { (cat, list) ->
                    appendLine("## ${getCategoryTitle(cat)}")
                    list.forEach { t ->
                        val check = if (t.isCompleted) "[x]" else "[ ]"
                        val prioBadge = when (t.priority) {
                            "HIGH" -> "🔺 #عالي"
                            "MEDIUM" -> "🟡 #متوسط"
                            "LOW" -> "🟢 #منخفض"
                            else -> ""
                        }
                        appendLine("- $check ${t.title} [تاريخ: ${t.dueDate}] $prioBadge")
                        if (!t.description.isNullOrBlank()) {
                            appendLine("  > ${t.description}")
                        }
                    }
                    appendLine()
                }
            }
            tasksFile.writeText(tasksContent)
            count++

            // 4. Export Daily Notes
            val dates = (completions.map { it.date } + tasks.map { it.dueDate } + reviews.map { it.date } + focusSessions.map { it.date }).distinct()
            for (date in dates) {
                val dailyFile = File(dailyDir, "$date.md")
                val dateCompletions = completions.filter { it.date == date }
                val dateTasks = tasks.filter { it.dueDate == date }
                val dateFocus = focusSessions.filter { it.date == date }
                val dateReview = reviews.firstOrNull { it.date == date }

                val dailyContent = buildString {
                    appendLine("---")
                    appendLine("date: \"$date\"")
                    appendLine("type: daily_note")
                    appendLine("habitsDone: ${dateCompletions.size}")
                    appendLine("tasksDone: ${dateTasks.count { it.isCompleted }}")
                    appendLine("focusMinutes: ${dateFocus.sumOf { it.durationMinutes }}")
                    appendLine("---")
                    appendLine()
                    appendLine("# مذكرة يوم: $date")
                    appendLine()
                    appendLine("## 🟢 العادات المنجزة")
                    if (dateCompletions.isEmpty()) {
                        appendLine("- لم تسجل عادات منجزة اليوم.")
                    } else {
                        dateCompletions.forEach { comp ->
                            val habit = habits.firstOrNull { it.id == comp.habitId }
                            appendLine("- [x] [[habits/${habit?.name ?: comp.habitId}|${habit?.name ?: comp.habitId}]]")
                        }
                    }
                    appendLine()
                    appendLine("## 🔵 المهام")
                    if (dateTasks.isEmpty()) {
                        appendLine("- لا توجد مهام لهذا اليوم.")
                    } else {
                        dateTasks.forEach { t ->
                            val check = if (t.isCompleted) "[x]" else "[ ]"
                            appendLine("- $check ${t.title}")
                        }
                    }
                    appendLine()
                    appendLine("## ⏱️ جلسات التركيز")
                    if (dateFocus.isEmpty()) {
                        appendLine("- لم تُسجل جلسات تركيز.")
                    } else {
                        dateFocus.forEach { s ->
                            appendLine("- **${s.title}**: ${s.durationMinutes} دقيقة (${s.status})")
                        }
                    }
                    appendLine()
                    if (dateReview != null) {
                        appendLine("## 📝 مراجعة اليوم")
                        appendLine("- **أكبر إنجاز**: ${dateReview.biggestAchievement}")
                        appendLine("- **ما لم يكتمل**: ${dateReview.whatWasNotDone}")
                        appendLine("- **ملاحظات**: ${dateReview.dailyNote}")
                    }
                }
                dailyFile.writeText(dailyContent)
                count++
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
    }

    private fun getCategoryTitle(cat: String): String = when (cat) {
        "TODAY" -> "مهام اليوم"
        "THIS_WEEK" -> "مهام الأسبوع"
        "THIS_MONTH" -> "مهام الشهر"
        "SOMEDAY" -> "مهام مؤجلة / قادمة"
        else -> cat
    }
}
