package com.example.fitapp.data.backup

import com.example.fitapp.data.local.entity.UserProfile
import org.json.JSONArray
import org.json.JSONObject

/** Backup file format, version 1. Unknown fields are ignored on read. */
internal object BackupJson {

    fun encode(data: BackupData): String = JSONObject().apply {
        put("format", BackupData.FORMAT)
        put("version", data.version)
        put("exportedAt", data.exportedAt)
        put("appVersion", data.appVersion)
        data.profile?.let { put("profile", encodeProfile(it)) }
        put("favorites", JSONArray(data.favorites.map { f ->
            JSONObject().apply {
                put("exercise", f.exerciseCode)
                put("isFavorite", f.isFavorite)
                putOpt("lastUsedAt", f.lastUsedAt)
                put("quickAddCount", f.quickAddCount)
            }
        }))
        put("workouts", JSONArray(data.workouts.map { w ->
            JSONObject().apply {
                put("key", w.key)
                put("name", w.name)
                putOpt("notes", w.notes)
                put("isArchived", w.isArchived)
                put("exercises", JSONArray(w.exercises.map { e ->
                    JSONObject().apply {
                        put("exercise", e.exerciseCode)
                        put("order", e.order)
                        put("sets", e.sets)
                        put("reps", e.reps)
                        put("restSeconds", e.restSeconds)
                    }
                }))
            }
        }))
        put("logs", JSONArray(data.logs.map { l ->
            JSONObject().apply {
                putOpt("workoutKey", l.workoutKey)
                putOpt("presetCode", l.presetCode)
                put("workoutName", l.workoutName)
                put("startedAt", l.startedAt)
                putOpt("finishedAt", l.finishedAt)
                putOpt("durationMin", l.durationMin)
                put("sets", JSONArray(l.sets.map { s ->
                    JSONObject().apply {
                        put("exercise", s.exerciseCode)
                        put("exerciseOrder", s.exerciseOrder)
                        put("setNumber", s.setNumber)
                        put("weight", s.weight)
                        put("reps", s.reps)
                        put("done", s.done)
                        putOpt("durationSeconds", s.durationSeconds)
                        put("restSeconds", s.restSeconds)
                    }
                }))
            }
        }))
    }.toString(2)

    /** @throws IllegalArgumentException when the text is not a supported TitanFit backup. */
    fun decode(text: String): BackupData {
        val root = try {
            JSONObject(text)
        } catch (e: org.json.JSONException) {
            throw IllegalArgumentException("Файл не является резервной копией TitanFit", e)
        }
        require(root.optString("format") == BackupData.FORMAT) { "Файл не является резервной копией TitanFit" }
        val version = root.optInt("version", -1)
        require(version in 1..BackupData.CURRENT_VERSION) { "Эта версия резервной копии не поддерживается" }
        return BackupData(
            version = version,
            exportedAt = root.optLong("exportedAt"),
            appVersion = root.optString("appVersion"),
            profile = root.optJSONObject("profile")?.let(::decodeProfile),
            favorites = root.arrayOrEmpty("favorites").objects().map { f ->
                BackupFavorite(
                    exerciseCode = f.getString("exercise"),
                    isFavorite = f.optBoolean("isFavorite"),
                    lastUsedAt = f.longOrNull("lastUsedAt"),
                    quickAddCount = f.optInt("quickAddCount")
                )
            },
            workouts = root.arrayOrEmpty("workouts").objects().map { w ->
                BackupWorkout(
                    key = w.getLong("key"),
                    name = w.getString("name"),
                    notes = w.stringOrNull("notes"),
                    isArchived = w.optBoolean("isArchived"),
                    exercises = w.arrayOrEmpty("exercises").objects().map { e ->
                        BackupWorkoutExercise(
                            exerciseCode = e.getString("exercise"),
                            order = e.getInt("order"),
                            sets = e.getInt("sets"),
                            reps = e.getString("reps"),
                            restSeconds = e.getInt("restSeconds")
                        )
                    }
                )
            },
            logs = root.arrayOrEmpty("logs").objects().map { l ->
                BackupLog(
                    workoutKey = l.longOrNull("workoutKey"),
                    presetCode = l.stringOrNull("presetCode"),
                    workoutName = l.getString("workoutName"),
                    startedAt = l.getLong("startedAt"),
                    finishedAt = l.longOrNull("finishedAt"),
                    durationMin = l.longOrNull("durationMin")?.toInt(),
                    sets = l.arrayOrEmpty("sets").objects().map { s ->
                        BackupSet(
                            exerciseCode = s.getString("exercise"),
                            exerciseOrder = s.getInt("exerciseOrder"),
                            setNumber = s.getInt("setNumber"),
                            weight = s.getDouble("weight"),
                            reps = s.getInt("reps"),
                            done = s.getBoolean("done"),
                            durationSeconds = s.longOrNull("durationSeconds")?.toInt(),
                            restSeconds = s.optInt("restSeconds", 60)
                        )
                    }
                )
            }
        )
    }

    private fun encodeProfile(p: UserProfile) = JSONObject().apply {
        put("gender", p.gender.name)
        put("age", p.age)
        put("heightCm", p.heightCm)
        put("weightKg", p.weightKg)
        put("goal", p.goal.name)
        put("location", p.location.name)
        put("experience", p.experience.name)
        put("daysPerWeek", p.daysPerWeek)
        put("focus", p.focus.name)
        put("preferredDuration", p.preferredDuration.name)
        put("onboardingCompleted", p.onboardingCompleted)
    }

    private fun decodeProfile(o: JSONObject): UserProfile {
        val d = UserProfile()
        return UserProfile(
            gender = enumOr(o.optString("gender"), d.gender),
            age = o.optInt("age", d.age),
            heightCm = o.optDouble("heightCm", d.heightCm),
            weightKg = o.optDouble("weightKg", d.weightKg),
            goal = enumOr(o.optString("goal"), d.goal),
            location = enumOr(o.optString("location"), d.location),
            experience = enumOr(o.optString("experience"), d.experience),
            daysPerWeek = o.optInt("daysPerWeek", d.daysPerWeek),
            focus = enumOr(o.optString("focus"), d.focus),
            preferredDuration = enumOr(o.optString("preferredDuration"), d.preferredDuration),
            onboardingCompleted = o.optBoolean("onboardingCompleted", d.onboardingCompleted)
        )
    }

    private inline fun <reified T : Enum<T>> enumOr(name: String, fallback: T): T =
        enumValues<T>().firstOrNull { it.name == name } ?: fallback

    private fun JSONObject.arrayOrEmpty(name: String): JSONArray = optJSONArray(name) ?: JSONArray()
    private fun JSONArray.objects(): List<JSONObject> = List(length()) { getJSONObject(it) }
    private fun JSONObject.longOrNull(name: String): Long? = if (isNull(name)) null else getLong(name)
    private fun JSONObject.stringOrNull(name: String): String? = if (isNull(name)) null else getString(name)
}
