# 📋 HANDOFF.md — Передаточный документ проекта TitanFit

---

## 🎯 1. О проекте и текущей задаче

- **Проект**: Мобильное фитнес-приложение **TitanFit** (Android, Jetpack Compose, Kotlin, Hilt, Room).
- **Рабочая директория**: `d:\Antigravity\Fitnessapp`
- **Репозиторий GitHub**: [https://github.com/alexion1ce/TitanFit.git](https://github.com/alexion1ce/TitanFit.git)
- **Текущая версия**: **0.4.39** (`versionCode = 53`, тег релиза **`v0.4.39`**).
- **Основная текущая задача**: Полный визуальный редизайн приложения под стиль **Cyber Titanium & Crimson Glow (Вариант 1)** с внедрением **отполированных стальных пластин-карточек (`FitBrushedSteelCard`)** и **металлических плашек (`FitCyanPill`, `FitDarkPill`, `FitSubPill`)**, точно соответствующих эталонному скриншоту пользователя.

---

## 🟢 2. Что уже сделано в проекте:

1. **Репозиторий и версионирование в Git**:
   - Проект полностью заничен в Git, ветка `master` отслеживает `origin/master`.
   - Релизы с `v0.4.32` по `v0.4.39` выгружены на GitHub. Стабильный чекпоинт старого дизайна сохранён в теге `v0.4.32`.

2. **Иконка приложения и Онбординг**:
   - Создана адаптивная 3D-иконка без белых полей (`ic_launcher_foreground.png` 432x432 px full-bleed).
   - Исправлен ключ первого запуска анкеты-онбординга (`onboarding_v2_completed`).

3. **Исправления навигации и сенсорных кнопок**:
   - Кнопка «Назад» на экране `ProgramDetailScreen` переведена на круглый контейнер `Box(modifier = Modifier.size(46.dp).clip(CircleShape).clickable(onClick = onBack))` с гарантированным срабатыванием.
   - Кнопка «Назад» в шапке Журнала (`JournalScreen`) настроена на прямой переход в раздел **«Прогресс»** (`Destinations.PROGRESS`).

4. **Очистка от дубликатов на экране «Мои тренировки» (`MyWorkoutsScreen.kt`)**:
   - Устранены 3-4 дублирующие кнопки «Создать тренировку». Оставлена ровно **одна главная алая карточка действий** `+ Создать свою программу` (`CreateWorkoutHeroCard`).

5. **Эталонный дизайн отполированных стальных плашек (Скриншот-эталон №1)**:
   - В [FitAppDesign.kt](file:///d:/Antigravity/Fitnessapp/app/src/main/java/com/example/fitapp/ui/components/FitAppDesign.kt) созданы:
     - `FitBrushedSteelCard`: карточка-плита с градиентом отполированной стали `FitMetalPlateGradient` (`#E5EAEE` -> `#B7BFCB` -> `#88909D` -> `#5E6573`), двухслойной стальной рамкой `FitMetalPlateBorder` и жирным белым заголовком в верхнем регистре (`HYPERPHYSIQUE STRENGTH`).
     - `FitCyanPill`: ярко-голубая овальная плашка (`#00E5FF`) группы мышц.
     - `FitDarkPill`: угольно-тёмные овалы (`#171B22`) со временем и подходами.
     - `FitSubPill`: овалы (`#282F3B`) под теги упражнений.

---

## 🟡 3. Где мы сейчас и какой следующий шаг:

- **Текущее состояние**: Релиз **0.4.39** (`versionCode = 53`) собран, протестирован unit-тестами, закоммичен и выгружен на GitHub.
- **Следующий шаг**: 
  1. Получить обратную связь от пользователя после тестирования APK **0.4.39** на телефоне.
  2. При необходимости распространить компоненты `FitBrushedSteelCard` и `FitCyanPill` на остальные экраны (`ProgramsScreen.kt`, `CatalogScreen.kt`, `ProgramDetailScreen.kt`), чтобы приложения выглядели единой стальной системой.

---

## 🚨 4. «ГРАБЛИ» — На что ни в коем случае нельзя наступать:

1. ⚠️ **Переменные окружения JDK в PowerShell**:
   Перед ВСЕМИ командами `gradlew` ОБЯЗАТЕЛЬНО задавать `JAVA_HOME`:
   ```powershell
   $env:JAVA_HOME='C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot'; $env:Path="$env:JAVA_HOME\bin;$env:Path"
   ```

2. ⚠️ **Сборка APK и кэш манифеста Gradle**:
   При каждой сборке APK **ОБЯЗАТЕЛЬНО**:
   - Повышать `versionCode` (сейчас 53) и `versionName` (сейчас 0.4.39) в `app/build.gradle.kts`.
   - Обновлять `CHANGELOG.md`.
   - Выполнять сборку строго с флагом `--rerun-tasks`:
     ```powershell
     .\gradlew.bat --no-daemon assembleDebug --rerun-tasks
     ```
   - Заливать изменения в Git и ставить тег `v0.4.X`.

3. ⚠️ **Реализм дизайна и ожидания пользователя**:
   Нельзя ограничиваться изменением пары констант цвета в `Color.kt`! Пользователь сравнивает живое приложение со скриншотами. Карточки ОБЯЗАТЕЛЬНЫ должны использовать `FitBrushedSteelCard` (стальной градиент, ярко-голубые плашки `#00E5FF`, иконку карандаша справа вверху).

4. ⚠️ **Запрет на дублирование кнопок действий**:
   На одном экране не должно быть нескольких одинаковых кнопок «Создать тренировку». Всегда оставлять 1 главный CTA-элемент.

5. ⚠️ **Контекст кнопки «Назад» в Журнале**:
   В `JournalScreen.kt` кнопка назад должна переводить в раздел **«Прогресс»** (`navController.navigate(Destinations.PROGRESS)`).

---

## 🛠️ 5. Служебные программы, окружение и скиллы (где установлены):

1. **JDK / Java Compiler**:
   - **Путь**: `C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot`
   - **Настройка в консоли**: 
     ```powershell
     $env:JAVA_HOME='C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot'; $env:Path="$env:JAVA_HOME\bin;$env:Path"
     ```

2. **Android Gradle Wrapper**:
   - **Скрипт**: `d:\Antigravity\Fitnessapp\gradlew.bat`
   - **Команда сборки APK**: `.\gradlew.bat --no-daemon assembleDebug --rerun-tasks`
   - **Выходной APK**: `d:\Antigravity\Fitnessapp\app\build\outputs\apk\debug\app-debug.apk`

3. **Система контроля версий Git & Удалённый репозиторий**:
   - **Утилита**: `git` (установлен в системе Windows).
   - **Удалённый репозиторий**: `https://github.com/alexion1ce/TitanFit.git`
   - **Ветка**: `master` (отслеживает `origin/master`).

4. **Python HTTP Сервер для локальной раздачи APK**:
   - **Интерпретатор**: Python 3.11 (`C:\Users\alex1on1ce\AppData\Roaming\uv\python\cpython-3.11-windows-x86_64-none`)
   - **Команда запуска**:
     ```powershell
     Set-Location d:\Antigravity\Fitnessapp\app\build\outputs\apk\debug
     python -m http.server 8000 --bind 0.0.0.0
     ```

5. **Системные Скиллы и Правила Агента**:
   - **Гайд по Antigravity (`antigravity-guide`)**: `C:\Users\alex1on1ce\.gemini\antigravity\builtin\skills\antigravity_guide\SKILL.md`
   - **Стандарт генерации иллюстраций упражнений**: файл правил `RULE[d:\Antigravity\Fitnessapp\AGENTS.md]`.
   - **Папка кэша / логов артефактов**: `C:\Users\alex1on1ce\.gemini\antigravity\brain\08b090c6-ec4e-4de2-99f2-3b3e211ab034`
