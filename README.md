# Dreamlock v0.3 - a calmer focus app

Apple-inspired spacing, hierarchy and rounded forms, built with native Kotlin + Jetpack Compose. This is not an iOS clone: system sans typography is used, not Apple's licensed SF font, and Android back, settings and accessibility remain native. Blue for action, neutral surfaces for content, automatic light and dark themes. Motivational copy keeps its lowercase terminal voice.

## 100% offline runtime · zero developer data collection

Local Room and preferences only. No server, accounts, analytics or ads. See PRIVACY.md for precisely what is stored locally, accessibility event handling and the release checks still required.

## Status: source starter, not a tested release

This revision has been hand-reviewed, not compiled or run on an Android device. The accompanying web illustrations are design studies, not emulator screenshots. Build and device checks below remain required. Do not ship or describe it as a production blocker yet.

### Existing capabilities preserved
- Room dream setup: title, why, optional photo, first milestone and first daily action.
- Home next step and completion, dream profile checkboxes and add-action flow.
- 15/25/45 minute timer and local session logging; a completed timer marks its action done.
- Interruption preview with real dream data, the unchanged 28-quote offline shuffled deck, and local RETURNED / DISMISSED outcome logging.
- Unchanged accessibility scroll-burst estimator for Instagram and YouTube; today's estimated count and feed minutes on home, progress and interruption when nonzero.
- No internet permission; offline runtime. First Gradle sync needs internet to download dependencies. Android auto-backup disabled so local records are not automatically backed up.

### Honest limits
- App-launch detection and overlay blocking are NOT implemented. FocusMonitorService.kt is a plan, not a running service.
- Timer is ViewModel-based, not a reliable background/foreground-service timer. Keep the prototype open. Process death, back navigation and background execution need engineering and device tests.
- Counter estimates scroll bursts, not actual videos. It can include home-feed scrolling; permission-off is shown as off, not a misleading zero. This is not an Accessibility Play-policy approval.
- No dream title/image editing, multiple dreams, catch-up windows or commitment mode.
- Quote shuffle history is in memory; restarting the process can repeat quotes.

## Design decisions: screen-by-screen

### Onboarding
The dream promise leads, with a single Create my dream action immediately below it. The loop, honest prototype limits and privacy follow for readers who need detail. Removed terminal boot strings and repeated ready heading. One dream symbol establishes identity; no decorative icon next to every sentence. No permissions on first launch.

### Dream setup
Dream title and why come first because the intervention depends on them. Optional image sits next to that context, then checkpoint, then today's action. Save comes last because it commits the full plan. Human-readable field labels replace code-like snake_case. State survives rotation via rememberSaveable. Photo is a secondary action with an explicit photo icon. Native keyboard insets prevent the form hiding behind the keyboard.

### Home / Today
The next action is the hero, with Start focus inside its card, not buried after six statistics. Mark complete is a quieter text action to avoid competing with Start. The dream title above opens its profile. Compact step progress follows; detailed counts move to a dedicated Progress screen. Scroll awareness has its own opt-in card, below the task so scrolling is not the app's main identity. Permission state refreshes on return from Settings. No vanity streaks or fabricated chart.

### Focus
Countdown comes first, then intention and three duration chips with an actual selected state. One primary control changes from Start to Pause/Resume. End is outlined, below the main action. Developer preview is behind a labelled disclosure so test code cannot masquerade as the product. A pre-session back action is available; Android system back is retained. Reliable background timing is explicitly not promised.

### Interruption preview
Dream image and why establish meaning, immediately followed by Back to my task. Quote and scroll estimate sit after recovery so they do not obstruct returning. Dismiss is a secondary outlined action. Removed the old '5 more minutes' promise: v0.2 only logged dismissal and did not implement a grace period. The replacement honestly says Dismiss preview. No shame copy, flashing chrome or fake security claim.

### Dream profile
Dream context precedes milestones and daily actions. Existing checkboxes stay beside their own rows. Add step follows existing steps, with explicit text explaining which milestone receives it. Back is labelled and reachable above the title. No fake edit button for an edit flow that does not exist.

### Progress (new route, existing data)
Current dream completion is separated from all-time sessions and returns; today's scroll estimate is separately labelled. Test interruption returns are disclosed. No 'this week' label over all-time data. No chart until historical aggregation exists. Back returns to Today.

### Shared components and icons
Rounded stroke vector symbols share a 24dp canvas and 1.8dp weight, drawn locally with no icon font or extra dependency. Dream, focus, progress, photo and back each clarify an action. Text labels remain visible; decorative icons have null content descriptions. Buttons have at least 52dp height, content can scroll, text uses sp and colors adapt to dark mode. Cards use 24dp corners, images 16dp, and subtle 1dp elevation. The launcher uses the same dream symbol, replacing >_. System sans avoids a bundled typeface cost; the old OFL font remains in the archive only for continuity, not used by UI.

## Open and build
1. Unzip and open DreamLock in Android Studio with JDK 17, Android SDK 35.
2. Let Gradle sync finish, then Run on an emulator or Motorola device.
3. Versions stay pinned to the v0.2 set (Kotlin 2.0.21, Compose BOM 2024.09.03, AGP 8.7.3, Room 2.6.1); this is a visual/UX revision, not a dependency migration.

## Required acceptance checks before calling it ready
- Run ./gradlew assembleDebug and lint, then fix any compile/API issues.
- Walk onboarding > setup > Today > focus > preview > return > progress > dream profile.
- Test 320dp width, large fonts, TalkBack, keyboard, dark theme, rotation and back navigation.
- Enable/disable counter in Android Settings; return to Today and verify status refresh and count updates. Test date rollover and service restarts.
- Run a short timer and early-end path; verify Room outcomes and no duplicate completion.
- Run quotes through a full deck in one process and verify attribution and reset behavior.
- Before distribution: implement and test blocking and foreground timing separately, review accessibility disclosure and current store policy. This redesign does not solve those engineering tasks.

## Source map
ui/Components.kt: shared UI + local vector icons. ui/theme/Theme.kt: semantic theme.
ui/stats/StatsScreen.kt: new Progress route. Data and monitoring classes retain v0.2 behavior.

## License and credits
Copyright (C) 2026 Nadeem A / nadeem9986.
Dreamlock's original source in this starter is provided under GNU GPL version 3.0 only (SPDX: GPL-3.0-only). See LICENSE for the full standard text. This is a distribution license, not a claim that ideas, names or all derivatives are exclusively protected. Distribution obligations and third-party licenses still apply; no legal clearance is claimed.

Third-party code and assets keep their own licenses, not Dreamlock's GPL header. The unused bundled Departure Mono font retains its supplied OFL notice in FONT-LICENSE.txt. AndroidX, Kotlin, Room, Coil, Gradle and their transitive dependencies require a version-resolved notice/license review before distributing a compiled app. THIRD_PARTY_NOTICES.md is a release checklist, not a completed audit. No Apple fonts, Apple icons or Apple branding assets are included. Original rounded vector symbols are part of the app source.
