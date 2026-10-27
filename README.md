# DREAMLOCK

**A pilot for your dream, not a guard for your phone.**

DREAMLOCK is an Android app that connects your daily focus to your long-term dreams. When you reach for a distracting app during a focus session, it doesn't scold you. It shows you what you are working toward and gives you one tap back to the task.

> **Status:** early planning. No application code yet. Product spec, MVP scope and roadmap are being written first. See [Project status](#project-status).

## The problem

Most app blockers are built on friction and guilt: timers, lock screens, streak counters that reset when you slip. They treat attention as a discipline problem. People either bypass them or feel worse about themselves, and then uninstall.

The real gap is motivation. In the moment you open a distracting app, the dream you care about is abstract and far away, and the feed is right there.

## The idea

DREAMLOCK makes the dream concrete at the moment it matters. You define what you are working toward, break it into milestones and daily actions, and the app shows up at the point of distraction with something personal: your own dream image, your own words, and a short path back.

## Core loop: Plan > Protect > Prove > See

| Step | What happens |
|------|--------------|
| **Plan** | Define a dream, split it into milestones, pick a small daily action. |
| **Protect** | Start a focus session. If you open a distracting app, a dream interruption screen appears instead of a lecture. |
| **Prove** | Mark the action done. Progress is recorded locally, with no pressure mechanics. |
| **See** | Review how daily actions add up toward the milestone and the dream. |

## Key features (planned)

- **Dream profile.** Your dreams, images and personal "why", stored on the device.
- **Focus timer.** Simple focus sessions tied to a specific dream action.
- **Dream interruption.** When a distracting app opens in a session, show your dream image, a short message and one tap back to the task.
- **Commitment mode.** Opt-in extra friction for people who want it. Never the default.
- **Catch-up windows.** Planned, limited time for social media (for example 30 minutes) that you set yourself, with the app honoring the stop.
- **Dream-to-action milestones.** Deterministic, rules-based planning that turns a dream into the next small step.
- **Privacy-first and offline.** Local storage, minimal data collection, works without a connection.
- **Dream Terminal UI direction.** A terminal-style, JARVIS-like interface for people who like their tools to feel like a cockpit.

Later and speculative: an optional ESP32 desk beacon that signals focus state.

## Design principles

DREAMLOCK is designed around how motivation actually works, not around punishment.

- **No shame.** No guilt copy, no streak resets, no manipulative messaging. A slip is information, not failure, and recovery is one tap.
- **Autonomy first.** You choose your dreams, your apps, your windows and how strict to be. Stricter modes are opt-in.
- **Make the goal vivid.** Personal images and your own words at the point of temptation, since concrete goals compete better with a feed than abstract ones.
- **Small next actions.** Milestones are broken into steps small enough to start today.
- **Honest about limits.** Android cannot truly block an app from launching. DREAMLOCK detects the launch and responds with an overlay. It is a nudge you opted into, not a tamper-proof lock, and the app says so.
- **Privacy by default.** Your dreams are personal. They stay on your device.

## Tech stack (planned)

- **Platform:** Android, Kotlin
- **UI:** Jetpack Compose
- **Storage:** Room, local-first
- **Planning logic:** deterministic and rules-based before any AI
- **Detection:** Usage Access plus display-over-other-apps overlay, with AccessibilityService evaluated against Google Play policy
- **Distribution:** free for users, responsible monetization later (sponsorships, optional donations, non-intrusive ads)

More detail in [Docs/architecture.md](Docs/architecture.md).

## Project status

Early planning. Current work:

- [x] Product concept and principles
- [x] Feature backlog and prioritization
- [ ] Architecture decisions
- [ ] MVP scope locked ([Docs/mvp-scope.md](Docs/mvp-scope.md))
- [ ] Throwaway prototype to prove app-launch detection and overlay on a real device
- [ ] Early user tests with about 10 people

## Roadmap

| Phase | Goal |
|-------|------|
| **0. Foundations** | Spec, principles, architecture, risk list. |
| **1. Blocking prototype** | Prove launch detection and the interruption overlay on a physical phone. |
| **2. MVP** | Dream profile, focus timer, dream interruption, offline storage. |
| **3. Commitment and catch-up** | Commitment mode, catch-up windows, slip recovery. |
| **4. Closed test** | Testers on Google Play closed testing, feedback loop. |
| **5. Public release** | Production release and monetization experiments. |

Dates are intentionally not set yet.

## Contributing

The project is at the planning stage and not open for code contributions yet. Ideas and feedback are welcome as issues.

## License

To be decided.
