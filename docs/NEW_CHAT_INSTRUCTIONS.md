# Инструкция для нового чата Codex по проекту GymAPP

Дата подготовки: 2026-07-06.

Проект: `C:\Users\alex1on1ce\Documents\GymAPP`.

Эта инструкция нужна, чтобы новый чат Codex мог продолжить работу без потери контекста. Перед любыми изменениями новый чат должен заново проверить актуальное состояние файлов, потому что в проекте уже есть незакоммиченные изменения пользователя или предыдущей сессии.

## 1. Краткое описание проекта

GymAPP / FitApp - Android-приложение для тренировок.

Основные функции, найденные в проекте:

- каталог упражнений;
- детальная карточка упражнения с иллюстрацией;
- готовые программы тренировок;
- пользовательские тренировки;
- активная тренировка;
- журнал завершенных тренировок;
- экран прогресса и статистики;
- локальная база данных Room;
- локальные PNG-иллюстрации упражнений в анатомическом стиле.

Корневое имя Gradle-проекта: `FitApp`.

Пакет приложения: `com.example.fitapp`.

Название приложения в ресурсах: `FitApp`.

README в корне проекта не найден.

## 2. Текущая версия проекта

Версия найдена в `app/build.gradle.kts`:

- `versionName = "0.4.5"`
- `versionCode = 19`

Дополнительно:

- в `CHANGELOG.md` последняя описанная версия: `0.4.5 - 2026-07-01`;
- последний Git-тег, найденный командой сортировки тегов: `v0.4.2`;
- последний коммит на `main` на момент проверки: `500cd39 Update exercise catalog artwork`;
- `PROJECT_HANDOFF.md` содержит устаревшую версию `0.2.0` и местами поврежденную русскую кодировку, поэтому его нельзя считать главным источником версии.

## 3. Используемые технологии и инструменты

Проект Android на Kotlin.

Найденные технологии:

- Android Gradle Plugin `8.5.2`;
- Gradle wrapper `8.14.5`;
- Kotlin `2.0.20`;
- KSP `2.0.20-1.0.25`;
- Jetpack Compose;
- Compose BOM `2024.09.03`;
- Material 3;
- Navigation Compose `2.8.1`;
- Room `2.6.1`;
- Hilt `2.52`;
- Coil Compose `2.7.0`;
- Java/JVM target `17`;
- Android `compileSdk = 34`, `minSdk = 26`, `targetSdk = 34`.

Архитектурно проект выглядит как MVVM + Repository:

- `ViewModel` + `UiState` для экранов;
- `Repository` для доступа к данным;
- Room DAO/entity для локальной базы;
- Hilt для внедрения зависимостей;
- Navigation Compose для маршрутов.

## 4. Важные папки и файлы

Корень проекта:

- `AGENTS.md` - главные проектные правила, особенно стандарт генерации PNG-ассетов упражнений.
- `build.gradle.kts` - плагины проекта.
- `settings.gradle.kts` - имя проекта `FitApp`, модуль `:app`, репозитории.
- `gradle/libs.versions.toml` - версии библиотек и Gradle-плагинов.
- `gradle/wrapper/gradle-wrapper.properties` - Gradle wrapper `8.14.5`.
- `app/build.gradle.kts` - Android-конфигурация, зависимости, `versionName`, `versionCode`.
- `CHANGELOG.md` - история версий до `0.4.5`.
- `PROJECT_HANDOFF.md` - старый handoff-документ; использовать осторожно из-за устаревшей версии и поврежденной кодировки.
- `.gitignore` - исключает `.gradle`, `.kotlin`, `build`, `app/build`, `local.properties`, `.idea`, `.codex-remote-attachments`, `rejected-assets`, `tmp_*contact_sheet*.jpg`.
- `rejected-assets/` - локальные отклоненные ассеты, исключены из Git.

Код приложения:

- `app/src/main/AndroidManifest.xml` - разрешения `INTERNET`, `POST_NOTIFICATIONS`, `FitApplication`, `MainActivity`, `RestTimerAlarmReceiver`.
- `app/src/main/java/com/example/fitapp/FitApplication.kt` - Hilt application.
- `app/src/main/java/com/example/fitapp/MainActivity.kt` - главный activity-файл.
- `app/src/main/java/com/example/fitapp/data/local/AppDatabase.kt` - Room database, schema version `3`.
- `app/src/main/java/com/example/fitapp/data/local/DatabaseMigrations.kt` - миграции Room.
- `app/src/main/java/com/example/fitapp/data/local/dao/` - DAO.
- `app/src/main/java/com/example/fitapp/data/local/entity/` - сущности базы.
- `app/src/main/java/com/example/fitapp/data/repository/` - репозитории.
- `app/src/main/java/com/example/fitapp/data/seed/DatabaseSeeder.kt` - seed-данные упражнений; найдено 69 `Exercise(...)`.
- `app/src/main/java/com/example/fitapp/data/seed/WorkoutPresets.kt` - готовые программы.
- `app/src/main/java/com/example/fitapp/di/DatabaseModule.kt` - Hilt-модуль базы.
- `app/src/main/java/com/example/fitapp/ui/catalog/` - каталог и детали упражнения.
- `app/src/main/java/com/example/fitapp/ui/components/ExerciseArtwork.kt` - показ иллюстраций и маппинг `exerciseCode -> drawable`.
- `app/src/main/java/com/example/fitapp/ui/builder/` - пользовательские тренировки.
- `app/src/main/java/com/example/fitapp/ui/programs/` - готовые программы.
- `app/src/main/java/com/example/fitapp/ui/session/` - активная тренировка и таймер отдыха.
- `app/src/main/java/com/example/fitapp/ui/journal/` - журнал тренировок.
- `app/src/main/java/com/example/fitapp/ui/progress/` - экран прогресса.
- `app/src/main/java/com/example/fitapp/ui/navigation/` - навигация и нижнее меню.
- `app/src/main/java/com/example/fitapp/ui/theme/` - тема Compose.

Ресурсы:

- `app/src/main/res/drawable-nodpi/` - PNG-ассеты упражнений; найдено 70 PNG.
- `app/src/main/res/values/strings.xml` - `app_name = FitApp`.
- `app/src/main/res/drawable/` и `mipmap-anydpi-v26/` - иконка приложения.

## 5. Правила проекта из AGENTS.md

Главное правило: не менять лишнее и сначала изучать существующий проект.

Особые правила для exercise PNG assets:

- все готовые PNG сохраняются в `app/src/main/res/drawable-nodpi`;
- максимум 3 artwork assets за один turn;
- один вызов imagegen на один asset;
- не собирать APK, не запускать Gradle, не менять код приложения и не менять Git без отдельной просьбы пользователя;
- использовать стандартный prompt-шаблон из `AGENTS.md`;
- фон должен быть чисто белым, без комнаты, пола и декора;
- человек должен быть реалистичным, иллюстрированным, с нормальными волосами и естественным лицом;
- нельзя делать серо-белого манекена, лысого персонажа, мультяшный стиль, ярко-зеленую или ярко-синюю одежду как доминанту;
- для верхней части тела предпочтителен голый торс или минимальная нейтральная одежда, чтобы были видны мышечные подсветки;
- обычно нужно показывать две фазы движения: старт и финиш;
- техника упражнения, траектория, хват, стойка, угол скамьи, тросы, рукояти, тренажер и веса должны быть анатомически и механически правдоподобными;
- для тренажеров и блоков важно корректно показывать трос, блоки, рукояти, весовой стек и selector pin;
- если selector pin виден, он должен оставаться на одной выбранной пластине во всех фазах;
- в рабочей фазе поднимается только выбранный верхний блок стека, нижние плиты остаются внизу;
- не должно быть текста, чисел, стрелок, подписей, водяных знаков, логотипов и брендов;
- основные мышцы подсвечиваются красным, вспомогательные оранжевым, стабилизаторы зеленым;
- practical benchmark: `exercise_rope_pushdown.png`;
- если композиция и механика хороши, при доработке править только проблемную область, а не регенерировать сцену целиком.

Стандартный шаблон промпта из `AGENTS.md` нужно использовать как основу и добавлять в конец:

```text
Exercise-specific request:
{exercise_specific_description}
```

## 6. Что уже сделано

По `CHANGELOG.md`:

- `0.1.0` - MVP: каталог, конструктор тренировок, готовые программы, активная тренировка, журнал, прогресс.
- `0.2.0` - миграции Room, уведомления таймера отдыха, таймер тренировки, haptic feedback, вкладка пользовательских тренировок, внешняя ссылка на YouTube, offline muscle maps, Gradle wrapper.
- `0.3.0` - анатомические иллюстрации, полноэкранный preview, цветовая легенда.
- `0.3.1` - красно-оранжево-зеленые activation images, маппинг для seed-упражнений.
- `0.3.2` - кнопка старта программы и кликабельные упражнения в деталях.
- `0.3.3` - исправлен plank artwork, добавлены lying leg raises, начат аудит artwork по механике.
- `0.3.4` - исправления нижней навигации, добавлены pull-ups, lunges, floor push-ups artwork.
- `0.3.5` - исправлена метка вкладки программ.
- `0.3.6` - аудит artwork mappings, добавлены отдельные изображения для многих упражнений.
- `0.3.7` - улучшен экран активной тренировки.
- `0.3.8` - исправлен возврат выбранных упражнений из picker.
- `0.3.9` - кнопка старта пользовательской тренировки, действия Start/Edit/Delete, нормализован текст экрана.
- `0.3.10` - исправлен cursor jumping в числовых полях.
- `0.4.0` - каталог расширен с 30 до 70 упражнений.
- `0.4.1` - добавлены явные artwork mappings для 70 упражнений.
- `0.4.2` - удален good morning.
- `0.4.3` - переименование leg curl и cleanup на update.
- `0.4.4` - заменены оставшиеся старые черно-белые artwork, обновлены fallback category artwork.
- `0.4.5` - исправлены иллюстрации: machine chest press, machine row, reverse pec deck, straight-arm pulldown, dumbbell fly, triceps pushdown, T-bar row.

По текущим файлам:

- в `drawable-nodpi` найдено 70 PNG-файлов;
- в `DatabaseSeeder.kt` найдено 69 seed-упражнений;
- `ExerciseArtwork.kt` содержит явный маппинг множества `exerciseCode` на PNG;
- Room database version сейчас `3`.

## 7. Что сейчас в работе

Git status на момент анализа:

```text
## main
 M app/src/main/java/com/example/fitapp/MainActivity.kt
 M app/src/main/java/com/example/fitapp/data/repository/WorkoutLogRepository.kt
 M app/src/main/java/com/example/fitapp/data/repository/WorkoutRepository.kt
 M app/src/main/java/com/example/fitapp/ui/navigation/MainScreen.kt
 M app/src/main/java/com/example/fitapp/ui/programs/ProgramsScreen.kt
 M app/src/main/java/com/example/fitapp/ui/programs/ProgramsUiState.kt
 M app/src/main/java/com/example/fitapp/ui/programs/ProgramsViewModel.kt
 M app/src/main/java/com/example/fitapp/ui/progress/ProgressScreen.kt
 M app/src/main/java/com/example/fitapp/ui/progress/ProgressUiState.kt
 M app/src/main/java/com/example/fitapp/ui/progress/ProgressViewModel.kt
?? .serena/
```

Важно: эти изменения уже были в рабочем дереве до создания этой инструкции. Новый чат не должен откатывать или перезаписывать их без отдельной просьбы пользователя.

По `git diff --stat` локальные изменения крупные:

- 10 Kotlin-файлов изменены;
- примерно `1097 insertions`, `298 deletions`;
- есть предупреждения Git о возможной замене LF на CRLF при следующем касании файлов.

Что видно по diff:

- `WorkoutRepository.kt`: в `WorkoutExerciseItem` добавлены `exerciseCode`, `primaryMuscleCode`, `secondaryMuscleCode`.
- `WorkoutLogRepository.kt`: добавлен `RecentWorkoutSummary` и метод `getRecentWorkoutSummaries(limit: Int = 3)`.
- `ProgramsUiState.kt`: `ProgramCard` теперь хранит список `exercises`.
- `ProgramsViewModel.kt`: карточки программ получают `exercises = detail?.exercises.orEmpty()`.
- `ProgressUiState.kt`: добавлен `recentWorkouts`.
- `ProgressViewModel.kt`: загружаются `recentWorkouts = workoutLogRepository.getRecentWorkoutSummaries()`.
- `ProgramsScreen.kt`: крупная переработка визуального экрана программ, включая today's card, progress card, exercise strip, compact program cards.
- `ProgressScreen.kt`: крупная переработка экрана прогресса, включая аналитику объема, график, последние тренировки, рекорды, KPI-карточки.
- `MainScreen.kt`: переработка нижней навигации; по текущему файлу bottom items: `Каталог`, `Программы`, `Прогресс`, `Мои`.
- `MainActivity.kt`: изменен, но в этой подготовке не проверялось поведение.

Папка `.serena/` не отслеживается Git. Она выглядит как локальный кеш/память инструмента и не должна попадать в коммит без явной причины.

## 8. Что нужно реализовать дальше

Не найден единый актуальный task list. Из `CHANGELOG.md`, `PROJECT_HANDOFF.md`, текущего diff и структуры проекта можно выделить следующие направления, но перед работой нужно уточнять у пользователя приоритет:

1. Довести текущие незакоммиченные UI-изменения до проверенного состояния:
   - программы;
   - прогресс;
   - нижняя навигация;
   - связанные ViewModel/Repository изменения.

2. Проверить, что текущие UI-изменения компилируются и не ломают навигацию.

3. Сверить каталог упражнений:
   - в seed найдено 69 упражнений;
   - в ресурсах 70 PNG;
   - в changelog указано расширение до 70 упражнений;
   - нужно понять, это ожидаемое состояние или один seed-элемент удален/не добавлен.

4. Продолжать аудит exercise artwork mappings:
   - `ExerciseArtwork.kt`;
   - соответствие кодов seed-упражнений PNG-файлам;
   - корректность анатомии и механики PNG.

5. При генерации новых exercise PNG строго соблюдать AGENTS.md:
   - не больше 3 ассетов за turn;
   - сохранять в `app/src/main/res/drawable-nodpi`;
   - не запускать Gradle и не менять код без отдельной просьбы.

6. Технический долг из старого `PROJECT_HANDOFF.md` использовать осторожно:
   - тесты;
   - ProGuard rules;
   - локализация строк;
   - темная тема и контрастность;
   - единая обработка ошибок;
   - экспорт/импорт тренировок;
   - RPE/RIR;
   - warm-up sets;
   - графики по отдельным упражнениям.

## 9. Полный пошаговый план работ

Если новый чат получает задачу продолжить разработку, порядок работы должен быть таким:

1. Открыть проект `C:\Users\alex1on1ce\Documents\GymAPP`.
2. Прочитать `AGENTS.md`.
3. Прочитать эту инструкцию: `docs/NEW_CHAT_INSTRUCTIONS.md`.
4. Проверить актуальный `git status --short --branch`.
5. Если есть незакоммиченные изменения, считать их чужими/текущими и не откатывать.
6. Проверить `app/build.gradle.kts` на актуальный `versionName` и `versionCode`.
7. Проверить `CHANGELOG.md`, если задача связана с релизом или историей изменений.
8. Для code task:
   - найти релевантные файлы через `rg`;
   - читать существующий код перед правками;
   - менять минимальный набор файлов;
   - следовать стилю Compose/MVVM/Repository, уже принятому в проекте;
   - не создавать новую архитектуру без необходимости.
9. Для UI task:
   - учитывать текущие правки в `MainScreen.kt`, `ProgramsScreen.kt`, `ProgressScreen.kt`;
   - проверять, что текст не обрезается и не налезает;
   - использовать Material/Compose паттерны проекта.
10. Для database task:
   - проверить `AppDatabase.kt`;
   - если меняется схема, добавить миграцию в `DatabaseMigrations.kt`;
   - не использовать destructive migration без явной просьбы.
11. Для exercise artwork task:
   - использовать prompt standard из `AGENTS.md`;
   - максимум 3 PNG за turn;
   - сохранять только в `app/src/main/res/drawable-nodpi`;
   - не запускать Gradle, не собирать APK, не менять Kotlin-код без отдельной просьбы.
12. После изменений выполнить проверку, подходящую задаче.
13. В итоговом ответе указать:
   - что сделано;
   - какие файлы затронуты;
   - какие проверки выполнены;
   - что не проверено и почему.

## 10. Как проверять изменения

Для обычных Kotlin/Android-изменений:

- базовая проверка компиляции: `.\gradlew.bat assembleDebug`;
- если нужны только тесты и они появятся: `.\gradlew.bat test`;
- если нужно проверить Android instrumented tests: `.\gradlew.bat connectedAndroidTest` только при доступном устройстве/эмуляторе;
- перед коммитом смотреть `git status --short` и `git diff --stat`;
- для точечной проверки кода использовать `rg` и чтение конкретных файлов.

На момент анализа тестовые папки `test` / `androidTest` в `app/src` не найдены.

Для exercise PNG:

- визуально проверить PNG;
- сверить имя файла с `ExerciseArtwork.kt`;
- сверить соответствие упражнению в `DatabaseSeeder.kt`;
- не запускать Gradle и не собирать APK, если пользователь просит только ассеты.

В этой подготовке Gradle/сборка не запускались, потому что пользователь запретил менять/собирать проект без отдельного разрешения.

## 11. Какие команды можно запускать

Без отдельного разрешения обычно можно запускать только безопасные read-only команды:

```powershell
git status --short --branch
git log -5 --oneline --decorate
git diff --stat
git diff -- <path>
Get-ChildItem -Force
Get-ChildItem -Recurse -File <path>
Get-Content -Raw <path>
Select-String -Path <path> -Pattern <pattern>
rg "<pattern>"
rg --files
```

Если пользователь явно попросил проверить сборку или кодовую задачу требует проверки, можно запускать:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat test
```

Для Android Studio/эмулятора команды и действия запускать только если это нужно задаче и пользователь не запрещал.

## 12. Какие команды не запускать без отдельной просьбы пользователя

Не запускать без отдельной просьбы:

```powershell
.\gradlew.bat assembleRelease
.\gradlew.bat bundleRelease
.\gradlew.bat connectedAndroidTest
git add
git commit
git push
git reset --hard
git checkout -- <file>
git clean -fd
Remove-Item -Recurse
```

Также без отдельной просьбы не нужно:

- собирать APK;
- менять Git-состояние;
- создавать коммиты;
- пушить ветки;
- открывать PR;
- удалять файлы;
- чистить build-кеши;
- менять Kotlin-код при задаче только на генерацию PNG;
- менять Gradle-конфигурацию при задаче только на ассеты.

## 13. Риски, ограничения и важные замечания

- Рабочее дерево не чистое. Есть локальные изменения в 10 Kotlin-файлах и новая `.serena/`.
- Не откатывать локальные изменения без прямой просьбы пользователя.
- `PROJECT_HANDOFF.md` устарел: там указана версия `0.2.0`, а актуальная версия в Gradle - `0.4.5`.
- В `PROJECT_HANDOFF.md` повреждена русская кодировка, поэтому использовать его только как вторичный источник.
- `CHANGELOG.md` тоже местами показывает поврежденную кодировку в старых русских строках, но структура версий понятна.
- Найдено 69 seed-упражнений и 70 PNG-ассетов. Нужно отдельно проверить, ожидаемо ли это после удаления good morning.
- Последний Git-тег `v0.4.2`, но версия приложения уже `0.4.5`; теги отстают.
- В проекте нет найденных тестовых папок `test`/`androidTest`.
- Room schema version `3`; при изменении сущностей нужна миграция.
- `applicationId` все еще `com.example.fitapp`, то есть выглядит как dev/package placeholder.
- В UI есть много hardcoded Russian strings; полная локализация не завершена.
- При работе с изображениями особенно важно соблюдать `AGENTS.md`, потому что проект уже имеет утвержденный визуальный стандарт.

## 14. Готовый стартовый промпт для нового чата

Скопируй и вставь в новый чат:

```text
Ты работаешь с проектом C:\Users\alex1on1ce\Documents\GymAPP.

Сначала обязательно прочитай:
- AGENTS.md
- docs/NEW_CHAT_INSTRUCTIONS.md
- app/build.gradle.kts
- CHANGELOG.md

Затем проверь:
- git status --short --branch
- последние локальные изменения через git diff --stat

Важно:
- в проекте уже есть незакоммиченные изменения в Kotlin-файлах и новая папка .serena;
- не откатывай, не удаляй и не перезаписывай чужие изменения без прямой просьбы;
- не запускай Gradle, не собирай APK и не меняй Git-состояние без отдельной просьбы пользователя;
- если задача касается exercise PNG assets, сохраняй готовые файлы только в app/src/main/res/drawable-nodpi, генерируй максимум 3 ассета за один turn, один imagegen call на asset, используй стандарт промпта из AGENTS.md и не меняй код приложения без отдельной просьбы.

Актуальная версия приложения по app/build.gradle.kts:
- versionName 0.4.5
- versionCode 19

Текущий стек:
- Android Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- Hilt
- Coil
- Gradle Kotlin DSL
- Java 17

Если продолжаешь текущую разработку, сначала разберись с локальными изменениями в:
- MainActivity.kt
- WorkoutLogRepository.kt
- WorkoutRepository.kt
- MainScreen.kt
- ProgramsScreen.kt
- ProgramsUiState.kt
- ProgramsViewModel.kt
- ProgressScreen.kt
- ProgressUiState.kt
- ProgressViewModel.kt

После любых изменений кратко сообщи:
- что сделано;
- какие файлы изменены;
- какие проверки выполнены;
- что осталось непроверенным.
```
