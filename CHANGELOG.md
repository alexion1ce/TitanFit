# Changelog

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
