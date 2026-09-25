package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MotivationalQuote
import com.example.data.model.TaskItem
import com.example.data.model.VaultItem
import com.example.ui.AchievementViewModel
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 1. Reset Today Confirmation Dialog
 */
@Composable
fun ResetTodayConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        icon = {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "إعادة ضبط",
                tint = FocusOrange,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "إعادة ضبط إنجاز اليوم؟",
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "سيتم إلغاء تعليم العادات والمهام المنجزة لهذا اليوم لتبدأ من جديد. لن يتم حذف أي من بياناتك أو تاريخك السابق.",
                color = TextSecondaryDark,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = FocusOrange)
            ) {
                Text("إعادة الضبط الآن 🔄", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondaryDark)
            }
        }
    )
}

/**
 * 2. Motivational Quotes Management Dialog
 */
@Composable
fun QuotesManagementDialog(
    viewModel: AchievementViewModel,
    onDismiss: () -> Unit
) {
    val quotes by viewModel.allQuotes.collectAsStateWithLifecycle()
    var isAddingQuote by remember { mutableStateOf(false) }
    var newQuoteText by remember { mutableStateOf("") }
    var newQuoteAuthor by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✨", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إدارة العبارات التحفيزية",
                                color = TextPrimaryDark,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "تظهر في أعلى الصفحة الرئيسية لتشجيعك",
                                color = TextMutedDark,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextMutedDark)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Add button toggle
                Button(
                    onClick = { isAddingQuote = !isAddingQuote },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold)
                ) {
                    Icon(
                        imageVector = if (isAddingQuote) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "إضافة",
                        tint = AccentGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAddingQuote) "إلغاء الإضافة" else "+ إضافة عبارة تحفيزية جديدة",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Add form
                AnimatedVisibility(visible = isAddingQuote) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark)
                            .padding(12.dp)
                    ) {
                        OutlinedTextField(
                            value = newQuoteText,
                            onValueChange = { newQuoteText = it },
                            label = { Text("نص العبارة التحفيزية", color = TextSecondaryDark) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = AccentGold,
                                unfocusedBorderColor = BorderDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newQuoteAuthor,
                            onValueChange = { newQuoteAuthor = it },
                            label = { Text("القائل أو المصدر (اختياري)", color = TextSecondaryDark) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = AccentGold,
                                unfocusedBorderColor = BorderDark
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (newQuoteText.isNotBlank()) {
                                    viewModel.addQuote(newQuoteText, newQuoteAuthor)
                                    newQuoteText = ""
                                    newQuoteAuthor = ""
                                    isAddingQuote = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                        ) {
                            Text("حفظ في القائمة ✨", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quotes list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quotes, key = { it.id }) { quote ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "“${quote.text}”",
                                        color = TextPrimaryDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "— ${quote.author}",
                                        color = AccentGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteQuote(quote) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف العبارة",
                                        tint = TextMutedDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3. Obsidian Vault Manager Dialog
 */
@Composable
fun ObsidianVaultDialog(
    viewModel: AchievementViewModel,
    onDismiss: () -> Unit
) {
    val vaultItems by viewModel.allVaultItems.collectAsStateWithLifecycle()
    var selectedFolder by remember { mutableStateOf("الكل") }
    var isCreatingNote by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newFolder by remember { mutableStateOf("Notes") }
    var newContent by remember { mutableStateOf("") }
    var newTags by remember { mutableStateOf("") }
    var viewingItem by remember { mutableStateOf<VaultItem?>(null) }

    val folders = listOf("الكل", "Notes", "Habits", "Goals", "Daily")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoalPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = "Obsidian Vault", tint = GoalPurple)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "خزنة Obsidian المحلية 🗄️",
                                color = TextPrimaryDark,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "تخزين محلي بملفات Markdown مقروءة",
                                color = TextMutedDark,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextMutedDark)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sync and Export Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.syncVaultToMarkdown() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoalPurple.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoalPurple)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "مزامنة", tint = GoalPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مزامنة الخزنة (.md)", color = GoalPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isCreatingNote = !isCreatingNote },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "مذكرة جديدة", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ مذكرة Markdown", color = PrimaryEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Add note inline form
                AnimatedVisibility(visible = isCreatingNote) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark)
                            .padding(12.dp)
                    ) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("عنوان المذكرة", color = TextSecondaryDark) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = GoalPurple,
                                unfocusedBorderColor = BorderDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = newTags,
                            onValueChange = { newTags = it },
                            label = { Text("الوسوم (مثال: أهداف, تعلم)", color = TextSecondaryDark) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = GoalPurple,
                                unfocusedBorderColor = BorderDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = newContent,
                            onValueChange = { newContent = it },
                            label = { Text("المحتوى بتنسيق Markdown (# عنوان, - [ ] مهمة)", color = TextSecondaryDark) },
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedBorderColor = GoalPurple,
                                unfocusedBorderColor = BorderDark
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    viewModel.addVaultNote(newTitle, newFolder, newContent, newTags)
                                    newTitle = ""
                                    newContent = ""
                                    newTags = ""
                                    isCreatingNote = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GoalPurple)
                        ) {
                            Text("حفظ في الخزنة 💾", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Folder filter tabs
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(folders) { f ->
                        FilterChip(
                            selected = selectedFolder == f,
                            onClick = { selectedFolder = f },
                            label = { Text(f, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoalPurple,
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceVariantDark,
                                labelColor = TextSecondaryDark
                            )
                        )
                    }
                }

                // Vault Notes List
                val filteredItems = if (selectedFolder == "الكل") vaultItems else vaultItems.filter { it.folder.equals(selectedFolder, ignoreCase = true) }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد ملفات في هذا المجلد.\nاضغط 'مزامنة الخزنة' لتصدير كل العادات والمهام كملفات .md",
                                    color = TextMutedDark,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items(filteredItems, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewingItem = item },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = "Markdown File",
                                        tint = GoalPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.title,
                                            color = TextPrimaryDark,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${item.folder}/${item.fileName} ${if (item.tags.isNotBlank()) "• #" + item.tags else ""}",
                                            color = TextMutedDark,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteVaultNote(item) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف المذكرة",
                                        tint = TextMutedDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to view note markdown content
    viewingItem?.let { note ->
        AlertDialog(
            onDismissRequest = { viewingItem = null },
            containerColor = SurfaceDark,
            title = {
                Text(note.title, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        text = "📁 المجلد: ${note.folder} | الملف: ${note.fileName}",
                        color = GoalPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceVariantDark)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = note.contentMarkdown.ifBlank { "(محتوى فارغ)" },
                            color = TextPrimaryDark,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewingItem = null }) {
                    Text("إغلاق", color = PrimaryEmerald, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

/**
 * 4. TickTick Style Calendar & Task Management Dialog
 */
@Composable
fun TickTickCalendarDialog(
    viewModel: AchievementViewModel,
    onDismiss: () -> Unit
) {
    val selectedDate by viewModel.calendarSelectedDate.collectAsStateWithLifecycle()
    val tasksForSelectedDate by viewModel.calendarTasks.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val habits by viewModel.allHabits.collectAsStateWithLifecycle()
    val completions by viewModel.todayCompletions.collectAsStateWithLifecycle()

    var calendarMonthOffset by remember { mutableIntStateOf(0) }

    // Generate calendar days for month
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, calendarMonthOffset)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val monthNameFormat = SimpleDateFormat("MMMM yyyy", Locale("ar"))
    val currentMonthTitle = monthNameFormat.format(calendar.time)

    val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun..7=Sat

    val daysInMonth = (1..maxDaysInMonth).toList()

    val yearMonthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TaskBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "تقويم TickTick", tint = TaskBlue)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تقويم المهام (TickTick View)",
                                color = TextPrimaryDark,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "إدارة المهام وتواريخ الاستحقاق",
                                color = TextMutedDark,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextMutedDark)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Month Navigator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { calendarMonthOffset-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الشهر السابق", tint = TextSecondaryDark)
                    }

                    Text(
                        text = currentMonthTitle,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    IconButton(onClick = { calendarMonthOffset++ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "الشهر القادم", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days of Week Header (Sat to Fri)
                val dayHeaders = listOf("سبت", "أحد", "اثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    dayHeaders.forEach { d ->
                        Text(
                            text = d,
                            color = TextMutedDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Month Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // Empty cells for offset
                    val emptyDays = (firstDayOfWeek) % 7
                    items(emptyDays) {
                        Box(modifier = Modifier.size(36.dp))
                    }

                    items(daysInMonth) { day ->
                        val dayFormatted = String.format("%02d", day)
                        val dateKey = "$yearMonthStr-$dayFormatted"
                        val isSelected = selectedDate == dateKey
                        val tasksOnDay = allTasks.filter { it.dueDate == dateKey }
                        val hasTasks = tasksOnDay.isNotEmpty()

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isSelected -> TaskBlue
                                        hasTasks -> TaskBlue.copy(alpha = 0.2f)
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (isSelected) 1.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setCalendarDate(dateKey) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$day",
                                    color = if (isSelected) Color.White else TextPrimaryDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected || hasTasks) FontWeight.Bold else FontWeight.Normal
                                )
                                if (hasTasks && !isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(TaskBlue)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Day Tasks Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مهام يوم: $selectedDate",
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${tasksForSelectedDate.size} مهمة",
                        color = TaskBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tasks list for selected date
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (tasksForSelectedDate.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد مهام مجدولة لهذا التاريخ 🌱",
                                    color = TextMutedDark,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    items(tasksForSelectedDate, key = { it.id }) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (task.isCompleted) TaskBlue else Color.Transparent)
                                            .border(1.5.dp, if (task.isCompleted) TaskBlue else TextMutedDark, RoundedCornerShape(6.dp))
                                            .clickable { viewModel.toggleTask(task) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (task.isCompleted) {
                                            Icon(Icons.Default.Check, contentDescription = "مكتمل", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = task.title,
                                            color = TextPrimaryDark,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${task.estimatedMinutes} دقيقة • أولوية: ${when(task.priority) { "HIGH" -> "عالية 🔴"; "MEDIUM" -> "متوسطة 🟡"; else -> "عادية" }}",
                                            color = TextMutedDark,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                // Quick Reschedule Action
                                Row {
                                    TextButton(
                                        onClick = {
                                            // Move to tomorrow
                                            val cal = Calendar.getInstance()
                                            cal.add(Calendar.DAY_OF_YEAR, 1)
                                            val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                                            viewModel.rescheduleTask(task, tomorrow)
                                        }
                                    ) {
                                        Text("لغداً ➡️", color = AccentGold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
