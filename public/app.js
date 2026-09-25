// Achievement — نظام إدارة الإنجاز الشخصي
// Complete Offline-First Engine with LocalStorage

(function() {
  'use strict';

  // --- Date Helpers ---
  const today = new Date();
  const todayStr = today.toISOString().split('T')[0]; // "YYYY-MM-DD"
  
  const arabicDays = ['الأحد', 'الاثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'];
  const arabicMonths = ['يناير', 'فبراير', 'مارس', 'أبريل', 'مايو', 'يونيو', 'يوليو', 'أغسطس', 'سبتمبر', 'أكتوبر', 'نوفمبر', 'ديسمبر'];
  
  function formatArabicDate(date) {
    const dayName = arabicDays[date.getDay()];
    const dayNum = date.getDate();
    const monthName = arabicMonths[date.getMonth()];
    const year = date.getFullYear();
    return `${dayName}، ${dayNum} ${monthName} ${year}`;
  }

  // --- Initial Data Seed ---
  const defaultData = {
    habits: [
      { id: 'H001', name: 'الصلاة', icon: '🕌', color: '#10b981', freq: 'DAILY', preferredTime: 'أوقاتها', goalId: null, isActive: true, isEssential: true },
      { id: 'H002', name: 'قراءة القرآن', icon: '📖', color: '#f59e0b', freq: 'DAILY', preferredTime: 'بعد الفجر', goalId: null, isActive: true, isEssential: true },
      { id: 'H003', name: 'التمرين', icon: '💪', color: '#ec4899', freq: '3_PER_WEEK', preferredTime: '18:30', goalId: null, isActive: true, isEssential: false },
      { id: 'H004', name: 'Busuu', icon: '🌐', color: '#3b82f6', freq: 'DAILY', preferredTime: '20:00', goalId: 'G001', isActive: true, isEssential: false },
      { id: 'H005', name: 'المذاكرة وتطوير المهارات', icon: '📚', color: '#10b981', freq: 'DAILY', preferredTime: '10:00', goalId: 'G002', isActive: true, isEssential: false }
    ],
    completions: [
      { habitId: 'H001', date: todayStr, time: '08:15' },
      { habitId: 'H002', date: todayStr, time: '05:30' },
      { habitId: 'H003', date: todayStr, time: '18:30' }
    ],
    tasks: [
      { id: 'T001', title: 'دراسة English (الوحدة 15)', desc: 'مراجعة الكلمات وقواعد المحادثة', dueDate: todayStr, priority: 'HIGH', estMins: 25, goalId: 'G001', isCompleted: false },
      { id: 'T002', title: 'إنهاء مهمة تطوير الواجهة', desc: 'ضبط الألوان والرسوم البيانية', dueDate: todayStr, priority: 'MEDIUM', estMins: 60, goalId: 'G002', isCompleted: true },
      { id: 'T003', title: 'شراء كابل USB', desc: 'من المتجر القريب', dueDate: todayStr, priority: 'LOW', estMins: 10, goalId: null, isCompleted: false },
      { id: 'T004', title: 'مراجعة الدرس وتلخيصه', desc: 'كتابة الملاحظات الهامة', dueDate: todayStr, priority: 'MEDIUM', estMins: 30, goalId: 'G001', isCompleted: false }
    ],
    goals: [
      {
        id: 'G001',
        title: 'الوصول إلى مستوى B1 في الإنجليزية',
        category: 'لغات',
        color: '#3b82f6',
        milestones: [
          { id: 'M001', title: 'A1', percent: 100 },
          { id: 'M002', title: 'A2', percent: 70 },
          { id: 'M003', title: 'B1', percent: 40 }
        ]
      },
      {
        id: 'G002',
        title: 'تعلم البرمجة وتطوير التطبيقات',
        category: 'برمجة وتقنية',
        color: '#8b5cf6',
        milestones: [
          { id: 'M004', title: 'مستوى مبتدئ', percent: 100 },
          { id: 'M005', title: 'مستوى متوسط', percent: 50 },
          { id: 'M006', title: 'مستوى متقدم', percent: 20 }
        ]
      }
    ],
    focusSessions: [
      { id: 'FS001', title: 'جلسة تركيز English', duration: 25, status: 'COMPLETED', date: todayStr, time: '09:00', relatedName: 'English A1' },
      { id: 'FS002', title: 'جلسة برمجة جزئية', duration: 15, status: 'PARTIAL', date: todayStr, time: '11:30', relatedName: 'تطوير الواجهة' }
    ],
    dailyReviews: {
      [todayStr]: {
        mood: 'HAPPY',
        achievement: 'إكمال 3 عادات وجلسة تركيز English وجلسة التمرين',
        notDone: 'شراء كابل USB',
        note: 'اليوم كان إنتاجه جيد ومستقر. استمريت في التركيز الصباحي وخلصت التمارين بنجاح.'
      }
    },
    restDays: {},
    settings: {
      isRestDayToday: false,
      restDayFriday: true,
      defaultDuration: 25,
      soundEnabled: true
    },
    timelineEvents: [
      { time: '08:15', title: 'الصلاة', subtitle: 'عادة مكتملة ✓', icon: '🕌', color: '#10b981', date: todayStr },
      { time: '09:00', title: 'English Focus', subtitle: '25 دقيقة • English A1', icon: '⏱️', color: '#f97316', date: todayStr },
      { time: '10:00', title: 'مهمة تطوير الواجهة', subtitle: 'مهمة منجزة ✓', icon: '✅', color: '#3b82f6', date: todayStr },
      { time: '18:30', title: 'التمرين', subtitle: 'عادة مكتملة ✓', icon: '💪', color: '#ec4899', date: todayStr },
      { time: '22:00', title: 'ملاحظة يومية', subtitle: 'يوم جيد، استمرت على خطتي', icon: '📝', color: '#f59e0b', date: todayStr }
    ]
  };

  // Load from LocalStorage or seed
  let appState = JSON.parse(localStorage.getItem('achievement_app_data')) || defaultData;

  function saveState() {
    localStorage.setItem('achievement_app_data', JSON.stringify(appState));
  }

  // --- DOM Elements ---
  const headerDateText = document.getElementById('headerDateText');
  const restModeHeaderBtn = document.getElementById('restModeHeaderBtn');
  const restBadgeLabel = document.getElementById('restBadgeLabel');
  const restBanner = document.getElementById('restBanner');
  const todayActionList = document.getElementById('todayActionList');
  const habitsRing = document.getElementById('habitsRing');
  const tasksRing = document.getElementById('tasksRing');
  const focusRing = document.getElementById('focusRing');
  const habitsDoneFraction = document.getElementById('habitsDoneFraction');
  const habitsPercent = document.getElementById('habitsPercent');
  const tasksDoneFraction = document.getElementById('tasksDoneFraction');
  const tasksPercent = document.getElementById('tasksPercent');
  const focusHoursMinutes = document.getElementById('focusHoursMinutes');
  const focusPercent = document.getElementById('focusPercent');
  const motivationalMessage = document.getElementById('motivationalMessage');
  const todayItemsCount = document.getElementById('todayItemsCount');
  const homeDailyNotePreview = document.getElementById('homeDailyNotePreview');

  // --- Render Top Header ---
  headerDateText.textContent = formatArabicDate(today);

  // --- UI Toast ---
  function showToast(msg) {
    const toast = document.getElementById('appToast');
    toast.textContent = msg;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 2500);
  }

  // --- Ring Offset Math (r=32 => perimeter = 2 * PI * 32 = 201.06) ---
  const CIRCLE_CIRCUMFERENCE = 201.06;
  function updateRing(element, percent) {
    const p = Math.min(100, Math.max(0, percent));
    const offset = CIRCLE_CIRCUMFERENCE - (p / 100) * CIRCLE_CIRCUMFERENCE;
    element.style.strokeDashoffset = offset;
  }

  // --- Calculate & Refresh Today Summary ---
  function refreshTodayStats() {
    const isRest = appState.settings.isRestDayToday;
    
    // In Rest Mode, non-essential habits do NOT count towards pressure!
    const relevantHabits = isRest 
      ? appState.habits.filter(h => h.isActive && h.isEssential)
      : appState.habits.filter(h => h.isActive);

    const completedHabitIds = new Set(
      appState.completions.filter(c => c.date === todayStr).map(c => c.habitId)
    );

    const habitsDone = relevantHabits.filter(h => completedHabitIds.has(h.id)).length;
    const habitsTotal = relevantHabits.length;
    const habitPct = habitsTotal > 0 ? Math.round((habitsDone / habitsTotal) * 100) : 0;

    const todayTasks = appState.tasks.filter(t => t.dueDate === todayStr);
    const tasksDone = todayTasks.filter(t => t.isCompleted).length;
    const tasksTotal = todayTasks.length;
    const tasksPct = tasksTotal > 0 ? Math.round((tasksDone / tasksTotal) * 100) : 0;

    const todaySessions = appState.focusSessions.filter(s => s.date === todayStr);
    const totalFocusMinutes = todaySessions.reduce((acc, s) => acc + s.duration, 0);
    const focusHours = Math.floor(totalFocusMinutes / 60);
    const focusMins = totalFocusMinutes % 60;
    const focusPct = Math.min(100, Math.round((totalFocusMinutes / 120) * 100)); // 2h baseline

    // Update Rings
    updateRing(habitsRing, habitPct);
    habitsDoneFraction.textContent = `${habitsDone}/${habitsTotal}`;
    habitsPercent.textContent = `${habitPct}%`;

    updateRing(tasksRing, tasksPct);
    tasksDoneFraction.textContent = `${tasksDone}/${tasksTotal}`;
    tasksPercent.textContent = `${tasksPct}%`;

    updateRing(focusRing, focusPct);
    focusHoursMinutes.textContent = focusHours > 0 ? `${focusHours}h ${focusMins}m` : `${focusMins}m`;
    focusPercent.textContent = `${focusPct}%`;

    // Motivational message (Non-judgmental philosophy from brief)
    if (isRest) {
      motivationalMessage.textContent = 'اليوم راحة 🏖️ شحن الطاقة جزء أساسي من خطتك';
    } else {
      const overall = Math.round(((habitsDone + tasksDone) / Math.max(1, habitsTotal + tasksTotal)) * 100);
      if (overall >= 80) {
        motivationalMessage.textContent = 'أداء مبهر اليوم! واصل بهذا الإيقاع 🚀';
      } else if (overall >= 50) {
        motivationalMessage.textContent = 'أحسنت! أنت على الطريق الصحيح ✨';
      } else if (overall > 0) {
        motivationalMessage.textContent = 'بداية جيدة.. خطوة صغيرة بعد أخرى 🌱';
      } else {
        motivationalMessage.textContent = 'لسه اليوم فيه وقت .. فرصة جديدة للانطلاق';
      }
    }

    // Rest Banner & Header Button
    if (isRest) {
      restModeHeaderBtn.classList.add('active');
      restBadgeLabel.textContent = 'راحة 🏖️';
      restBanner.style.display = 'flex';
    } else {
      restModeHeaderBtn.classList.remove('active');
      restBadgeLabel.textContent = 'راحة؟';
      restBanner.style.display = 'none';
    }

    const settingsSwitch = document.getElementById('restModeSettingsSwitch');
    if (settingsSwitch) settingsSwitch.checked = isRest;

    // Home Daily Note preview
    const todayReview = appState.dailyReviews[todayStr];
    if (todayReview && todayReview.note) {
      homeDailyNotePreview.textContent = todayReview.note;
    } else {
      homeDailyNotePreview.textContent = 'لا توجد ملاحظة مسجلة بعد. دوّن ملخصًا سريعًا ليومك هنا...';
    }
  }

  // --- Render Action Items: "What should I do now?" ---
  function renderTodayActionList() {
    const isRest = appState.settings.isRestDayToday;
    const relevantHabits = isRest 
      ? appState.habits.filter(h => h.isActive && h.isEssential)
      : appState.habits.filter(h => h.isActive);

    const completedHabitIds = new Set(
      appState.completions.filter(c => c.date === todayStr).map(c => c.habitId)
    );

    const todayTasks = appState.tasks.filter(t => t.dueDate === todayStr);

    todayItemsCount.textContent = `${relevantHabits.length + todayTasks.length} عناصر`;
    todayActionList.innerHTML = '';

    // 1. Render Habits
    relevantHabits.forEach(habit => {
      const isDone = completedHabitIds.has(habit.id);
      const itemEl = document.createElement('div');
      itemEl.className = `action-item ${isDone ? 'completed' : ''}`;
      itemEl.innerHTML = `
        <div class="custom-checkbox"></div>
        <div class="item-icon-box green">${habit.icon || '🔁'}</div>
        <div class="item-details">
          <div class="item-title">${habit.name}</div>
          <div class="item-sub">${habit.preferredTime ? habit.preferredTime : (habit.isEssential ? 'عادة أساسية' : 'عادة يومية')}</div>
        </div>
        <div class="item-badge green">عادة</div>
      `;

      itemEl.addEventListener('click', () => {
        toggleHabitCompletion(habit.id);
      });

      todayActionList.appendChild(itemEl);
    });

    // 2. Render Tasks
    todayTasks.forEach(task => {
      const itemEl = document.createElement('div');
      itemEl.className = `action-item ${task.isCompleted ? 'completed' : ''}`;
      itemEl.innerHTML = `
        <div class="custom-checkbox"></div>
        <div class="item-icon-box blue">✅</div>
        <div class="item-details">
          <div class="item-title">${task.title}</div>
          <div class="item-sub">${task.estMins} دقيقة ${task.priority === 'HIGH' ? '• أولوية عالية 🔴' : ''}</div>
        </div>
        <div class="item-badge blue">مهمة</div>
      `;

      itemEl.addEventListener('click', () => {
        toggleTaskCompletion(task.id);
      });

      todayActionList.appendChild(itemEl);
    });

    // 3. Quick Focus Suggestion Card
    const focusEl = document.createElement('div');
    focusEl.className = 'action-item';
    focusEl.innerHTML = `
      <div class="item-icon-box orange" style="border-radius: 50%;">▶</div>
      <div class="item-icon-box orange">⏱️</div>
      <div class="item-details">
        <div class="item-title">جلسة تركيز مقترحة (English A1)</div>
        <div class="item-sub">25 دقيقة • Pomodoro</div>
      </div>
      <div class="item-badge orange">تركيز</div>
    `;
    focusEl.addEventListener('click', () => {
      switchTab('screenFocus');
    });
    todayActionList.appendChild(focusEl);
  }

  // --- Toggle Habit ---
  function toggleHabitCompletion(habitId) {
    const habit = appState.habits.find(h => h.id === habitId);
    if (!habit) return;

    const existingIndex = appState.completions.findIndex(
      c => c.habitId === habitId && c.date === todayStr
    );

    if (existingIndex >= 0) {
      appState.completions.splice(existingIndex, 1);
      // Remove from timeline
      appState.timelineEvents = appState.timelineEvents.filter(
        e => !(e.title === habit.name && e.date === todayStr)
      );
      showToast(`تم إلغاء تحديد "${habit.name}"`);
    } else {
      const timeNow = new Date().toTimeString().substring(0, 5);
      appState.completions.push({ habitId, date: todayStr, time: timeNow });
      appState.timelineEvents.unshift({
        time: timeNow,
        title: habit.name,
        subtitle: 'عادة مكتملة ✓',
        icon: habit.icon || '🕌',
        color: habit.color || '#10b981',
        date: todayStr
      });
      showToast(`أحسنت! أتممت "${habit.name}" ✓`);
    }

    saveState();
    refreshTodayStats();
    renderTodayActionList();
    renderTimelineEvents();
  }

  // --- Toggle Task ---
  function toggleTaskCompletion(taskId) {
    const task = appState.tasks.find(t => t.id === taskId);
    if (!task) return;

    task.isCompleted = !task.isCompleted;
    const timeNow = new Date().toTimeString().substring(0, 5);

    if (task.isCompleted) {
      appState.timelineEvents.unshift({
        time: timeNow,
        title: task.title,
        subtitle: 'مهمة منجزة ✓',
        icon: '✅',
        color: '#3b82f6',
        date: todayStr
      });
      showToast(`تم إنجاز المهمة: "${task.title}" ✓`);
    } else {
      appState.timelineEvents = appState.timelineEvents.filter(
        e => !(e.title === task.title && e.date === todayStr)
      );
      showToast(`تمت إعادة المهمة: "${task.title}"`);
    }

    saveState();
    refreshTodayStats();
    renderTodayActionList();
    renderTasksList();
    renderTimelineEvents();
  }

  // --- Toggle Rest Day ---
  function toggleRestDay() {
    appState.settings.isRestDayToday = !appState.settings.isRestDayToday;
    const isRest = appState.settings.isRestDayToday;

    const timeNow = new Date().toTimeString().substring(0, 5);
    if (isRest) {
      appState.restDays[todayStr] = true;
      appState.timelineEvents.unshift({
        time: timeNow,
        title: 'يوم راحة 🏖️',
        subtitle: 'شحن الطاقة والاستراحة',
        icon: '🏖️',
        color: '#06b6d4',
        date: todayStr
      });
      showToast('تم تفعيل وضع الراحة لهذا اليوم 🏖️');
    } else {
      delete appState.restDays[todayStr];
      appState.timelineEvents = appState.timelineEvents.filter(
        e => !(e.title.includes('راحة') && e.date === todayStr)
      );
      showToast('تم إلغاء وضع الراحة');
    }

    saveState();
    refreshTodayStats();
    renderTodayActionList();
    renderTimelineEvents();
  }

  restModeHeaderBtn.addEventListener('click', toggleRestDay);
  const restModeSettingsSwitch = document.getElementById('restModeSettingsSwitch');
  if (restModeSettingsSwitch) {
    restModeSettingsSwitch.addEventListener('change', toggleRestDay);
  }

  // --- Bottom Navigation Routing ---
  const navItems = document.querySelectorAll('.nav-item');
  const screens = document.querySelectorAll('.screen');

  function switchTab(screenId) {
    screens.forEach(s => s.classList.remove('active'));
    navItems.forEach(n => n.classList.remove('active'));

    const targetScreen = document.getElementById(screenId);
    if (targetScreen) targetScreen.classList.add('active');

    const activeNav = document.querySelector(`.nav-item[data-target="${screenId}"]`);
    if (activeNav) activeNav.classList.add('active');

    // Scroll to top
    document.getElementById('mainContent').scrollTop = 0;

    // Refresh specific screen content
    if (screenId === 'screenProgress') renderProgressTabs();
    if (screenId === 'screenFocus') refreshFocusUI();
    if (screenId === 'screenAnalytics') renderAnalyticsScreen();
  }

  navItems.forEach(item => {
    item.addEventListener('click', () => {
      const targetId = item.getAttribute('data-target');
      switchTab(targetId);
    });
  });

  // --- Modals Controller ---
  function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('active');
  }

  function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('active');
  }

  document.querySelectorAll('[data-close]').forEach(btn => {
    btn.addEventListener('click', () => {
      closeModal(btn.getAttribute('data-close'));
    });
  });

  document.querySelectorAll('.modal-backdrop').forEach(backdrop => {
    backdrop.addEventListener('click', (e) => {
      if (e.target === backdrop) backdrop.classList.remove('active');
    });
  });

  // Header and Home Quick Add buttons
  document.getElementById('headerQuickAddBtn').addEventListener('click', () => openModal('modalQuickAddMenu'));
  document.getElementById('homeQuickAddBtn').addEventListener('click', () => openModal('modalQuickAddMenu'));

  document.querySelectorAll('.quick-opt-item[data-open]').forEach(opt => {
    opt.addEventListener('click', () => {
      closeModal('modalQuickAddMenu');
      openModal(opt.getAttribute('data-open'));
    });
  });

  document.querySelector('.quick-opt-item[data-action="goFocus"]').addEventListener('click', () => {
    closeModal('modalQuickAddMenu');
    switchTab('screenFocus');
  });

  // --- Add Task Handler ---
  document.getElementById('confirmAddTaskBtn').addEventListener('click', () => {
    const title = document.getElementById('taskTitleInput').value.trim();
    if (!title) return alert('الرجاء إدخال عنوان المهمة');

    const desc = document.getElementById('taskDescInput').value.trim();
    const priority = document.getElementById('taskPrioritySelect').value;
    const estMins = parseInt(document.getElementById('taskEstMinSelect').value) || 25;
    const goalId = document.getElementById('taskGoalSelect').value || null;

    const newTask = {
      id: 'T' + Date.now().toString().slice(-5),
      title,
      desc,
      dueDate: todayStr,
      priority,
      estMins,
      goalId,
      isCompleted: false
    };

    appState.tasks.push(newTask);
    saveState();
    closeModal('modalAddTask');
    document.getElementById('taskTitleInput').value = '';
    document.getElementById('taskDescInput').value = '';
    showToast('تمت إضافة المهمة بنجاح ✅');
    refreshTodayStats();
    renderTodayActionList();
    renderTasksList();
  });

  // --- Add Habit Handler ---
  document.getElementById('confirmAddHabitBtn').addEventListener('click', () => {
    const name = document.getElementById('habitNameInput').value.trim();
    if (!name) return alert('الرجاء إدخال اسم العادة');

    const freq = document.getElementById('habitFreqSelect').value;
    const time = document.getElementById('habitTimeInput').value.trim();
    const isEssential = document.getElementById('habitEssentialCheckbox').checked;
    const goalId = document.getElementById('habitGoalSelect').value || null;

    const newHabit = {
      id: 'H' + Date.now().toString().slice(-5),
      name,
      icon: '🔁',
      color: '#10b981',
      freq,
      preferredTime: time || null,
      goalId,
      isActive: true,
      isEssential
    };

    appState.habits.push(newHabit);
    saveState();
    closeModal('modalAddHabit');
    document.getElementById('habitNameInput').value = '';
    document.getElementById('habitTimeInput').value = '';
    showToast('تم إنشاء العادة بنجاح 🔁');
    refreshTodayStats();
    renderTodayActionList();
    renderHabitsList();
  });

  // --- Add Goal Handler ---
  let tempMilestones = ['A1', 'A2', 'B1'];
  document.getElementById('addMilestoneBtn').addEventListener('click', () => {
    const val = document.getElementById('milestoneInput').value.trim();
    if (val) {
      tempMilestones.push(val);
      document.getElementById('milestoneInput').value = '';
      renderTempMilestones();
    }
  });

  function renderTempMilestones() {
    const cont = document.getElementById('goalMilestonesContainer');
    cont.innerHTML = '';
    tempMilestones.forEach((m, idx) => {
      const chip = document.createElement('div');
      chip.className = 'milestone-chip';
      chip.textContent = m;
      chip.title = 'اضغط للحذف';
      chip.style.cursor = 'pointer';
      chip.addEventListener('click', () => {
        tempMilestones.splice(idx, 1);
        renderTempMilestones();
      });
      cont.appendChild(chip);
    });
  }

  document.getElementById('confirmAddGoalBtn').addEventListener('click', () => {
    const title = document.getElementById('goalTitleInput').value.trim();
    if (!title) return alert('الرجاء إدخال اسم الهدف');

    const category = document.getElementById('goalCategorySelect').value;
    const milestones = tempMilestones.map((m, i) => ({
      id: 'M' + Date.now() + i,
      title: m,
      percent: i === 0 ? 50 : 0
    }));

    const newGoal = {
      id: 'G' + Date.now().toString().slice(-5),
      title,
      category,
      color: '#8b5cf6',
      milestones
    };

    appState.goals.push(newGoal);
    saveState();
    closeModal('modalAddGoal');
    document.getElementById('goalTitleInput').value = '';
    showToast('تم إنشاء الهدف والمراحل بنجاح 🎯');
    renderGoalsList();
  });

  // --- Daily Note & Review Handlers ---
  document.getElementById('openDailyReviewBtn').addEventListener('click', () => {
    const existing = appState.dailyReviews[todayStr] || {};
    document.getElementById('reviewAchievementInput').value = existing.achievement || '';
    document.getElementById('reviewNotDoneInput').value = existing.notDone || '';
    document.getElementById('reviewNoteInput').value = existing.note || '';
    openModal('modalDailyReview');
  });

  document.querySelectorAll('.mood-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.mood-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
    });
  });

  document.getElementById('confirmSaveReviewBtn').addEventListener('click', () => {
    const activeMoodBtn = document.querySelector('.mood-btn.active');
    const mood = activeMoodBtn ? activeMoodBtn.getAttribute('data-mood') : 'HAPPY';
    const achievement = document.getElementById('reviewAchievementInput').value.trim();
    const notDone = document.getElementById('reviewNotDoneInput').value.trim();
    const note = document.getElementById('reviewNoteInput').value.trim();

    appState.dailyReviews[todayStr] = { mood, achievement, notDone, note };
    saveState();
    closeModal('modalDailyReview');
    showToast('تم حفظ مراجعة اليوم بنجاح 🧠');
    refreshTodayStats();
  });

  document.getElementById('confirmSaveNoteBtn').addEventListener('click', () => {
    const note = document.getElementById('dailyNoteInput').value.trim();
    if (!note) return;

    if (!appState.dailyReviews[todayStr]) appState.dailyReviews[todayStr] = { mood: 'HAPPY' };
    appState.dailyReviews[todayStr].note = note;

    appState.timelineEvents.unshift({
      time: new Date().toTimeString().substring(0, 5),
      title: 'ملاحظة يومية',
      subtitle: note.slice(0, 40),
      icon: '📝',
      color: '#f59e0b',
      date: todayStr
    });

    saveState();
    closeModal('modalAddNote');
    document.getElementById('dailyNoteInput').value = '';
    showToast('تم تدوين الملاحظة بنجاح 📝');
    refreshTodayStats();
    renderTimelineEvents();
  });

  // --- Progress Tabs (Habits, Goals, Tasks) ---
  const segTabBtns = document.querySelectorAll('#screenProgress .tab-btn');
  segTabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      segTabBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      const tab = btn.getAttribute('data-tab');
      document.querySelectorAll('#screenProgress .tab-content').forEach(c => c.classList.remove('active'));
      const activeContent = document.getElementById(`tab${tab.charAt(0).toUpperCase() + tab.slice(1)}`);
      if (activeContent) activeContent.classList.add('active');
    });
  });

  function renderProgressTabs() {
    renderHabitsList();
    renderGoalsList();
    renderTasksList();
  }

  // Habits Tab Rendering (Crucial: Inactive ≠ Deleted!)
  function renderHabitsList() {
    const habitsList = document.getElementById('habitsList');
    const pausedHabitsList = document.getElementById('pausedHabitsList');
    const pausedTitle = document.getElementById('pausedHabitsTitle');
    habitsList.innerHTML = '';
    pausedHabitsList.innerHTML = '';

    const activeHabits = appState.habits.filter(h => h.isActive);
    const pausedHabits = appState.habits.filter(h => !h.isActive);

    activeHabits.forEach(habit => {
      const card = createHabitCard(habit);
      habitsList.appendChild(card);
    });

    if (pausedHabits.length > 0) {
      pausedTitle.style.display = 'block';
      pausedHabits.forEach(habit => {
        const card = createHabitCard(habit);
        pausedHabitsList.appendChild(card);
      });
    } else {
      pausedTitle.style.display = 'none';
    }
  }

  function createHabitCard(habit) {
    const card = document.createElement('div');
    card.className = 'card';
    card.style.display = 'flex';
    card.style.justifyContent = 'space-between';
    card.style.alignItems = 'center';
    card.style.padding = '12px 14px';

    card.innerHTML = `
      <div style="display:flex; align-items:center; gap:12px;">
        <div class="item-icon-box green" style="background:rgba(16, 185, 129, ${habit.isActive ? '0.15' : '0.05'});">${habit.icon || '🔁'}</div>
        <div>
          <div style="display:flex; align-items:center; gap:6px;">
            <strong style="color: ${habit.isActive ? 'var(--text-primary)' : 'var(--text-muted)'}">${habit.name}</strong>
            ${habit.isEssential ? '<span class="item-badge" style="background:rgba(245,158,11,0.15); color:var(--accent-gold); font-size:0.65rem;">أساسية</span>' : ''}
          </div>
          <div style="font-size:0.75rem; color:var(--text-muted); margin-top:2px;">
            ${habit.freq === 'DAILY' ? 'يوميًا' : '3 مرات أسبوعيًا'} ${habit.preferredTime ? '• ' + habit.preferredTime : ''}
          </div>
        </div>
      </div>
      <label class="switch">
        <input type="checkbox" ${habit.isActive ? 'checked' : ''}>
        <span class="slider"></span>
      </label>
    `;

    card.querySelector('input').addEventListener('change', (e) => {
      habit.isActive = e.target.checked;
      saveState();
      showToast(habit.isActive ? `تمت إعادة تفعيل "${habit.name}"` : `تم إيقاف "${habit.name}" مؤقتًا (تاريخها محفوظ)`);
      refreshTodayStats();
      renderTodayActionList();
      renderHabitsList();
    });

    return card;
  }

  // Goals Tab Rendering
  function renderGoalsList() {
    const goalsList = document.getElementById('goalsList');
    goalsList.innerHTML = '';

    appState.goals.forEach(goal => {
      const avgProgress = goal.milestones.length > 0 
        ? Math.round(goal.milestones.reduce((acc, m) => acc + m.percent, 0) / goal.milestones.length)
        : 0;

      const card = document.createElement('div');
      card.className = 'card';
      card.innerHTML = `
        <div class="card-header">
          <div style="display:flex; align-items:center; gap:10px;">
            <div class="item-icon-box purple">🎯</div>
            <div>
              <h4 class="card-title-sm">${goal.title}</h4>
              <span class="card-subtitle">${goal.category}</span>
            </div>
          </div>
          <span class="item-badge purple" style="font-size:0.8rem;">${avgProgress}%</span>
        </div>
        <div class="progress-bar-bg" style="margin-bottom:12px;">
          <div class="progress-bar-fill purple" style="width: ${avgProgress}%"></div>
        </div>
        <div style="font-size:0.78rem; font-weight:700; color:var(--text-secondary); margin-bottom:6px;">مراحل الهدف:</div>
        <div class="milestone-rows-container"></div>
      `;

      const mContainer = card.querySelector('.milestone-rows-container');
      goal.milestones.forEach(m => {
        const mRow = document.createElement('div');
        mRow.style.cssText = 'display:flex; justify-content:space-between; align-items:center; padding:4px 0; font-size:0.8rem;';
        mRow.innerHTML = `
          <span>${m.percent >= 100 ? '✅' : '⏳'} ${m.title}</span>
          <div style="display:flex; align-items:center; gap:8px;">
            <input type="range" min="0" max="100" value="${m.percent}" style="width:80px; accent-color:var(--goal-purple);">
            <span style="font-weight:700; color:var(--goal-purple); width:32px; text-align:left;">${m.percent}%</span>
          </div>
        `;

        mRow.querySelector('input').addEventListener('input', (e) => {
          m.percent = parseInt(e.target.value);
          saveState();
          renderGoalsList();
        });

        mContainer.appendChild(mRow);
      });

      goalsList.appendChild(card);
    });
  }

  // Tasks Tab Rendering
  function renderTasksList() {
    const tasksList = document.getElementById('tasksList');
    const completedTasksList = document.getElementById('completedTasksList');
    tasksList.innerHTML = '';
    completedTasksList.innerHTML = '';

    const incomplete = appState.tasks.filter(t => !t.isCompleted);
    const completed = appState.tasks.filter(t => t.isCompleted);

    incomplete.forEach(t => tasksList.appendChild(createTaskCard(t)));
    completed.forEach(t => completedTasksList.appendChild(createTaskCard(t)));
  }

  function createTaskCard(task) {
    const card = document.createElement('div');
    card.className = `card ${task.isCompleted ? 'completed' : ''}`;
    card.style.cssText = 'display:flex; align-items:center; gap:12px; padding:12px; margin-bottom:8px;';
    card.innerHTML = `
      <div class="custom-checkbox" style="${task.isCompleted ? 'background:var(--task-blue); border-color:var(--task-blue); color:white;' : ''}">
        ${task.isCompleted ? '✓' : ''}
      </div>
      <div style="flex:1;">
        <strong style="font-size:0.9rem; ${task.isCompleted ? 'text-decoration:line-through; color:var(--text-muted);' : ''}">${task.title}</strong>
        <div style="font-size:0.75rem; color:var(--text-muted); margin-top:2px;">
          ${task.dueDate} • ${task.estMins} دقيقة ${task.desc ? '• ' + task.desc : ''}
        </div>
      </div>
      <span class="item-badge ${task.priority === 'HIGH' ? 'orange' : 'blue'}">
        ${task.priority === 'HIGH' ? 'عالية' : (task.priority === 'LOW' ? 'منخفضة' : 'متوسطة')}
      </span>
    `;

    card.addEventListener('click', () => {
      toggleTaskCompletion(task.id);
    });

    return card;
  }

  // --- Focus Screen & Pomodoro Engine ---
  let timerTargetMinutes = 25;
  let timerRemainingSeconds = 25 * 60;
  let timerInterval = null;
  let timerIsRunning = false;
  let timerStartedAt = null;

  const timerDigits = document.getElementById('timerDigits');
  const timerRingFg = document.getElementById('timerRingFg');
  const timerPlayPauseBtn = document.getElementById('timerPlayPauseBtn');
  const timerStopBtn = document.getElementById('timerStopBtn');
  const timerStateLabel = document.getElementById('timerStateLabel');
  const timerLinkedTag = document.getElementById('timerLinkedTag');
  const focusSumBadge = document.getElementById('focusSumBadge');
  const todaySessionsList = document.getElementById('todaySessionsList');
  const focusTargetSelect = document.getElementById('focusTargetSelect');

  // Ring circumference for 105 radius = 2 * PI * 105 = 659.73
  const TIMER_CIRCUMFERENCE = 659.73;

  function updateTimerRing() {
    const fraction = timerRemainingSeconds / (timerTargetMinutes * 60);
    const offset = TIMER_CIRCUMFERENCE - (fraction * TIMER_CIRCUMFERENCE);
    timerRingFg.style.strokeDashoffset = offset;

    const mins = Math.floor(timerRemainingSeconds / 60);
    const secs = timerRemainingSeconds % 60;
    timerDigits.textContent = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  }

  function startTimer() {
    if (timerIsRunning) return;
    timerIsRunning = true;
    timerStartedAt = Date.now();
    timerPlayPauseBtn.textContent = '⏸';
    timerStateLabel.textContent = 'وقت التركيز ⏱️';

    timerInterval = setInterval(() => {
      if (timerRemainingSeconds > 0) {
        timerRemainingSeconds--;
        updateTimerRing();
      } else {
        clearInterval(timerInterval);
        timerIsRunning = false;
        completeFocusSession(true);
      }
    }, 1000);
  }

  function pauseTimer() {
    timerIsRunning = false;
    clearInterval(timerInterval);
    timerPlayPauseBtn.textContent = '▶';
    timerStateLabel.textContent = 'متوقف مؤقتًا';
  }

  // CRITICAL RULE from prompt: If stopped after 13 mins, save 13 min Focus Time with Partial status!
  function stopAndSavePartial() {
    if (timerInterval) clearInterval(timerInterval);
    timerIsRunning = false;
    timerPlayPauseBtn.textContent = '▶';

    const elapsedSeconds = (timerTargetMinutes * 60) - timerRemainingSeconds;
    const elapsedMinutes = Math.floor(elapsedSeconds / 60);

    if (elapsedMinutes >= 1) {
      completeFocusSession(false, elapsedMinutes);
    } else {
      timerRemainingSeconds = timerTargetMinutes * 60;
      updateTimerRing();
      timerStateLabel.textContent = 'جاهز للبدء';
      showToast('تم إلغاء الجلسة (أقل من دقيقة)');
    }
  }

  function completeFocusSession(isCompleted, actualMinutes) {
    const duration = actualMinutes !== undefined ? actualMinutes : timerTargetMinutes;
    const selectedLinkName = focusTargetSelect.value || timerLinkedTag.textContent || 'جلسة حرة';
    const timeNow = new Date().toTimeString().substring(0, 5);

    const newSession = {
      id: 'FS' + Date.now().toString().slice(-5),
      title: `جلسة تركيز ${selectedLinkName}`,
      duration,
      status: isCompleted ? 'COMPLETED' : 'PARTIAL',
      date: todayStr,
      time: timeNow,
      relatedName: selectedLinkName
    };

    appState.focusSessions.unshift(newSession);

    appState.timelineEvents.unshift({
      time: timeNow,
      title: newSession.title,
      subtitle: `${duration} دقيقة • ${isCompleted ? 'مكتملة ✓' : 'جلسة جزئية'}`,
      icon: '⏱️',
      color: '#f97316',
      date: todayStr
    });

    saveState();
    timerRemainingSeconds = timerTargetMinutes * 60;
    updateTimerRing();
    timerStateLabel.textContent = 'جاهز للبدء';

    showToast(isCompleted 
      ? `جلسة تركيز مكتملة بنجاح ✅ (${duration} دقيقة)`
      : `تم حفظ ${duration} دقيقة وقت تركيز (جلسة جزئية) ⏱️`
    );

    refreshTodayStats();
    refreshFocusUI();
    renderTimelineEvents();
  }

  timerPlayPauseBtn.addEventListener('click', () => {
    if (timerIsRunning) pauseTimer(); else startTimer();
  });

  timerStopBtn.addEventListener('click', stopAndSavePartial);

  // Preset Buttons
  document.querySelectorAll('.preset-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      if (timerIsRunning) return;
      document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      timerTargetMinutes = parseInt(btn.getAttribute('data-min'));
      timerRemainingSeconds = timerTargetMinutes * 60;
      updateTimerRing();
    });
  });

  // Focus link type chips
  document.querySelectorAll('#focusLinkTypeChips .chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('#focusLinkTypeChips .chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      const type = chip.getAttribute('data-type');
      populateFocusTargetSelect(type);
    });
  });

  function populateFocusTargetSelect(type) {
    focusTargetSelect.innerHTML = '';
    if (type === 'NONE') {
      focusTargetSelect.innerHTML = '<option value="جلسة حرة">جلسة حرة بدون ربط</option>';
      timerLinkedTag.textContent = 'جلسة حرة';
      return;
    }

    if (type === 'TASK') {
      const incomplete = appState.tasks.filter(t => !t.isCompleted);
      incomplete.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.title;
        opt.textContent = t.title;
        focusTargetSelect.appendChild(opt);
      });
      if (incomplete.length > 0) timerLinkedTag.textContent = incomplete[0].title;
    } else if (type === 'GOAL') {
      appState.goals.forEach(g => {
        const opt = document.createElement('option');
        opt.value = g.title;
        opt.textContent = g.title;
        focusTargetSelect.appendChild(opt);
      });
      if (appState.goals.length > 0) timerLinkedTag.textContent = appState.goals[0].title;
    } else if (type === 'HABIT') {
      appState.habits.forEach(h => {
        const opt = document.createElement('option');
        opt.value = h.name;
        opt.textContent = h.name;
        focusTargetSelect.appendChild(opt);
      });
      if (appState.habits.length > 0) timerLinkedTag.textContent = appState.habits[0].name;
    }
  }

  focusTargetSelect.addEventListener('change', (e) => {
    timerLinkedTag.textContent = e.target.value;
  });

  function refreshFocusUI() {
    populateFocusTargetSelect('TASK');
    const todaySessions = appState.focusSessions.filter(s => s.date === todayStr);
    const sumMins = todaySessions.reduce((acc, s) => acc + s.duration, 0);
    const h = Math.floor(sumMins / 60);
    const m = sumMins % 60;
    focusSumBadge.textContent = `المجموع: ${h > 0 ? `${h}س ` : ''}${m}د`;

    todaySessionsList.innerHTML = '';
    if (todaySessions.length === 0) {
      todaySessionsList.innerHTML = '<p style="color:var(--text-muted); font-size:0.75rem; padding:8px 0;">لا توجد جلسات مسجلة اليوم بعد.</p>';
      return;
    }

    todaySessions.forEach(s => {
      const row = document.createElement('div');
      row.style.cssText = 'display:flex; justify-content:space-between; align-items:center; padding:8px 0; border-bottom:1px solid var(--border-color); font-size:0.82rem;';
      row.innerHTML = `
        <div>
          <strong style="color:var(--text-primary);">${s.title}</strong>
          <div style="font-size:0.72rem; color:${s.status === 'PARTIAL' ? 'var(--accent-gold)' : 'var(--primary-emerald)'};">
            ${s.status === 'PARTIAL' ? 'جلسة جزئية' : 'مكتملة ✓'} • ${s.time || ''}
          </div>
        </div>
        <span class="item-badge orange">${s.duration} دقيقة</span>
      `;
      todaySessionsList.appendChild(row);
    });
  }

  // --- Analytics & Timeline Screen ---
  function renderAnalyticsScreen() {
    renderCalendarStrip();
    renderTimelineEvents();
  }

  function renderCalendarStrip() {
    const strip = document.getElementById('calendarDaysStrip');
    strip.innerHTML = '';

    for (let i = -3; i <= 3; i++) {
      const d = new Date(today);
      d.setDate(d.getDate() + i);
      const dString = d.toISOString().split('T')[0];
      const dayNum = d.getDate();
      const dayName = arabicDays[d.getDay()].slice(0, 3);
      const isSelected = dString === todayStr;
      const isRest = !!appState.restDays[dString];

      const box = document.createElement('div');
      box.className = `cal-day-box ${isSelected ? 'active' : ''}`;
      box.innerHTML = `
        <div class="cal-day-name">${dayName}</div>
        <div class="cal-day-num">${dayNum}</div>
        <div>${isRest ? '🏖️' : '🟢'}</div>
      `;

      box.addEventListener('click', () => {
        document.querySelectorAll('.cal-day-box').forEach(b => b.classList.remove('active'));
        box.classList.add('active');
        renderTimelineEvents(dString);
      });

      strip.appendChild(box);
    }
  }

  function renderTimelineEvents(targetDate = todayStr) {
    const list = document.getElementById('historyTimelineList');
    list.innerHTML = '';

    const events = appState.timelineEvents.filter(e => e.date === targetDate);
    if (events.length === 0) {
      list.innerHTML = '<div class="card" style="text-align:center; color:var(--text-muted); font-size:0.8rem;">لا توجد أحداث مسجلة في هذا اليوم</div>';
      return;
    }

    events.forEach(e => {
      const item = document.createElement('div');
      item.className = 'timeline-item';
      item.innerHTML = `
        <span class="timeline-time">${e.time || '--:--'}</span>
        <div class="timeline-card">
          <div style="display:flex; align-items:center; gap:10px;">
            <span style="font-size:1.1rem;">${e.icon || '✓'}</span>
            <div>
              <strong style="font-size:0.88rem; color:var(--text-primary);">${e.title}</strong>
              <div style="font-size:0.72rem; color:var(--text-muted);">${e.subtitle || ''}</div>
            </div>
          </div>
          <span style="color:${e.color || 'var(--primary-emerald)'}; font-size:0.85rem;">✓</span>
        </div>
      `;
      list.appendChild(item);
    });
  }

  // --- AI Report Generation (Structured Markdown for ChatGPT / Gemini) ---
  document.getElementById('generateAiReportBtn').addEventListener('click', () => {
    const reportText = generateAiMarkdownReport();
    document.getElementById('exportPreviewTitle').textContent = 'تقرير الذكاء الاصطناعي (AI Analysis)';
    document.getElementById('exportPreviewText').textContent = reportText;
    openModal('modalExportPreview');
  });

  function generateAiMarkdownReport() {
    const relevantHabits = appState.habits.filter(h => h.isActive);
    const completedCount = appState.completions.length;
    const tasksDone = appState.tasks.filter(t => t.isCompleted).length;
    const tasksTotal = appState.tasks.length;
    const focusMins = appState.focusSessions.reduce((acc, s) => acc + s.duration, 0);
    const focusH = Math.floor(focusMins / 60);
    const focusM = focusMins % 60;

    let md = `# 📱 ACHIEVEMENT WEEKLY REPORT — نظام إدارة الإنجاز\n\n`;
    md += `**الفترة:** ${formatArabicDate(new Date(Date.now() - 6 * 86400000))} → ${formatArabicDate(today)}\n\n`;
    md += `## 📊 SUMMARY / نظرة عامة\n`;
    md += `- **إكمال العادات:** 78% (${completedCount} تسجيلات)\n`;
    md += `- **المهام المنجزة:** ${tasksDone} / ${tasksTotal}\n`;
    md += `- **وقت التركيز:** ${focusH}س ${focusM}د (${focusMins} دقيقة)\n`;
    md += `- **أيام الراحة:** ${Object.keys(appState.restDays).length} يوم\n\n`;

    md += `## 🔁 HABITS / العادات\n`;
    relevantHabits.forEach(h => {
      const cCount = appState.completions.filter(c => c.habitId === h.id).length;
      md += `- **${h.name}:** ${cCount} مرات أسبوعيًا | النوع: ${h.freq}\n`;
    });
    md += `\n`;

    md += `## 🎯 GOALS / الأهداف والمراحل\n`;
    appState.goals.forEach(g => {
      const avg = Math.round(g.milestones.reduce((acc, m) => acc + m.percent, 0) / g.milestones.length);
      md += `### 🎯 ${g.title} (${avg}%)\n`;
      g.milestones.forEach(m => {
        md += `  - ${m.percent >= 100 ? '✅' : '⏳'} ${m.title}: ${m.percent}%\n`;
      });
    });
    md += `\n`;

    md += `## ⏱️ FOCUS / أين ذهب وقتك؟\n`;
    const grouped = {};
    appState.focusSessions.forEach(s => {
      const key = s.relatedName || 'عام';
      grouped[key] = (grouped[key] || 0) + s.duration;
    });
    for (let k in grouped) {
      const h = Math.floor(grouped[k] / 60);
      const m = grouped[k] % 60;
      md += `- **${k}:** ${h}h ${m}m\n`;
    }
    md += `\n`;

    md += `## 📝 DAILY REVIEWS & NOTES\n`;
    for (let d in appState.dailyReviews) {
      const r = appState.dailyReviews[d];
      md += `### ${d} (المزاج: ${r.mood || '🙂'})\n`;
      if (r.achievement) md += `- **أكبر إنجاز:** ${r.achievement}\n`;
      if (r.notDone) md += `- **ما لم يتم:** ${r.notDone}\n`;
      if (r.note) md += `- **ملاحظة:** ${r.note}\n`;
    }
    md += `\n`;

    md += `## 🤖 للتحليل مع الذكاء الاصطناعي (ChatGPT / Gemini)\n`;
    md += `> "حلل هذا الأسبوع معي: أين كان الالتزام جيدًا؟ أين ظهر التراجع؟ أين ذهب الوقت وما الأهداف التي تتحرك؟ وما هي خطة تحسين الأسبوع القادم؟"\n`;

    return md;
  }

  // Copy & Share buttons
  document.getElementById('copyExportTextBtn').addEventListener('click', () => {
    const text = document.getElementById('exportPreviewText').textContent;
    navigator.clipboard.writeText(text).then(() => {
      showToast('تم نسخ التقرير للحافظة! جاهز للصق في ChatGPT 🤖');
    });
  });

  document.getElementById('shareExportTextBtn').addEventListener('click', () => {
    const text = document.getElementById('exportPreviewText').textContent;
    if (navigator.share) {
      navigator.share({
        title: 'Achievement Weekly Report',
        text: text
      }).catch(() => {});
    } else {
      navigator.clipboard.writeText(text);
      showToast('تم نسخ التقرير');
    }
  });

  // --- Backup & Restore JSON ---
  document.getElementById('exportBackupJsonBtn').addEventListener('click', () => {
    const jsonStr = JSON.stringify(appState, null, 2);
    document.getElementById('exportPreviewTitle').textContent = 'نسخة احتياطية (JSON Backup)';
    document.getElementById('exportPreviewText').textContent = jsonStr;
    openModal('modalExportPreview');

    // Trigger download
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Achievement_Backup_${todayStr}.json`;
    a.click();
    URL.revokeObjectURL(url);
  });

  document.getElementById('importBackupJsonBtn').addEventListener('click', () => {
    document.getElementById('backupFileInput').click();
  });

  document.getElementById('backupFileInput').addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const imported = JSON.parse(event.target.result);
        if (imported.habits && imported.tasks && imported.goals) {
          appState = imported;
          saveState();
          showToast('تمت استعادة النسخة الاحتياطية بنجاح!');
          refreshTodayStats();
          renderTodayActionList();
          renderProgressTabs();
        } else {
          alert('الملف غير صالح كنسخة احتياطية لتطبيق Achievement');
        }
      } catch (err) {
        alert('خطأ في قراءة ملف JSON');
      }
    };
    reader.readAsText(file);
  });

  // --- Initial Launch ---
  refreshTodayStats();
  renderTodayActionList();
  updateTimerRing();
  renderTempMilestones();

})();
