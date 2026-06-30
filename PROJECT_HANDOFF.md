# FitApp — Фитнес-приложение для Android

> Документ для передачи проекта. Содержит полное описание архитектуры, текущего состояния, структуры файлов и плана дальнейших действий.

---

## 📋 Краткая сводка

| Параметр | Значение |
|----------|----------|
| **Платформа** | Android (min SDK 26 / Android 8.0, target SDK 34) |
| **Стек** | Kotlin + Jetpack Compose + Material 3 |
| **База данных** | Room (версия схемы 3) |
| **DI** | Hilt |
| **Архитектура** | MVVM + Repository (Clean, упрощённая) |
| **Пакет** | `com.example.fitapp` |
| **Версия приложения** | `0.2.0` (`versionCode = 2`) |
| **Состояние** | Фазы 1–4 завершены, Фаза 5 (полировка) — не начата |

---

## ✅ Что реализовано (Фазы 1–4)

### Фаза 1 — MVP-каркас
- Настройка Gradle-проекта (version catalog `libs.versions.toml`)
- Material 3 тема (светлая + тёмная), фирменные цвета, типографика
- Room-база: сущности `MuscleGroup`, `Equipment`, `Exercise`
- Seed-данные: **32 упражнения** на русском с описаниями и ссылками на YouTube
- Экран **«Каталог упражнений»** — список с фильтрами (по мышцам, оборудованию) и поиском
- Экран **«Детали упражнения»** — описание, техника, целевые мышцы, локальная схема задействованных мышц и кнопка открытия видео во внешнем YouTube/браузере
- Навигация + нижнее меню (Navigation Compose)

### Фаза 2 — Конструктор тренировок
- Сущности `Workout`, `WorkoutExercise`, `WorkoutType`
- Экран **«Мои тренировки»** — список пользовательских тренировок + FAB создания + удаление
- Экран **«Редактор тренировки»** — название, заметки, список упражнений с полями (подходы/повторения/отдых)
- Экран **«Выбор упражнений»** (пикер) — мультивыбор с поиском и фильтрами
- Результат пикера передаётся в редактор через `SavedStateHandle`

### Фаза 3 — Готовые программы + активная тренировка
- **4 готовые программы** в seed-данных: Full Body, Push/Pull/Legs, Upper/Lower, Домашняя
- Пресеты создаются автоматически через `DatabaseInitializer`, связываясь с упражнениями по коду
- Экран **«Готовые программы»** — список с описанием и сводкой (упражнения/подходы)
- Экран **«Детали программы»** — полный список упражнений + кнопка «Начать тренировку»
- Сущности `WorkoutLog`, `SetLog` для записи выполненных тренировок
- Экран **«Активная тренировка»** — ввод веса/повторений по каждому подходу, чекбоксы, **плавающий таймер отдыха**, завершение с подтверждением

### Фаза 4 — Журнал и прогресс
- Экран **«Журнал»** — список завершённых тренировок с датой, длительностью, удалением
- Экран **«Детали тренировки»** — таблица подходов с фактическими весами/объёмом
- Экран **«Прогресс»** — сводная статистика, **график объёма по неделям** (Compose Canvas), личные рекорды
- `WorkoutLogRepository` расширен: `getLogDetail`, `getWeeklyVolume`, `getPersonalRecords`, `getOverallStats`

---

## 🗂️ Структура проекта

```
ZCodeProject/
├── settings.gradle.kts
├── build.gradle.kts                  # плагины проекта
├── CHANGELOG.md                       # история версий приложения
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml            # каталог версий всех зависимостей
│   └── wrapper/gradle-wrapper.properties
├── app/
│   ├── build.gradle.kts              # модуль app + все зависимости
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml       # INTERNET/POST_NOTIFICATIONS permissions, FitApplication, MainActivity
│       ├── res/
│       │   ├── values/               # strings, colors, themes, ic_launcher_background
│       │   ├── drawable/             # ic_launcher_foreground (векторная иконка)
│       │   └── mipmap-anydpi-v26/    # адаптивная иконка запуска
│       └── java/com/example/fitapp/
│           ├── FitApplication.kt     # @HiltAndroidApp
│           ├── MainActivity.kt       # @AndroidEntryPoint, enableEdgeToEdge
│           │
│           ├── data/
│           │   ├── local/
│           │   │   ├── AppDatabase.kt          # Room, version = 3, 7 сущностей
│           │   │   ├── DatabaseMigrations.kt   # миграции Room 1→2 и 2→3
│           │   │   ├── WorkoutWithExercises.kt # @Relation-проекция
│           │   │   ├── dao/                    # 7 DAO-интерфейсов
│           │   │   │   ├── EquipmentDao.kt
│           │   │   │   ├── ExerciseDao.kt
│           │   │   │   ├── MuscleGroupDao.kt
│           │   │   │   ├── SetLogDao.kt
│           │   │   │   ├── WorkoutDao.kt
│           │   │   │   ├── WorkoutExerciseDao.kt
│           │   │   │   └── WorkoutLogDao.kt
│           │   │   └── entity/                 # 8 сущностей
│           │   │       ├── Difficulty.kt       # enum BEGINNER/INTERMEDIATE/ADVANCED
│           │   │       ├── Equipment.kt
│           │   │       ├── Exercise.kt
│           │   │       ├── MuscleGroup.kt
│           │   │       ├── SetLog.kt           # выполненный подход
│           │   │       ├── Workout.kt          # тренировка (CUSTOM/PRESET)
│           │   │       ├── WorkoutExercise.kt  # упражнение в тренировке
│           │   │       ├── WorkoutLog.kt       # запись о сессии
│           │   │       └── WorkoutType.kt      # enum CUSTOM/PRESET
│           │   ├── repository/                 # 6 репозиториев
│           │   │   ├── DatabaseInitializer.kt  # первичное наполнение БД
│           │   │   ├── EquipmentRepository.kt
│           │   │   ├── ExerciseRepository.kt
│           │   │   ├── MuscleGroupRepository.kt
│           │   │   ├── WorkoutLogRepository.kt # логи + прогресс + рекорды
│           │   │   └── WorkoutRepository.kt    # CRUD тренировок
│           │   └── seed/
│           │       ├── DatabaseSeeder.kt       # 32 упражнения + справочники
│           │       └── WorkoutPresets.kt       # 4 готовые программы
│           │
│           ├── di/
│           │   └── DatabaseModule.kt           # Hilt-модуль: AppDatabase + DAO
│           │
│           └── ui/
│               ├── theme/                      # Color, Theme, Type (Material 3)
│               ├── components/                 # DifficultyChip
│               ├── navigation/                 # Destinations, NavGraph, MainScreen
│               ├── catalog/                    # Каталог + детали упражнения
│               │   ├── CatalogScreen.kt
│               │   ├── CatalogViewModel.kt
│               │   ├── CatalogUiState.kt
│               │   ├── ExerciseDetailScreen.kt
│               │   ├── ExerciseDetailViewModel.kt
│               │   └── ExerciseDetailUiState.kt
│               ├── builder/                    # Конструктор тренировок
│               │   ├── MyWorkoutsScreen.kt     # список
│               │   ├── WorkoutEditorScreen.kt  # редактор
│               │   └── ExercisePickerScreen.kt # пикер
│               ├── programs/                   # Готовые программы
│               │   ├── ProgramsScreen.kt       # список
│               │   └── ProgramDetailScreen.kt  # детали
│               ├── session/                    # Активная тренировка
│               │   ├── ActiveWorkoutScreen.kt
│               │   ├── RestTimerAlarmReceiver.kt
│               │   └── RestTimerNotifications.kt
│               ├── journal/                    # Журнал
│               │   ├── JournalScreen.kt        # список логов
│               │   └── LogDetailScreen.kt      # детали лога
│               └── progress/                   # Прогресс
│                   └── ProgressScreen.kt       # статистика + график
```

**Всего: 60 Kotlin-файлов.**

---

## 🧭 Навигация (нижнее меню)

```
[Каталог] [Программы] [Мои] [Журнал] [Прогресс]
```

Доступ к **«Моим тренировкам»** (конструктор) — через отдельную вкладку **«Мои»** в нижнем меню. Иконка в TopAppBar экрана «Программы» также сохранена как дополнительный быстрый переход.

Маршруты определены в `ui/navigation/Destinations.kt`:
- `catalog`, `programs`, `my_workouts`, `journal`, `progress` — вкладки нижнего меню
- `exercise/{exerciseId}` — детали упражнения
- `my_workouts`, `workout_editor/{workoutId}`, `exercise_picker` — конструктор
- `program_detail/{workoutId}` — детали готовой программы
- `active_workout/{workoutId}` — активная тренировка
- `log_detail/{logId}` — детали выполненной тренировки

---

## 🗄️ Модели данных (Room, схема v3)

| Сущность | Назначение |
|----------|-----------|
| `MuscleGroup` | Группа мышц (chest/back/legs/...) с эмодзи |
| `Equipment` | Оборудование (barbell/dumbbell/machine/...) |
| `Exercise` | Упражнение: описание, техника, видео YouTube, сложность |
| `Workout` | Тренировка/программа (type: CUSTOM или PRESET) |
| `WorkoutExercise` | Упражнение в тренировке: sets/reps/restSeconds/order |
| `WorkoutLog` | Запись о выполненной сессии: startedAt/finishedAt/durationMin |
| `SetLog` | Выполненный подход: weight/reps/done |

Foreign keys с `CASCADE`-удалением. Добавлены явные миграции Room `1→2` и `2→3`; для следующих изменений схемы нужно добавлять новые миграции.

---

## ⚠️ Известные проблемы и нюансы

1. **БД миграции**: `fallbackToDestructiveMigration()` убран, добавлены миграции `1→2` и `2→3`. При следующих изменениях схемы нужно продолжать цепочку миграций, чтобы не терять пользовательские тренировки и журнал.

2. **YouTube-плеер** (`ExerciseDetailScreen.kt`): используется библиотека `android-youtube-player:core:12.1.0`. В v12.x API:
   - Метод колбека называется `onYouTubePlayer` (НЕ `onYouTubePlayerReady`)
   - Регистрация lifecycle: `lifecycleOwner.lifecycle.addObserver(this)`

3. **`flatMapLatest`** в `CatalogViewModel` и `ExercisePickerViewModel` требует `@OptIn(ExperimentalCoroutinesApi::class)` — аннотации уже добавлены.

4. **Иконки приложений**: сейчас простая векторная иконка (гантели). Нет PNG-иконок под все плотности.

5. **Изображения упражнений**: в seed-данных поле `imageUrl = null`. Вместо фото показываются эмодзи мышц.

6. **«Мои тренировки»** доступны только через иконку в «Программах», а не из нижнего меню.

---

## 🚧 План действий — Фаза 5: Полировка (не начата)

### 5.1. Реальные изображения упражнений
- [ ] Добавить в `Exercise.imageUrl` реальные ссылки или локальные drawable
- [ ] Настроить **Coil** для загрузки (библиотека уже в зависимостях)
- [ ] Заменить эмодзи-плейсхолдеры в карточках на фото
- [ ] В деталях упражнения — крупное фото вместо видео как основной визуал

### 5.2. Иконка приложения
- [ ] Создать PNG-иконки для всех плотностей (mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi)
- [ ] Или использовать Image Asset Studio в Android Studio
- [ ] Splash screen с иконкой

### 5.3. UX-улучшения
- [ ] Drag-and-drop переупорядочивание упражнений в редакторе (метод `onMoveExercise` уже есть в ViewModel)
- [ ] Анимации переходов между экранами (Navigation Compose transitions)
- [ ] Swipe-to-delete в журнале и списке тренировок
- [x] Haptic feedback при отметке подходов
- [ ] Звук/вибрация по окончании таймера отдыха

### 5.4. Функциональные доработки
- [x] **Счётчик отдыха с фоновой работой**: таймер считает остаток от реального времени окончания и ставит системное уведомление через `AlarmManager`/`BroadcastReceiver`; для постоянного ongoing-уведомления можно позже добавить Foreground Service
- [x] **Правильные миграции Room** вместо `fallbackToDestructiveMigration()`
- [ ] **Резервное копирование** тренировок (экспорт/импорт JSON)
- [x] **Таймер тренировки** в активной сессии (общая длительность сверху)
- [ ] **Warm-up sets** — разминочные подходы отдельно от рабочих
- [ ] **RPE/RIR** — оценка усилия в подходе
- [ ] **Графики по упражнению** — прогресс конкретного упражнения во времени (сейчас только общий объём)

### 5.5. Технический долг
- [ ] **Тесты**: unit-тесты для репозиториев и ViewModel; UI-тесты для Compose
- [ ] **ProGuard правила** для релизной сборки
- [ ] **Локализация**: вынести все строки в `strings.xml` (сейчас много хардкода на русском)
- [ ] **Тёмная тема**: проверить контрастность на всех экранах
- [ ] **Обработка ошибок**: единый Snackbar-host вместо AlertDialog в некоторых местах
- [ ] ** Paging** для каталога при росте числа упражнений

### 5.6. Перед релизом
- [ ] Сменить `applicationId` с `com.example.fitapp` на реальный
- [ ] Сменить иконку и название приложения
- [ ] Подписать APK (keystore)
- [x] Увеличить `versionCode`, завести `CHANGELOG.md`
- [ ] Проверить `proguard-rules.pro`

---

## 🛠️ Как запустить проект

### Требования
- **Android Studio** (Ladybug или новее, с поддержкой Kotlin 2.0)
- **JDK 17** (указан в `compileOptions`)
- **Android SDK** API 34

### Шаги
1. Открыть папку `ZCodeProject` как проект в Android Studio
2. Дождаться Gradle Sync (скачает зависимости, ~5–10 мин при первом запуске)
3. Создать эмулятор: **Tools → Device Manager → Create Device** (Pixel 7, API 34)
4. Выбрать конфигурацию **«app»** (НЕ тестовую!) в выпадающем списке рядом с ▶️
5. Нажать ▶️ **Run 'app'**

### Сборка APK без устройства
```bash
gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔑 Ключевые архитектурные решения

1. **MVVM + Repository**: каждый экран = `ViewModel` + `UiState` (data class) + Composable. Состояние через `StateFlow`.

2. **Hilt**: `@HiltAndroidApp` на `FitApplication`, `@AndroidEntryPoint` на `MainActivity`, `@HiltViewModel` на всех ViewModel. DAO и репозитории предоставляются через `DatabaseModule`.

3. **Реактивные списки**: `Repository.observeX()` возвращает `Flow`, который через `.stateIn()` превращается в `StateFlow<UiState>`.

4. **Seed при первом запуске**: `DatabaseInitializer.initializeIfNeeded()` вызывается в `init` блоке `CatalogViewModel` (первый загружаемый экран). Идемпотентен — проверяет `count()` перед вставкой.

5. **Передача данных между экранами**: через `SavedStateHandle` Navigation Compose (например, выбранные упражнения из пикера в редактор через ключ `KEY_PICKED_IDS`).

6. **Графики без сторонних библиотек**: экран «Прогресс» рисует столбчатый график через `Canvas` Compose.

---

## 📚 Используемые библиотеки (из `libs.versions.toml`)

| Библиотека | Версия | Назначение |
|-----------|--------|-----------|
| AGP | 8.5.2 | Android Gradle Plugin |
| Kotlin | 2.0.20 | Язык |
| Compose BOM | 2024.09.03 | UI-фреймворк |
| Room | 2.6.1 | База данных |
| Hilt | 2.52 | Внедрение зависимостей |
| Navigation Compose | 2.8.1 | Навигация |
| Coil | 2.7.0 | Загрузка изображений |
| KSP | 2.0.20-1.0.25 | Обработка аннотаций (Room, Hilt) |

---

*Документ создан для передачи проекта другой модели/разработчику. Все фазы 1–4 реализованы и должны компилироваться. Фаза 5 (полировка) — следующая.*
