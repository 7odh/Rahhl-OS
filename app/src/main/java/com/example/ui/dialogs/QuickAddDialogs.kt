package com.example.ui.dialogs

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GoalItem
import com.example.ui.QuickAddType
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
fun QuickAddMenuDialog(
    onDismiss: () -> Unit,
    onSelectOption: (QuickAddType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ماذا تريد أن تضيف؟",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                QuickAddMenuItem(
                    title = "مهمة جديدة",
                    subtitle = "عمل يُنجز لمرة واحدة وله تاريخ محدد",
                    icon = Icons.Default.Assignment,
                    color = TaskBlue,
                    testTag = "add_task_option",
                    onClick = { onSelectOption(QuickAddType.TASK) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickAddMenuItem(
                    title = "عادة جديدة",
                    subtitle = "سلوك مستمر يتكرر يوميًا أو أسبوعيًا",
                    icon = Icons.Default.Loop,
                    color = PrimaryEmerald,
                    testTag = "add_habit_option",
                    onClick = { onSelectOption(QuickAddType.HABIT) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickAddMenuItem(
                    title = "هدف جديد",
                    subtitle = "مسار كبير يتفرع منه مراحل وإنجازات",
                    icon = Icons.Default.Flag,
                    color = GoalPurple,
                    testTag = "add_goal_option",
                    onClick = { onSelectOption(QuickAddType.GOAL) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickAddMenuItem(
                    title = "جلسة تركيز",
                    subtitle = "بدء بومودورو أو تسجيل وقت تركيز",
                    icon = Icons.Default.Timer,
                    color = FocusOrange,
                    testTag = "add_focus_option",
                    onClick = { onSelectOption(QuickAddType.FOCUS) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickAddMenuItem(
                    title = "ملاحظة سريعة",
                    subtitle = "تدوين فكرة أو شعور في يومياتك",
                    icon = Icons.Default.EditNote,
                    color = AccentGold,
                    testTag = "add_note_option",
                    onClick = { onSelectOption(QuickAddType.NOTE) }
                )
            }
        }
    }
}

@Composable
fun QuickAddMenuItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariantDark.copy(alpha = 0.6f))
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMutedDark
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    currentDate: String,
    activeGoals: List<GoalItem>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String?, dueDate: String, dueTime: String?, estMins: Int, priority: String, goalId: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    var estimatedMinutes by remember { mutableStateOf(25) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "إضافة مهمة جديدة",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaskBlue
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المهمة (مثلاً: إنهاء ملف المشروع)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TaskBlue,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("تفاصيل إضافية (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TaskBlue,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("الأولوية:", fontSize = 13.sp, color = TextSecondaryDark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("LOW" to "منخفضة", "MEDIUM" to "متوسطة", "HIGH" to "عالية").forEach { (key, label) ->
                        FilterChip(
                            selected = priority == key,
                            onClick = { priority = key },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TaskBlue.copy(alpha = 0.2f),
                                selectedLabelColor = TaskBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("المدة المقدرة:", fontSize = 13.sp, color = TextSecondaryDark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 25, 45, 60).forEach { mins ->
                        FilterChip(
                            selected = estimatedMinutes == mins,
                            onClick = { estimatedMinutes = mins },
                            label = { Text("$mins د", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FocusOrange.copy(alpha = 0.2f),
                                selectedLabelColor = FocusOrange
                            )
                        )
                    }
                }

                if (activeGoals.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("ربط بهدف:", fontSize = 13.sp, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedGoalId == null,
                            onClick = { selectedGoalId = null },
                            label = { Text("بدون هدف", fontSize = 12.sp) }
                        )
                        activeGoals.take(3).forEach { g ->
                            FilterChip(
                                selected = selectedGoalId == g.id,
                                onClick = { selectedGoalId = g.id },
                                label = { Text(g.title.take(12), fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoalPurple.copy(alpha = 0.2f),
                                    selectedLabelColor = GoalPurple
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Text("إلغاء", color = TextSecondaryDark)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(
                                    title,
                                    description.takeIf { it.isNotBlank() },
                                    currentDate,
                                    null,
                                    estimatedMinutes,
                                    priority,
                                    selectedGoalId
                                )
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = TaskBlue),
                        modifier = Modifier.testTag("confirm_add_task")
                    ) {
                        Text("حفظ المهمة", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddHabitDialog(
    activeGoals: List<GoalItem>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, icon: String, color: String, freq: String, days: String, targetCount: Int, time: String?, goalId: String?, isEssential: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var frequencyType by remember { mutableStateOf("DAILY") }
    var preferredTime by remember { mutableStateOf("") }
    var isEssential by remember { mutableStateOf(false) }
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf("#10B981") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "إنشاء عادة جديدة 🔁",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم العادة (مثلاً: قراءة، رياضة، Busuu)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("التكرار:", fontSize = 13.sp, color = TextSecondaryDark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("DAILY" to "يوميًا", "X_TIMES_WEEK" to "3 مرات أسبوعيًا").forEach { (key, label) ->
                        FilterChip(
                            selected = frequencyType == key,
                            onClick = { frequencyType = key },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryEmerald.copy(alpha = 0.2f),
                                selectedLabelColor = PrimaryEmerald
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = preferredTime,
                    onValueChange = { preferredTime = it },
                    label = { Text("وقت مفضل اختياري (مثلاً: بعد الفجر أو 20:00)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Essential habit checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .clickable { isEssential = !isEssential }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isEssential,
                        onCheckedChange = { isEssential = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("عادة أساسية", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
                        Text("تظهر دائمًا حتى في أيام الراحة (مثل الصلاة والقرآن)", fontSize = 11.sp, color = TextMutedDark)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Text("إلغاء", color = TextSecondaryDark)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    name,
                                    "star",
                                    selectedColor,
                                    frequencyType,
                                    "1,2,3,4,5,6,7",
                                    if (frequencyType == "DAILY") 7 else 3,
                                    preferredTime.takeIf { it.isNotBlank() },
                                    selectedGoalId,
                                    isEssential
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        modifier = Modifier.testTag("confirm_add_habit")
                    ) {
                        Text("حفظ العادة", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String?, category: String, targetDate: String?, color: String, icon: String, milestones: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("عام") }
    var milestoneInput by remember { mutableStateOf("") }
    val milestones = remember { mutableStateListOf("المرحلة 1", "المرحلة 2") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "إنشاء هدف جديد 🎯",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoalPurple
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم الهدف (مثلاً: الوصول لمستوى B1 في الإنجليزية)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoalPurple,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("التصنيف:", fontSize = 13.sp, color = TextSecondaryDark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("لغات", "برمجة", "صحة", "عمل", "عام").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoalPurple.copy(alpha = 0.2f),
                                selectedLabelColor = GoalPurple
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("المراحل الفرعية (Milestones):", fontSize = 13.sp, color = TextSecondaryDark)
                milestones.forEachIndexed { idx, m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• $m", fontSize = 14.sp, color = TextPrimaryDark, modifier = Modifier.weight(1f))
                        IconButton(onClick = { milestones.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "حذف", tint = TextMutedDark, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = milestoneInput,
                        onValueChange = { milestoneInput = it },
                        label = { Text("إضافة مرحلة (مثل A1, A2)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (milestoneInput.isNotBlank()) {
                                milestones.add(milestoneInput)
                                milestoneInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "أضف", tint = GoalPurple)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Text("إلغاء", color = TextSecondaryDark)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(
                                    title,
                                    description.takeIf { it.isNotBlank() },
                                    category,
                                    null,
                                    "#8B5CF6",
                                    "goal",
                                    milestones.toList()
                                )
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoalPurple),
                        modifier = Modifier.testTag("confirm_add_goal")
                    ) {
                        Text("حفظ الهدف", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddDailyNoteDialog(
    currentDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ملاحظة اليوم 📝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
                Text(
                    text = "تدوين سريع للأفكار أو ملخص بسيط ليومك",
                    fontSize = 12.sp,
                    color = TextMutedDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("مثلاً: النهارده كنت تعبان لكن خلصت Busuu والجيم...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("quick_note_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("إلغاء", color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (note.isNotBlank()) onConfirm(note)
                        },
                        enabled = note.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        modifier = Modifier.testTag("save_note_button")
                    ) {
                        Text("حفظ الملاحظة", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyReviewDialog(
    initialMood: String = "HAPPY",
    initialAchievement: String = "",
    initialNotDone: String = "",
    initialNote: String = "",
    onDismiss: () -> Unit,
    onConfirm: (mood: String, ach: String, notDone: String, note: String) -> Unit
) {
    var mood by remember { mutableStateOf(initialMood) }
    var achievement by remember { mutableStateOf(initialAchievement) }
    var whatNotDone by remember { mutableStateOf(initialNotDone) }
    var noteText by remember { mutableStateOf(initialNote) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "مراجعة اليوم 🧠",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RestCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("كيف كان يومك؟", fontSize = 14.sp, color = TextPrimaryDark, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val moods = listOf(
                        "SAD" to "😞",
                        "NEUTRAL" to "😐",
                        "HAPPY" to "🙂",
                        "EXCITED" to "😄"
                    )
                    moods.forEach { (mKey, emoji) ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (mood == mKey) RestCyan.copy(alpha = 0.25f) else SurfaceVariantDark)
                                .border(
                                    width = if (mood == mKey) 2.dp else 1.dp,
                                    color = if (mood == mKey) RestCyan else BorderDark,
                                    shape = CircleShape
                                )
                                .clickable { mood = mKey },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 22.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = achievement,
                    onValueChange = { achievement = it },
                    label = { Text("أكبر إنجاز حققته اليوم:") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = whatNotDone,
                    onValueChange = { whatNotDone = it },
                    label = { Text("ما الذي لم يتم أو واجهت فيه صعوبة؟") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("ملاحظة ختامية لليوم:") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("إلغاء", color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            onConfirm(mood, achievement, whatNotDone, noteText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RestCyan)
                    ) {
                        Text("حفظ المراجعة", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ExportPreviewDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = content,
                        fontSize = 12.sp,
                        color = TextPrimaryDark,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            clipboard.setText(AnnotatedString(content))
                            copied = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (copied) PrimaryEmerald else SurfaceVariantDark
                        )
                    ) {
                        Icon(
                            imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "نسخ",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (copied) "تم النسخ!" else "نسخ النص", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, content)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, title)
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TaskBlue)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة لـ AI", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Text("إغلاق", color = TextSecondaryDark)
                }
            }
        }
    }
}
