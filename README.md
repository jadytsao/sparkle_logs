# Sparkle Log

A simple Android app for logging small moments of fulfillment ("sparkles") during your day — what happened, and how it made you feel.

- **Add**: log an event/incident and pick a feeling. Feelings start as free text; once created, they show up as reusable colored chips so you never retype them.
- **By Date**: browse sparkles chronologically, grouped by day.
- **By Feeling**: browse sparkles grouped by feeling, sorted by how often each feeling comes up. Tap a feeling's header to change its color.

Everything is stored on-device with Room (SQLite) — no server, no account, fully offline.

## Tech

Kotlin, Jetpack Compose (Material 3), Room, Navigation-Compose, MVVM (`SparkleViewModel` + `SparkleRepository`). minSdk 26, targetSdk 35.

## Opening the project

The project has already been built successfully once from the command line (`./gradlew assembleDebug`, using Android Studio's own Gradle 8.9 + JBR 21 toolchain) — Room's code generation, Kotlin compilation, and APK packaging all pass cleanly, so it should sync straight away.

1. Open Android Studio → **Open** → select the `sparkle_logs` folder (it's already registered as an Android Studio project here, so it may open directly).
2. Let Gradle sync finish.
3. If you don't have an emulator yet: **Device Manager** (right sidebar) → **Create device** → pick any phone profile → download a system image → Finish.
4. Run on that emulator (or a physical device) via the ▶ Run button.

## Manual test checklist

- Add a sparkle with a brand-new feeling name → confirm it gets a color automatically and appears as a chip.
- Add another sparkle and reuse that feeling by tapping its chip (not retyping it).
- Check the new entries show up correctly on both **By Date** and **By Feeling**.
- On **By Feeling**, tap a feeling's header and change its color → confirm it updates everywhere that feeling appears.
