"""Regression checks using real migration SQL in isolated in-memory SQLite.

This is not an Android/Room integration test. Statements are extracted verbatim;
the extractor fails if any execSQL call is skipped. No app database is accessed.
"""
import json
import re
import sqlite3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "app/src/main/java/com/example/fitapp"
MIGRATIONS = (SRC / "data/local/DatabaseMigrations.kt").read_text(encoding="utf-8-sig")


def statements(version):
    block = MIGRATIONS.split(f"val MIGRATION_{version} =", 1)[1].split("\n    val ", 1)[0]
    pattern = r'db\.execSQL\(\s*("""[\s\S]*?"""\.trimIndent\(\)|"[^"\n]*(?:"\s*\+\s*"[^"\n]*)*")\s*\)'
    matches = list(re.finditer(pattern, block))
    assert len(matches) == block.count("db.execSQL("), f"Unparsed SQL in {version}"
    for match in matches:
        raw = match.group(1)
        yield raw[3:raw.rfind('"""')] if raw.startswith('"""') else "".join(re.findall(r'"([^"\n]*)"', raw))


def migrate(db, version):
    for sql in statements(version):
        db.execute(sql)
    assert not db.execute("PRAGMA foreign_key_check").fetchall()


def fixture():
    db = sqlite3.connect(":memory:")
    db.execute("PRAGMA foreign_keys=ON")
    # The original v1 catalog schema is outside these migration changes. Include
    # only columns referenced by migrated SQL; new columns are checked below.
    db.execute("CREATE TABLE exercises (id INTEGER PRIMARY KEY NOT NULL, code TEXT NOT NULL)")
    for version in ("1_2", "2_3", "3_4"):
        migrate(db, version)
    db.executemany("INSERT INTO exercises VALUES (?, ?)", [(90, "squat"), (10, "press"), (40, "row"), (50, "plank"), (60, "side_plank")])
    db.execute("INSERT INTO workouts VALUES (1, 'Program', 'PRESET', NULL)")
    db.execute("INSERT INTO workout_logs VALUES (1, 1, 'Program', 1, 2, 1)")
    db.execute("INSERT INTO workout_exercises VALUES (1, 1, 90, 0, 2, '8-12', 90)")
    db.execute("INSERT INTO workout_exercises VALUES (2, 1, 10, 1, 2, '8-12', 120)")
    db.executemany("INSERT INTO set_logs VALUES (?, 1, ?, ?, ?, ?, ?)", [
        (1, 90, 1, 20, 10, 1), (2, 90, 2, 25, 8, 0), (3, 10, 1, 30, 10, 1)
    ])
    return db


def snapshot(db):
    return db.execute("SELECT id,logId,exerciseId,setNumber,weight,reps,done FROM set_logs ORDER BY id").fetchall()


def changed_template():
    db = fixture()
    old = snapshot(db)
    db.execute('UPDATE workout_exercises SET exerciseId=40, "order"=1 WHERE id=1')
    db.execute('UPDATE workout_exercises SET "order"=0 WHERE id=2')
    migrate(db, "4_5")
    assert db.execute("SELECT DISTINCT exerciseOrder FROM set_logs").fetchall() == [(0,)]
    migrate(db, "5_6")
    assert snapshot(db) == old, "Migration changed historical approaches"
    rows = db.execute("SELECT DISTINCT exerciseId,exerciseOrder,restSeconds FROM set_logs ORDER BY exerciseOrder").fetchall()
    assert rows == [(10, 0, 120), (90, 1, 60)], rows
    return rows


def collisions_and_duration():
    db = fixture()
    migrate(db, "4_5")
    db.execute("UPDATE set_logs SET exerciseOrder=0")
    db.execute('INSERT INTO workout_exercises VALUES (3,1,90,4,1,\'8\',180)')
    db.execute("INSERT INTO set_logs VALUES (4,1,90,1,40,5,1,4)")
    db.execute("INSERT INTO set_logs VALUES (5,1,50,1,10,30,1,5)")
    db.execute("INSERT INTO set_logs VALUES (6,1,60,1,10,0,0,6)")
    old = snapshot(db)[:4]
    migrate(db, "5_6")
    assert snapshot(db)[:4] == old
    rows = db.execute("SELECT id,exerciseId,exerciseOrder,restSeconds FROM set_logs ORDER BY id").fetchall()
    assert [r[2] for r in rows] == [1, 1, 0, 2, 3, 4], rows
    assert rows[3][3] == 180, "Repeated exercise lost its position-specific rest"
    duration = db.execute("SELECT reps,weight,durationSeconds FROM set_logs WHERE id IN (5,6) ORDER BY id").fetchall()
    assert duration == [(0, 0.0, 30), (0, 0.0, 1)], duration
    return {"groups": rows, "duration": duration}


def dao_query(filename, method):
    source = (SRC / "data/local/dao" / filename).read_text(encoding="utf-8-sig")
    match = re.search(r'@Query\("([^"\n]+)"\)\s*(?:suspend )?fun ' + method + r'\(', source)
    assert match, f"DAO query missing: {method}"
    return match.group(1)


def archive_and_schema():
    db = fixture()
    migrate(db, "4_5")
    migrate(db, "5_6")
    old = snapshot(db)
    db.execute(dao_query("WorkoutDao.kt", "archiveById"), {"id": 1})
    db.execute(dao_query("ExerciseDao.kt", "archiveByCodes"), {"codes": "squat"})
    assert snapshot(db) == old
    assert db.execute("SELECT count(*) FROM workout_logs").fetchone()[0] == 1
    assert db.execute("SELECT count(*) FROM workout_exercises").fetchone()[0] == 2
    assert db.execute("SELECT isArchived FROM workouts WHERE id=1").fetchone()[0] == 1
    assert db.execute("SELECT isArchived FROM exercises WHERE id=90").fetchone()[0] == 1
    assert db.execute(dao_query("WorkoutDao.kt", "observeByType"), {"type": "PRESET"}).fetchall() == []
    schema = {}
    for table, columns in {"workouts": {"isArchived": ("INTEGER", 1, "0"), "presetCode": ("TEXT", 0, None)},
                           "exercises": {"isArchived": ("INTEGER", 1, "0")},
                           "set_logs": {"exerciseOrder": ("INTEGER", 1, "0"), "durationSeconds": ("INTEGER", 0, None), "restSeconds": ("INTEGER", 1, "60")}}.items():
        info = {row[1]: (row[2], row[3], row[4]) for row in db.execute(f"PRAGMA table_info({table})")}
        for column, expected in columns.items():
            assert info[column] == expected, (table, column, info[column])
        schema[table] = {column: info[column] for column in columns}
    indices = {row[1]: row[2] for row in db.execute("PRAGMA index_list(workouts)")}
    assert indices["index_workouts_presetCode"] == 1
    assert [r[2] for r in db.execute("PRAGMA index_info(index_set_logs_logId_exerciseOrder_setNumber)")] == ["logId", "exerciseOrder", "setNumber"]
    # Fresh Room entity declarations must promise the same defaults. Full Room
    # schema validation still requires the Android integration test environment.
    for filename, field, value in [("Workout.kt", "isArchived", "0"), ("Exercise.kt", "isArchived", "0"), ("SetLog.kt", "restSeconds", "60"), ("SetLog.kt", "exerciseOrder", "0")]:
        source = (SRC / "data/local/entity" / filename).read_text(encoding="utf-8-sig")
        assert re.search(r'@ColumnInfo\(defaultValue = "' + value + r'"\)\s+val ' + field, source), (filename, field)
    db.execute("UPDATE workouts SET presetCode='stable' WHERE id=1")
    try:
        db.execute("INSERT INTO workouts(name,type,presetCode) VALUES ('Duplicate','PRESET','stable')")
    except sqlite3.IntegrityError:
        pass
    else:
        raise AssertionError("Preset code not unique")
    db.execute("INSERT INTO workouts(name,type) VALUES ('Custom','CUSTOM')")
    db.execute("INSERT INTO workouts(name,type) VALUES ('Custom 2','CUSTOM')")
    assert not db.execute("PRAGMA foreign_key_check").fetchall()
    return schema


def room_schema_check():
    schema_path = ROOT / "app/schemas/com.example.fitapp.data.local.AppDatabase/6.json"
    schema = json.loads(schema_path.read_text(encoding="utf-8"))["database"]
    assert schema["version"] == 6
    fresh = sqlite3.connect(":memory:")
    for entity in schema["entities"]:
        table = entity["tableName"]
        fresh.execute(entity["createSql"].replace("${TABLE_NAME}", table))
        for index in entity["indices"]:
            fresh.execute(index["createSql"].replace("${TABLE_NAME}", table))
    migrated = fixture()
    migrate(migrated, "4_5")
    migrate(migrated, "5_6")

    def columns(db, table):
        # cid differs when ALTER TABLE appends fields: Room compares by name.
        return {r[1]: {"type": r[2], "notNull": bool(r[3]), "default": r[4], "primaryKeyPosition": r[5]}
                for r in db.execute(f"PRAGMA table_info(`{table}`)")}

    def indices(db, table):
        return {r[1]: {"unique": bool(r[2]), "columns": [c[2] for c in db.execute(f"PRAGMA index_info(`{r[1]}`)")]}
                for r in db.execute(f"PRAGMA index_list(`{table}`)") if r[3] != "pk"}

    def foreign_keys(db, table):
        # Compare relationship contents, not SQLite's declaration-order ids.
        return sorted((r[2], r[3], r[4], r[5], r[6], r[7])
                      for r in db.execute(f"PRAGMA foreign_key_list(`{table}`)"))

    tables = {}
    for table in ("workouts", "workout_exercises", "workout_logs", "set_logs", "exercise_catalog_meta"):
        actual, expected = columns(migrated, table), columns(fresh, table)
        assert actual == expected, (table, "columns", actual, expected)
        actual_indices, expected_indices = indices(migrated, table), indices(fresh, table)
        assert actual_indices == expected_indices, (table, "indices", actual_indices, expected_indices)
        actual_fks, expected_fks = foreign_keys(migrated, table), foreign_keys(fresh, table)
        assert actual_fks == expected_fks, (table, "foreign_keys", actual_fks, expected_fks)
        # Validate AUTOINCREMENT as well as column-level PK positions.
        actual_sql = migrated.execute("SELECT sql FROM sqlite_master WHERE type='table' AND name=?", (table,)).fetchone()[0]
        expected_sql = fresh.execute("SELECT sql FROM sqlite_master WHERE type='table' AND name=?", (table,)).fetchone()[0]
        assert ("AUTOINCREMENT" in actual_sql.upper()) == ("AUTOINCREMENT" in expected_sql.upper())
        tables[table] = {"columns": actual, "indices": actual_indices, "foreignKeys": actual_fks, "status": "match"}
    actual_archive = columns(migrated, "exercises")["isArchived"]
    assert actual_archive == columns(fresh, "exercises")["isArchived"]
    result = {
        "status": "passed", "sqliteVersion": sqlite3.sqlite_version,
        "roomSchemaVersion": schema["version"], "roomIdentityHash": schema["identityHash"],
        "scope": "KSP-generated Room fresh createSql versus real SQL migrations 1 through 6, both executed in in-memory SQLite; not Android runtime",
        "fullyComparedTables": tables,
        "partiallyComparedTables": {"exercises": {"isArchived": actual_archive, "status": "match"}},
        "limitations": ["The fixture intentionally defines only exercises.id and exercises.code from v1; exercises original fields, foreign keys and indices are not compared.",
                        "The fixture omits unchanged v1 muscle_groups and equipment tables.",
                        "This verifies SQLite schema equivalence against Room's generated schema; Room on-device migration validation still requires Android execution."]
    }
    Path(__file__).with_name("room-schema-check.json").write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
    return {"status": result["status"], "fullyComparedTables": list(tables), "partial": "exercises.isArchived"}


if __name__ == "__main__":
    results = {"engine": sqlite3.sqlite_version, "scope": "in-memory SQLite SQL checks, not Android/Room runtime",
               "changed_template": changed_template(), "collisions_and_duration": collisions_and_duration(),
               "archive_and_schema": archive_and_schema(), "room_schema": room_schema_check()}
    report = json.dumps(results, indent=2)
    Path(__file__).with_name("migration-results.json").write_text(report + "\n", encoding="utf-8")
    print(report)
