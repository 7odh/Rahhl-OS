package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import com.example.ui.dialogs.QuotesManagementDialog
import com.example.ui.dialogs.ObsidianVaultDialog
import com.example.ui.dialogs.TickTickCalendarDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AchievementViewModel
import com.example.ui.dialogs.DailyReviewDialog
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
fun MoreScreen(
    viewModel: AchievementViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val summary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val dailyReview by viewModel.todayDailyReview.collectAsStateWithLifecycle()

    var showDailyReviewDialog by remember { mutableStateOf(false) }
    var selectedExportPeriod by remember { mutableIntStateOf(7) } // 1: Today, 7: Week, 30: Month
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    var showQuotesDialog by remember { mutableStateOf(false) }
    var showVaultDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    if (showQuotesDialog) {
        QuotesManagementDialog(
            viewModel = viewModel,
            onDismiss = { showQuotesDialog = false }
        )
    }

    if (showVaultDialog) {
        ObsidianVaultDialog(
            viewModel = viewModel,
            onDismiss = { showVaultDialog = false }
        )
    }

    if (showCalendarDialog) {
        TickTickCalendarDialog(
            viewModel = viewModel,
            onDismiss = { showCalendarDialog = false }
        )
    }

    if (showDailyReviewDialog) {
        DailyReviewDialog(
            initialMood = dailyReview?.moodRating ?: "HAPPY",
            initialAchievement = dailyReview?.biggestAchievement ?: "",
            initialNotDone = dailyReview?.whatWasNotDone ?: "",
            initialNote = dailyReview?.dailyNote ?: "",
            onDismiss = { showDailyReviewDialog = false },
            onConfirm = { mood, ach, notDone, note ->
                viewModel.saveDailyReview(mood, ach, notDone, note)
                showDailyReviewDialog = false
            }
        )
    }

    if (showImportDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showImportDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("استعادة نسخة احتياطية", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        label = { Text("الصق محتوى ملف JSON هنا") },
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showImportDialog = false }) { Text("إلغاء") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (importJsonText.isNotBlank()) {
                                    viewModel.importBackup(importJsonText)
                                    showImportDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                        ) {
                            Text("استعادة")
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        item {
            Text(
                text = "الإعدادات والتخصيص",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Daily Review Action Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = RestCyan.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, RestCyan.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDailyReviewDialog = true }
                    .testTag("open_daily_review_dialog")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(RestCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = "مراجعة اليوم", tint = RestCyan)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مراجعة نهاية اليوم 🧠",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "كيف كان يومك؟ أكبر إنجاز، ما لم يتم، والمزاج",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                    Button(
                        onClick = { showDailyReviewDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RestCyan)
                    ) {
                        Text("مراجعة", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 1. Rest Mode Settings (أيام الراحة)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BeachAccess, contentDescription = "راحة", tint = RestCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("أيام الراحة (Rest Mode)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تحديد أيام معينة مثل الجمعة لإخفاء العادات غير الضرورية وعدم احتسابها كتقصير.",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("تفعيل وضع الراحة لهذا اليوم", fontSize = 13.sp, color = TextPrimaryDark)
                        Switch(
                            checked = summary.isRestDay,
                            onCheckedChange = { viewModel.toggleRestDay() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RestCyan
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Quotes Management Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FormatQuote, contentDescription = "حكم", tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إدارة العبارات التحفيزية", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "أضف واحذف وعدل العبارات الملهمة التي تظهر وتتغير في أعلى الصفحة الرئيسية.",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showQuotesDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إدارة العبارات التحفيزية ✨", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Obsidian Vault Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, contentDescription = "Obsidian", tint = GoalPurple, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نظام التخزين وخزنة Obsidian (.md)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تخزين محلي كامل بملفات Markdown مقروءة ومرتبة في مجلدات (Habits, Goals, Tasks, Daily) متوافقة مع Obsidian.",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.syncVaultToMarkdown() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoalPurple.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoalPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("مزامنة الخزنة 📂", color = GoalPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showVaultDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoalPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("فتح الخزنة 🗄️", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // TickTick Calendar Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "تقويم", tint = TaskBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تقويم المهام وتواريخ الاستحقاق", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "عرض شهري تفاعلي زي تطبيق TickTick لعرض المهام والعادات حسب الأيام وإعادة الجدولة السريعة.",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showCalendarDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TaskBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("عرض تقويم TickTick 📅", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Notifications & Alerts Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = "تنبيهات", tint = FocusOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نظام التنبيهات والإشعارات", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تنبيهات تلقائية لمواعيد العادات، اكتمال وقت جلسة Pomodoro، وتذكير المراجعة اليومية المسائية.",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.testSendNotification() },
                        colors = ButtonDefaults.buttonColors(containerColor = FocusOrange.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FocusOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔔 إرسال إشعار تجريبي للتأكد", color = FocusOrange, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. AI Report Export (تصدير التقرير للذكاء الاصطناعي)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تصدير للذكاء الاصطناعي (AI Analysis)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يولد تقرير Markdown منظم وجاهز لمشاركته ونقاشه مع ChatGPT أو Gemini لتحليل التزامك وأين ذهب وقتك.",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("اختر الفترة الزمنية:", fontSize = 12.sp, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1 to "اليوم", 7 to "آخر 7 أيام", 30 to "هذا الشهر").forEach { (days, label) ->
                            FilterChip(
                                selected = selectedExportPeriod == days,
                                onClick = { selectedExportPeriod = days },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryEmerald.copy(alpha = 0.2f),
                                    selectedLabelColor = PrimaryEmerald
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.exportAiReport(selectedExportPeriod) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_ai_report_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = "توليد التقرير", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("توليد تقرير الذكاء الاصطناعي 🤖", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. Backup & Restore (النسخ الاحتياطي والاستعادة)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Backup, contentDescription = "نسخ", tint = TaskBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("النسخ الاحتياطي والاستعادة (JSON)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "حفظ كامل بيانات عاداتك ومهامك وأهدافك وجلساتك لاستعادتها في أي هاتف آخر بدون إنترنت.",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.exportBackupJson() },
                            colors = ButtonDefaults.buttonColors(containerColor = TaskBlue),
                            modifier = Modifier.weight(1f).testTag("export_backup_button")
                        ) {
                            Text("تصدير Backup", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.weight(1f).testTag("import_backup_button")
                        ) {
                            Text("استعادة Import", fontSize = 12.sp, color = TextPrimaryDark)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. About App
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "حول", tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حول نظام الإنجاز (Achievement)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تطبيق شخصي خفيف لإدارة اليوم، العادات، المهام، الأهداف، وجلسات التركيز. يعمل بدون إنترنت تمامًا (Offline-First) ويحفظ تاريخ إنجازك كاملًا دون حذف.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "نفسك اليوم .. أقوى من أمس ⛰️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
