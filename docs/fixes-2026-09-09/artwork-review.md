# Exercise artwork — 2026-09-09

Built-in image_gen used, exactly one edit call per asset (two calls total). Original files preserved. Inputs and saved outputs inspected with view_image.

## exercise_dumbbell_romanian_deadlift.png

Input edit target: exercise_romanian_deadlift.png.
Prompt: scientific-educational, precise-object-edit, square Android fitness PNG. Preserve adult identity, hair, face, colored bare torso, neutral black shorts, shoes, white background and both full-body phases. Remove entire barbell in each phase, replace with two separate matching dumbbells, one in each hand. No connected bar, floating weights or cropped implements. Preserve Romanian hip hinge, soft knees, hips back, straight arms; weights near thighs/shins. Preserve original composition; change cyan stabilizer overlays to green, primary muscles red, assisting orange. No text, arrows, labels, logos or watermark.

Visual review: two separate dumbbells per phase, grips and visible weights present, hip hinge and full body preserved. Neck angle inherited from source remains somewhat extended; no claim of exhaustive anatomical certification. White background, red/orange/green overlays, no stray barbell remnants.

## exercise_bodyweight_lunge.png

Input edit target: exercise_lunge.png.
Prompt: scientific-educational, precise-object-edit, square Android fitness PNG. Preserve adult identity, hair, natural face, bare colored torso, black shorts/shoes, white background and two full-body lunge phases. Remove every dumbbell/weight, locally repair relaxed empty hands with natural fingers at sides. Preserve upright torso, front foot flat, rear heel raised, rear knee close to ground, full-body margins. Keep highlight locations while converting cyan stabilizers to green; red primary and orange assisting muscles. No equipment, text, arrows, labels, watermark or logo.

Visual review: all weights removed; visible empty hands are naturally relaxed. Far hand in lower phase is naturally occluded by forward thigh. Original split stance and lower lunge retained, feet visible. No equipment fragments. Rear knee is very close to ground as in source.

Both final PNGs saved into app/src/main/res/drawable-nodpi. No code, Gradle or Git changes in this artwork subtask. Android rendering not tested here.