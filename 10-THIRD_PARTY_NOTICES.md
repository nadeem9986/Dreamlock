# Third-party notices: release checklist

Do not replace dependency licenses with the app's GPL license. Preserve their copyright and license texts in the distribution where required.

- Bundled Departure Mono: supplied FONT-LICENSE.txt remains unchanged. It is not used by v0.3 UI; remove the unused font only together with updating the notice inventory.
- Gradle wrapper: preserve its upstream copyright/license notices. Distribution is downloaded at build time.
- Resolve the pinned dependency graph with Gradle and capture all direct/transitive artifacts and exact licenses before release: AndroidX/Compose, Room, Kotlin/coroutines, Coil and test/build dependencies.
- Generate a dependency inventory and notices, inspect source license files and check GPL-3.0 compatibility. Downloaded binaries alone are not a license audit.
- Verify any user-supplied image or later-added assets separately. Do not assume online images, SF Symbols or Apple's SF fonts are freely redistributable in Android apps.
- Obtain legal advice for distribution questions, contributor rights, monetization or dual licensing. This starter does not establish ownership of future contributions.

Status: GPL text included; supplied font notice retained; dependency audit not completed. No compiled app is being distributed in this archive.
