# Project Instructions

## Exercise Artwork Generation Standard

Use this as the shared prompt template for all generated exercise PNG assets in
`app/src/main/res/drawable-nodpi`.

Generate one exercise artwork asset for Android `drawable-nodpi`.

Use case: scientific-educational

Asset type: fitness exercise illustration PNG for a mobile app.

Primary request: create an accurate illustration for exercise `{exercise_code}` /
`{exercise_name}`.

Scene/backdrop: pure clean white background, no floor texture, no room, no
decorative elements.

Subject: athletic adult person with realistic but illustrated proportions,
normal visible hair, natural non-cartoon face, colored skin and subtle athletic
clothing. For upper-body exercises, prefer a bare torso or minimal neutral
clothing so anatomical muscle highlights remain visible. Do not make the person
bald or nearly bald. Do not use a gray-white mannequin body. Avoid bright green
or bright blue shirts as dominant clothing unless the exercise specifically
requires it.

Exercise accuracy: show the exact exercise mechanics for `{exercise_name}`.
Usually show two movement phases: start position and finish position. Keep body
orientation, grip, stance, bench angle, machine path, cable path, bar path, and
range of motion anatomically correct. For machine/cable exercises, show the
equipment clearly and make the cable, pulley, handles, selector pin, and weight
stack physically plausible. If the selector pin is visible, it must stay in the
same selected plate number/index in every movement phase; when the selected block
lifts, the pin moves upward together with that selected plate. In the start
phase, unloaded/resting plates must stay down. In the working phase, only the
selected top block of the stack should lift; the lower unselected plates remain
down. Do not show duplicate selector pins.

Style/medium: polished color anatomical fitness illustration, clean
semi-realistic educational exercise-diagram look, consistent premium fitness app
asset style. Not photorealistic, not childish, not comic, not cartoonish.
Use the approved `exercise_rope_pushdown.png` result as the practical visual
benchmark for this project: realistic illustrated adult with normal hair,
natural face, colored bare torso when useful for upper-body anatomy, black/neutral
shorts, clean white background, coherent single-piece equipment, and crisp
red/orange/green anatomical overlays. Do not drift toward cartoon faces,
gray-white mannequins, bright colored shirts, or collage-like machine parts.

Composition/framing: full body and all required equipment visible, centered,
generous margins, square or near-square app asset composition. No cropping of
hands, feet, weights, cables, handles, benches, bars, or machines.

Muscle highlights: primary working muscles in red, assisting muscles in orange,
stabilizing muscles in green. These colors are anatomical overlays, not clothing
colors.

Constraints: no text, no numbers, no arrows, no labels, no watermark, no logo,
no brand marks. Both sides of the body must be anatomically complete unless
hidden naturally by perspective. Hands must visibly hold the correct implement.

Avoid: missing limbs, hidden hands, wrong handle/attachment, cable crossing
through the body, cable entering the back/neck/chin/face, impossible machine
mechanics, floating weights, duplicate selector pins, selector pin changing
position between phases, all plates lifting when only selected plates should
lift, mismatched start/finish phases, gray mannequin style, bald character,
bright green or bright blue clothing as the main visual element.

Revision rule: if a generated image has a good composition, body pose, equipment,
and exercise mechanics, preserve that image and edit only the specific problem
area. Do not regenerate the whole scene unless the composition or mechanics are
fundamentally wrong. For small fixes such as removing a shirt, correcting a
visible implement, or cleaning a local defect, use the existing best image as the
edit target and keep all other parts unchanged.

Exercise-specific details to append:

```text
Exercise-specific request:
{exercise_specific_description}
```

Workflow rule: generate at most three artwork assets per turn, one imagegen call
per asset. Save finished PNG files into `app/src/main/res/drawable-nodpi`. Do not
build APKs, run Gradle, change git state, or edit app code unless the user asks
for that separately.
