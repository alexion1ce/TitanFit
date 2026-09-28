"""Run the current pure Kotlin regression tests without requiring Gradle sockets."""
import os
import re
import subprocess
from pathlib import Path

OUT = Path(__file__).resolve().parent
ROOT = OUT.parents[1]
SRC = ROOT / 'app/src/main/java/com/example/fitapp'
CACHE = Path.home() / '.gradle/caches/modules-2/files-2.1'
JAVA = Path('C:/Program Files/Microsoft/jdk-21.0.11.10-hotspot/bin/java.exe')


def jar(group, artifact, version):
    return next((CACHE / group / artifact / version).glob(f'*/{artifact}-{version}.jar'))


stdlib = jar('org.jetbrains.kotlin', 'kotlin-stdlib', '2.0.20')
coroutines = jar('org.jetbrains.kotlinx', 'kotlinx-coroutines-core-jvm', '1.8.1')
annotations = jar('org.jetbrains', 'annotations', '13.0')
compiler_cp = [jar('org.jetbrains.kotlin', 'kotlin-compiler-embeddable', '2.0.20'), stdlib,
               jar('org.jetbrains.intellij.deps', 'trove4j', '1.0.20200330'), annotations, coroutines]
dependencies = [stdlib, coroutines, annotations, jar('androidx.room', 'room-common', '2.6.1'),
                jar('junit', 'junit', '4.13.2'), jar('org.hamcrest', 'hamcrest-core', '1.3')]
cp = os.pathsep.join(map(str, dependencies))
support = ['package com.example.fitapp.data.repository']
for filename, cls in [('WorkoutRepository.kt', 'WorkoutExerciseItem'), ('WorkoutLogRepository.kt', 'WeeklyVolume')]:
    source = (SRC / 'data/repository' / filename).read_text(encoding='utf-8-sig')
    support.append(re.search(r'data class ' + cls + r'\([\s\S]*?\n\)', source).group(0))
dto = OUT / 'ExactDataDeclarations.kt'
dto.write_text('\n\n'.join(support), encoding='utf-8')
selected = list((SRC / 'data/local/entity').glob('*.kt')) + [dto] + [SRC / p for p in [
    'data/repository/WorkoutSessionLogic.kt', 'data/repository/WeeklyVolumeCalculator.kt',
    'data/repository/WorkoutStatistics.kt', 'data/repository/ProgramRecommendations.kt',
    'ui/builder/WorkoutEditorUiState.kt', 'ui/session/SessionInput.kt', 'ui/session/SessionWriteQueue.kt']]
tests = list((ROOT / 'app/src/test/java').rglob('*Test.kt'))
test_classes = []
for test in tests:
    content = test.read_text(encoding='utf-8-sig')
    test_classes.append(re.search(r'package ([\w.]+)', content)[1] + '.' + re.search(r'class (\w+)', content)[1])
classes = OUT / 'core-test-classes'
compile_command = [str(JAVA), '-cp', os.pathsep.join(map(str, compiler_cp)),
                   'org.jetbrains.kotlin.cli.jvm.K2JVMCompiler', '-no-stdlib', '-no-reflect',
                   '-jvm-target', '17', '-classpath', cp, '-d', str(classes)] + list(map(str, selected + tests))


def run(command, filename):
    result = subprocess.run(command, cwd=ROOT, capture_output=True, text=True, encoding='utf-8', errors='replace')
    log = f'exit_code={result.returncode}\n' + result.stdout + result.stderr
    (OUT / filename).write_text(log, encoding='utf-8')
    print(log)
    if result.returncode:
        raise SystemExit(result.returncode)


run(compile_command, 'core-compile.txt')
run([str(JAVA), '-cp', str(classes) + os.pathsep + cp, 'org.junit.runner.JUnitCore'] + test_classes, 'core-tests.txt')
