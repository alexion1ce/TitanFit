# Changelog

## 0.4.17 - 2026-07-10

- Restored the original PNG exercise artwork after startup crashes on a physical device with the WebP asset bundle.

## 0.4.16 - 2026-07-10

- Added a safe exit flow for active workouts: continue later, save completed sets and exit, or cancel without saving.
- Resume an unfinished workout instead of creating duplicate logs; rest timer alarms are cancelled when leaving a session.
- Stopped auto-marking uncompleted sets as done when finishing a workout, keeping progress statistics accurate.
- Reworked weekly progress calculations around Monday-starting calendar weeks and added unit tests for week boundaries and daylight-saving time.
- Optimized 70 exercise illustrations from PNG to WebP, reducing bundled artwork from about 99 MB to about 3.1 MB.

## 0.4.15 - 2026-07-07

- Added the ability to add extra sets during an active workout for a specific exercise.
- Updated "My workouts" cards to use exercise artwork and muscle summaries based on the selected workout exercises.
- Added project workflow rules for using sub-agents only when useful and for committing every APK release.

## 0.4.14 - 2026-07-07

- Changed catalog quick-add so it asks whether to create a new workout or add the exercise to an existing workout.
- Added an existing-workout picker dialog from catalog exercise cards.
- Ensured quick-added exercises are appended after the workout editor finishes loading existing workout data.

## 0.4.13 - 2026-07-07

- Reworked the exercise catalog around recommendations, favorites, recent exercises, and quick-add actions.
- Added catalog filtering by difficulty and improved search with common synonyms for muscles, equipment, and movement names.
- Added persistent catalog metadata with a Room migration so favorites and recent exercises survive app restarts.
- Added quick creation of a new workout directly from an exercise card.

## 0.4.12 - 2026-07-06

- Reworked the "My workouts" screen with aligned premium workout cards.
- Added a summary strip and full-width create workout action.
- Improved Russian labels and action layout for edit, delete, and start workout controls.

## 0.4.11 - 2026-07-06

- Fixed selected exercises disappearing when returning to the new workout editor.
- Opened "My workouts" with fresh data after saving a custom workout.
- Refreshed progress data when opening the progress tab.
- Counted all sets as completed on finish when no individual sets were marked done.

## 0.4.10 - 2026-07-06

- Raised the custom bottom navigation above Android system buttons.
- Restored working progress actions: period filters, "show all", workout details, and progress reset.
- Fixed exercise selection returning to a new workout without selected exercises.

## 0.4.9 - 2026-07-06

- Fixed custom workout creation flow after exercise selection and save.
- Restored a stable "My workouts" destination in bottom navigation.
- Made custom workout saving transactional and improved journal loading state.

## 0.4.8 - 2026-07-06

- Unified the app around one dark fitness design across shared theme, navigation, catalog, progress, chips, and Material-based screens.
- Removed the light/dark theme switch so screens no longer jump between different visual systems.
- Fixed workout creation flow so selected exercises are consumed once, saved workouts return to "My workouts", and the journal shows a loading state before empty results.

## 0.4.7 - 2026-07-06

- Updated program details to the new dark program design and fixed the program start action.

## 0.4.6 - 2026-07-06

- Updated the app visual style with light/dark theme switching and saved theme choice.
- Reworked bottom navigation plus catalog, programs, and progress screens for a cleaner mobile UI.
- Expanded workout analytics with recent session summaries and additional progress data.

## 0.4.5 - 2026-07-01

- Corrected several exercise illustrations after checking movement mechanics: machine chest press, machine row, reverse pec deck, straight-arm pulldown, dumbbell fly, triceps pushdown, and T-bar row.

## 0.4.4 - 2026-07-01

- Replaced the remaining old black-and-white exercise artwork with the new color anatomical style.
- Updated fallback category artwork so old grayscale assets no longer appear in the app.

## 0.4.3 - 2026-07-01

- Renamed "Сгибания ног (бицепс бедра)" to "Сгибания ног в тренажёре".
- Added catalog name cleanup on app update so the rename appears on existing installs.

## 0.4.2 - 2026-07-01

- Removed "Наклоны со штангой good morning" from the exercise catalog.
- Added cleanup for the removed exercise on app update so it disappears from existing installs.

## 0.4.1 - 2026-07-01

- Added explicit artwork mappings for all 70 catalog exercises.
- Mapped newly added gym exercises to the closest available movement illustrations instead of broad muscle-group fallback images.

## 0.4.0 - 2026-07-01

- Expanded the exercise catalog from 30 to 70 exercises for broader gym testing.
- Added more chest, back, leg, shoulder, biceps, triceps, abs, and glute movements.
- Added missing exercise seeding on app update so new catalog items appear without clearing user data.

## 0.3.10 - 2026-06-30

- Fixed cursor jumping in numeric input fields while typing workout values.
- Improved weight, reps, sets, and rest fields so typed text is not reformatted on every key press.

## 0.3.9 - 2026-06-30

- Added a visible start button for custom workouts in "My workouts".
- Split custom workout card actions into Start, Edit, and Delete so saved templates are easier to use.
- Rewrote the "My workouts" screen text with normal Russian encoding.

## 0.3.8 - 2026-06-30

- Fixed returning selected exercises from the exercise picker back to the custom workout editor.
- Made the picker confirm button show how many exercises will be added.
- Rewrote the exercise picker screen text with normal Russian encoding.

## 0.3.7 - 2026-06-30

- Improved the active workout screen for phone use.
- Added a workout progress summary with completed sets, elapsed time, and completed volume.
- Reworked set rows into larger touch-friendly cards with quick plus/minus controls for weight and reps.
- Replaced the small checkbox flow with a larger "Done" action per set.

## 0.3.6 - 2026-06-30

- Audited exercise artwork mappings across the catalog and split broad shared images into exercise-specific artwork.
- Added separate artwork for deadlift, incline dumbbell press, cable fly, barbell row, leg press, leg extension, leg curl, Romanian deadlift, calf raise, lateral raise, face pull, barbell curl, hammer curl, triceps pushdown, skullcrusher, parallel-bar dips, barbell hip thrust, and kettlebell swing.
- Fixed mismatches where deadlift, triceps pushdown, incline dumbbell press, pull-ups, lunges, push-ups, and other catalog items could show unrelated movement images.

## 0.3.5 - 2026-06-30

- Restored the bottom navigation label "Программы".
- Tightened bottom navigation item labels so five tabs fit on one row more cleanly.

## 0.3.4 - 2026-06-30

- Fixed the bottom navigation label for programs so it no longer wraps awkwardly.
- Fixed bottom navigation padding so the "My workouts" create button is visible.
- Added separate artwork for pull-ups, lunges, and floor push-ups.
- Updated artwork mapping for pull-ups, lunges, and push-ups so they no longer show lat pulldown, squat, or bench press illustrations.

## 0.3.3 - 2026-06-30

- Fixed incorrect plank artwork: plank now shows a forearm plank, not floor crunches.
- Added separate artwork for lying leg raises.
- Started auditing exercise artwork against exercise movement patterns instead of broad muscle-group fallback only.

## 0.3.2 - 2026-06-30

- Fixed the "Start workout" button placement on program details so it stays above the Android navigation bar.
- Made exercise rows in program details clickable and opened exercise details from them.

## 0.3.1 - 2026-06-30

- Replaced one-color exercise illustrations with red/orange/green activation images for major muscle groups.
- Added missing artwork for triceps, abs, and glutes so old schematic fallback images no longer appear for seeded exercises.
- Fixed full-screen image preview so exercise artwork opens large instead of inside a small dialog card.
- Improved artwork mapping for all seeded exercise codes.

## 0.3.0 - 2026-06-30

- Added high-resolution anatomical exercise illustrations for key muscle groups.
- Added clickable exercise images with large preview in exercise details.
- Added color legend: red for primary muscle, orange for assisting muscles, green for stabilizers.
- Started app versioning with `versionCode = 3` and `versionName = 0.3.0`.

## 0.2.0 - 2026-06-30

- Added Room migrations for schema versions 1 -> 2 and 2 -> 3 instead of destructive migration.
- Added rest timer notifications when a rest period finishes.
- Added a workout duration timer on the active workout screen.
- Added haptic feedback when marking sets as done.
- Added the "My" tab for custom workouts in the bottom navigation.
- Replaced the embedded YouTube player with an external video link to avoid playback error 152-4.
- Added offline muscle maps for exercise cards and exercise details.
- Restored the Gradle wrapper so the project can be built with `gradlew.bat`.

## 0.1.0 - 2026-06-28

- Initial working MVP: exercise catalog, workout builder, preset programs, active workout, journal, and progress screen.
