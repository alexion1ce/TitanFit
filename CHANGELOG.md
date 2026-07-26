# Changelog

## 0.4.39 - 2026-07-26

- Added comprehensive handoff document `HANDOFF.md` detailing project architecture, completed milestones, current progress, next steps, and critical pitfalls.

## 0.4.38 - 2026-07-26

- Implemented 1-to-1 Brushed Steel Titanium Metallic Card design (`FitBrushedSteelCard`) matching user reference Screenshot 1.
- Added vibrant cyan primary pill tags (`FitCyanPill`), dark time/day pills (`FitDarkPill`), and sub-tag pills (`FitSubPill`) with bold uppercase title.

## 0.4.37 - 2026-07-26

- Fixed JournalScreen top-left back button to navigate directly to the Progress tab (`Destinations.PROGRESS`).
- Overhauled `MyWorkoutsScreen` to Option A Cyber Dashboard layout, eliminating all duplicate create buttons and introducing metallic gradient chips (`MetallicChip`).
- Transformed bottom navigation bar (`AppBottomBar`) into Cyber Titanium metallic surface with glowing top border and radial crimson FAB.

## 0.4.36 - 2026-07-26

- Upgraded `FitSurfaceCard`, `GlassCard`, and `RecommendedPanel` across Catalog and Programs to 1-to-1 Cyber Titanium metallic gradient fills (`Brush.linearGradient`) and glowing crimson/steel borders.
- Enhanced filter chips and search inputs with vibrant active state gradients and crimson accent rings.

## 0.4.35 - 2026-07-26

- Added explicit Back button on `JournalScreen` top bar and passed `onBack` handler in `NavGraph.kt`.
- Transformed `JournalCard` into rich Cyber Titanium metallic card with radial icon glow, metallic linear gradient background, and dedicated glowing crimson detail arrow button.

## 0.4.34 - 2026-07-26

- Fixed ProgramDetailScreen back button touch detection using explicit clip & click Box container.
- Enhanced JournalScreen cards with Cyber Titanium metallic styling and explicit `ChevronRight` detail navigation arrow.
- Full visual theme upgrade across ProgramDetailScreen, JournalScreen, and LogDetailScreen to rich Cyber Titanium & Crimson Glow style.

## 0.4.33 - 2026-07-26

- Implemented new **Cyber Titanium & Crimson Glow (Variant 1)** design theme across the application.
- Updated core design tokens (`Color.kt` & `FitAppDesign.kt`) to deep titanium `#0D0F12`, metallic card surfaces `#171B21`, vibrant crimson red accents `#FF3B30`, and electric cyan highlights `#00D2FF`.

## 0.4.32 - 2026-07-24

- Expanded TitanFit 3D launcher icon asset to full-bleed edge-to-edge (432x432 px) across all launcher densities and adaptive layers, eliminating white borders and white background cutouts on Android launchers.

## 0.4.31 - 2026-07-23

- Fixed Android 8.0+ Adaptive Icons root cause: generated `ic_launcher_foreground.png` bitmap replacing old vector placeholder so launcher icons on all Android phones display TitanFit 3D emblem.

## 0.4.30 - 2026-07-23

- Officially updated app brand name to **TitanFit** in AndroidManifest strings.
- Applied chosen 3D luxury barbell weight plate emblem design across all launcher mipmap icon densities (`hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`).
- Updated promo code branding to `TITANFIT`.

## 0.4.29 - 2026-07-23

- Fixed initial launch bug where existing SharedPreferences key bypassed the new 7-step Onboarding Wizard; updated completion key to `onboarding_v2_completed` guaranteeing initial launch wizard execution.
- Generated 4 distinct commercial app launcher icon design concepts for user review.

## 0.4.28 - 2026-07-23

- Created new 3D/flat neon red & dark graphite app launcher icon across all mipmap density buckets (`hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) and adaptive icon configurations (`ic_launcher.xml` / `ic_launcher_background.xml`).
- Architected non-intrusive monetization framework (`AdManager.kt` & `AdBannerContainer.kt`) supporting Banner, Interstitial, and Rewarded Ads (AdMob / Yandex Ads ready).
- Embedded dark-themed bottom banner container smoothly docked above main navigation bar.

## 0.4.27 - 2026-07-23

- Fixed mobile layout & clipping issues across all screens.
- Updated `ExerciseArtworkThumbnail` and `ExerciseArtworkHero` to frame illustrations inside white rounded containers with `ContentScale.Fit`, ensuring 100% of human figure, bar, weights, and cyan overlays are completely visible without cropping.
- Re-architected `ProgramsScreen` header and `TodayProgramCard` layout to eliminate text truncation on 360dp–400dp mobile screens.
- Wrapped onboarding steps 2, 3, 4 in scrollable views to prevent card clipping on smaller phone displays.
- Updated `MuscleColorLegend` to responsive `FlowRow` preventing legend text overflow.

## 0.4.26 - 2026-07-22

- Updated muscle color legend circle in `ExerciseArtwork.kt` to cyan (`#00D2FF`) for stabilizing muscles.
- Re-processed all 70 exercise PNG illustrations using HSV hue transformation to completely eliminate green edge halos/fringes and transform all stabilizing muscle highlights into vibrant cyan/light blue.

## 0.4.25 - 2026-07-22

- Recolored green stabilizing muscle overlays to vibrant cyan/light blue across all exercise PNG illustrations.
- Compressed all 70 exercise artwork assets using 256-color quantization, reducing total image footprint from 96.3 MB to 11.9 MB (87.7% reduction).

## 0.4.24 - 2026-07-22

- Upgraded Onboarding Wizard to 7-step commercial flow (Muscle Focus Area selection, Preferred Duration 30-90 min).
- Added interactive Plan Generation animation screen with progress bar and checklist feedback.
- Added 1-tap Gym vs Home location switcher chip ("🏋️ Зал" / "🏋️ Гантели" / "🏠 Свой вес") in Programs header.

## 0.4.23 - 2026-07-22

- Fixed missing preset database initialization when launching the app directly into Onboarding screen on first run.

## 0.4.22 - 2026-07-22

- Refactored multi-day workout programs into standalone daily templates (Push, Pull, Legs, Upper, Lower, Full Body A/B, Home Upper/Lower).
- Added "Workout Day Selection Dialog" («Какая сегодня тренировка?») to choose specific split days for today's session.

## 0.4.21 - 2026-07-22

- Added direct numeric keyboard input alongside the slider for age, height, and weight in the onboarding wizard step 1.

## 0.4.20 - 2026-07-22

- Added 6-step Onboarding Assistant for first launch: gender, age, height, weight, goal, training location (gym / home with dumbbells / home bodyweight), experience level, and days per week.
- Real-time BMI and Mifflin-St Jeor BMR & target daily calorie intake calculations.
- Recommendation engine matching user preferences to optimal home vs gym preset programs.
- User profile persistence with option to re-open questionnaire anytime from the Programs header.
- Added new "Home with dumbbells" preset program (`home_dumbbells`).

## 0.4.19 - 2026-07-22

- Removed remaining fake metrics (artificial completion percentage formula and hardcoded days-per-week calculations).
- Replaced KPI completion percentage with real set completion rate calculated across user's finished workouts.
- Dynamic weekly volume chart supporting proper period filtering (1, 4, 12, 52 weeks) with formatted week dates.
- Added estimated 1RM (One Rep Max) calculation using Epley formula for personal records.
- Fixed deprecated Material 3 AutoMirrored icon warnings.

## 0.4.18 - 2026-07-10

- Replaced the misleading 68% indicator on the empty progress screen with a neutral analytics icon.
- Removed the static, non-data-driven program progress card and percentage rings from the Programs screen.

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
