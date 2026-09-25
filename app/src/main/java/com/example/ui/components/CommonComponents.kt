package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavigationTab
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
fun AppHeader(
    dateText: String,
    greetingTitle: String = "صباح الخير",
    greetingSubtitle: String = "يوم جديد .. فرصة جديدة",
    isRestDay: Boolean = false,
    onRestClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
    onQuoteClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onVaultClick: () -> Unit = {},
    onQuickAddClick: () -> Unit = {}
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: Date & Tools (Vault, Calendar, Rest, Reset, Add)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateText,
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Reset Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceVariantDark)
                            .border(1.dp, FocusOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable(onClick = onResetClick)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("reset_today_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "إعادة ضبط اليوم",
                                tint = FocusOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ريست",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FocusOrange
                            )
                        }
                    }

                    // Rest Mode Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isRestDay) RestCyan.copy(alpha = 0.2f) else SurfaceVariantDark)
                            .border(
                                width = 1.dp,
                                color = if (isRestDay) RestCyan else BorderDark,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(onClick = onRestClick)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("rest_mode_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BeachAccess,
                                contentDescription = "Rest Mode",
                                tint = if (isRestDay) RestCyan else TextSecondaryDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isRestDay) "راحة 🏖️" else "راحة",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isRestDay) RestCyan else TextSecondaryDark
                            )
                        }
                    }

                    // Calendar Quick Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantDark)
                            .border(1.dp, BorderDark, CircleShape)
                            .clickable(onClick = onCalendarClick)
                            .testTag("header_calendar_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "التقويم",
                            tint = TaskBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Vault Quick Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantDark)
                            .border(1.dp, BorderDark, CircleShape)
                            .clickable(onClick = onVaultClick)
                            .testTag("header_vault_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "الخزنة",
                            tint = GoalPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Quick Add Icon Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PrimaryEmerald)
                            .clickable(onClick = onQuickAddClick)
                            .testTag("header_quick_add"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Quick Add",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Greeting and Motivational Quote Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Sun/Greeting",
                    tint = AccentGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = greetingTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Clickable Motivational Quote Chip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariantDark.copy(alpha = 0.6f))
                    .border(1.dp, AccentGold.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onQuoteClick)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = greetingSubtitle,
                            fontSize = 11.sp,
                            color = AccentGold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "تغيير ↻",
                        fontSize = 10.sp,
                        color = TextMutedDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicatorItem(
    label: String,
    value: String,
    percent: Int,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp,
    strokeWidth: Dp = 6.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                // Background Track
                drawArc(
                    color = color.copy(alpha = 0.15f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                // Progress Arc
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * (percent.coerceIn(0, 100) / 100f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "$percent%",
                    fontSize = 10.sp,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondaryDark
        )
    }
}

@Composable
fun AchievementBottomBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = modifier.border(width = 1.dp, color = BorderDark.copy(alpha = 0.5f))
    ) {
        NavigationBarItem(
            selected = selectedTab == NavigationTab.HOME,
            onClick = { onTabSelected(NavigationTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "الرئيسية"
                )
            },
            label = { Text("الرئيسية", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryEmerald,
                selectedTextColor = PrimaryEmerald,
                indicatorColor = PrimaryEmerald.copy(alpha = 0.18f),
                unselectedIconColor = TextMutedDark,
                unselectedTextColor = TextMutedDark
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        NavigationBarItem(
            selected = selectedTab == NavigationTab.PROGRESS,
            onClick = { onTabSelected(NavigationTab.PROGRESS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.PROGRESS) Icons.Filled.Flag else Icons.Outlined.Flag,
                    contentDescription = "الإنجازات"
                )
            },
            label = { Text("الإنجازات", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TaskBlue,
                selectedTextColor = TaskBlue,
                indicatorColor = TaskBlue.copy(alpha = 0.18f),
                unselectedIconColor = TextMutedDark,
                unselectedTextColor = TextMutedDark
            ),
            modifier = Modifier.testTag("nav_tab_progress")
        )

        NavigationBarItem(
            selected = selectedTab == NavigationTab.FOCUS,
            onClick = { onTabSelected(NavigationTab.FOCUS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.FOCUS) Icons.Filled.Timer else Icons.Outlined.Timer,
                    contentDescription = "التركيز"
                )
            },
            label = { Text("التركيز", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FocusOrange,
                selectedTextColor = FocusOrange,
                indicatorColor = FocusOrange.copy(alpha = 0.18f),
                unselectedIconColor = TextMutedDark,
                unselectedTextColor = TextMutedDark
            ),
            modifier = Modifier.testTag("nav_tab_focus")
        )

        NavigationBarItem(
            selected = selectedTab == NavigationTab.ANALYTICS,
            onClick = { onTabSelected(NavigationTab.ANALYTICS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.ANALYTICS) Icons.Filled.Assessment else Icons.Outlined.Assessment,
                    contentDescription = "التقارير"
                )
            },
            label = { Text("التقارير", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoalPurple,
                selectedTextColor = GoalPurple,
                indicatorColor = GoalPurple.copy(alpha = 0.18f),
                unselectedIconColor = TextMutedDark,
                unselectedTextColor = TextMutedDark
            ),
            modifier = Modifier.testTag("nav_tab_analytics")
        )

        NavigationBarItem(
            selected = selectedTab == NavigationTab.MORE,
            onClick = { onTabSelected(NavigationTab.MORE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.MORE) Icons.Filled.MoreHoriz else Icons.Outlined.MoreHoriz,
                    contentDescription = "المزيد"
                )
            },
            label = { Text("المزيد", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentGold,
                selectedTextColor = AccentGold,
                indicatorColor = AccentGold.copy(alpha = 0.18f),
                unselectedIconColor = TextMutedDark,
                unselectedTextColor = TextMutedDark
            ),
            modifier = Modifier.testTag("nav_tab_more")
        )
    }
}
