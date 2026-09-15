package com.example.fitapp.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `workouts` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `notes` TEXT
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `workout_exercises` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `workoutId` INTEGER NOT NULL,
                    `exerciseId` INTEGER NOT NULL,
                    `order` INTEGER NOT NULL,
                    `sets` INTEGER NOT NULL,
                    `reps` TEXT NOT NULL,
                    `restSeconds` INTEGER NOT NULL,
                    FOREIGN KEY(`workoutId`) REFERENCES `workouts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercises_workoutId` ON `workout_exercises` (`workoutId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercises_exerciseId` ON `workout_exercises` (`exerciseId`)")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `workout_logs` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `workoutId` INTEGER NOT NULL,
                    `workoutName` TEXT NOT NULL,
                    `startedAt` INTEGER NOT NULL,
                    `finishedAt` INTEGER,
                    `durationMin` INTEGER,
                    FOREIGN KEY(`workoutId`) REFERENCES `workouts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_logs_workoutId` ON `workout_logs` (`workoutId`)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `set_logs` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `logId` INTEGER NOT NULL,
                    `exerciseId` INTEGER NOT NULL,
                    `setNumber` INTEGER NOT NULL,
                    `weight` REAL NOT NULL,
                    `reps` INTEGER NOT NULL,
                    `done` INTEGER NOT NULL,
                    FOREIGN KEY(`logId`) REFERENCES `workout_logs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_set_logs_logId` ON `set_logs` (`logId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_set_logs_exerciseId` ON `set_logs` (`exerciseId`)")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `exercise_catalog_meta` (
                    `exerciseId` INTEGER NOT NULL,
                    `isFavorite` INTEGER NOT NULL,
                    `lastUsedAt` INTEGER,
                    `quickAddCount` INTEGER NOT NULL,
                    PRIMARY KEY(`exerciseId`),
                    FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_catalog_meta_isFavorite` ON `exercise_catalog_meta` (`isFavorite`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_catalog_meta_lastUsedAt` ON `exercise_catalog_meta` (`lastUsedAt`)")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `set_logs` ADD COLUMN `exerciseOrder` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `workout_logs` ADD COLUMN `restTimerTotalSeconds` INTEGER")
            db.execSQL("ALTER TABLE `workout_logs` ADD COLUMN `restTimerEndsAt` INTEGER")
            db.execSQL(
                """
                UPDATE `set_logs`
                SET `exerciseOrder` = COALESCE(
                    (
                        SELECT `workout_exercises`.`order`
                        FROM `workout_exercises`
                        INNER JOIN `workout_logs`
                            ON `workout_logs`.`workoutId` = `workout_exercises`.`workoutId`
                        WHERE `workout_logs`.`id` = `set_logs`.`logId`
                          AND `workout_exercises`.`exerciseId` = `set_logs`.`exerciseId`
                        ORDER BY `workout_exercises`.`order`
                        LIMIT 1
                    ),
                    0
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_set_logs_logId_exerciseOrder_setNumber` " +
                    "ON `set_logs` (`logId`, `exerciseOrder`, `setNumber`)"
            )
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `workouts` ADD COLUMN `isArchived` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `workouts` ADD COLUMN `presetCode` TEXT")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_workouts_presetCode` ON `workouts` (`presetCode`)")
            db.execSQL("ALTER TABLE `exercises` ADD COLUMN `isArchived` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `set_logs` ADD COLUMN `durationSeconds` INTEGER")
            db.execSQL("ALTER TABLE `set_logs` ADD COLUMN `restSeconds` INTEGER NOT NULL DEFAULT 60")
            // Legacy sessions have no rest snapshot: only use a matching exercise,
            // never the unrelated exercise now occupying its template position.
            db.execSQL(
                """
                UPDATE `set_logs` SET `restSeconds` = COALESCE((
                    SELECT MAX(0, MIN(3600, we.restSeconds))
                    FROM workout_exercises we
                    INNER JOIN workout_logs wl ON wl.workoutId = we.workoutId
                    WHERE wl.id = set_logs.logId AND we.exerciseId = set_logs.exerciseId
                        AND we.`order` = set_logs.exerciseOrder
                    LIMIT 1
                ), (
                    SELECT MAX(0, MIN(3600, we.restSeconds))
                    FROM workout_exercises we
                    INNER JOIN workout_logs wl ON wl.workoutId = we.workoutId
                    WHERE wl.id = set_logs.logId AND we.exerciseId = set_logs.exerciseId
                    ORDER BY we.`order` LIMIT 1
                ), 60)
                """.trimIndent()
            )
            // Keep every old group, including repeated occurrences of an exercise.
            // A fixed mapping avoids reading positions changed by this same UPDATE.
            db.execSQL(
                """
                CREATE TEMP TABLE session_groups_v6 AS
                SELECT DISTINCT logId, exerciseOrder AS oldOrder, exerciseId FROM set_logs
                """.trimIndent()
            )
            db.execSQL(
                """
                UPDATE set_logs SET exerciseOrder = (
                    SELECT COUNT(*) FROM session_groups_v6 g
                    WHERE g.logId = set_logs.logId AND
                        (g.oldOrder < set_logs.exerciseOrder OR
                        (g.oldOrder = set_logs.exerciseOrder AND g.exerciseId < set_logs.exerciseId))
                )
                """.trimIndent()
            )
            db.execSQL("DROP TABLE session_groups_v6")
            db.execSQL(
                """
                UPDATE set_logs SET durationSeconds = MAX(1, reps), reps = 0, weight = 0
                WHERE exerciseId IN (SELECT id FROM exercises WHERE code IN ('plank', 'side_plank'))
                """.trimIndent()
            )
        }
    }

    val ALL = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
}
