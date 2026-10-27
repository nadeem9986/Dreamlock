# Dreamlock privacy policy - v0.3 starter

28 October 2026. Maintainer: Nadeem A / nadeem9986.

## Offline by design
Dreamlock has no account, server, analytics, advertising SDK or telemetry. The app does not transmit your personal records to the developer or another service. It has no granted INTERNET permission: the manifest explicitly removes it during merging. Building the project requires internet to download development dependencies; running the installed app does not.

## Records on your device
Dream titles, reasons, image references, milestones, daily actions, focus sessions and interruption choices are stored in the app's local Room database. Today's estimated reels/shorts count and feed time are stored in local preferences. Quotes are bundled in the app. The developer cannot remotely see these records. 'Zero collection by the developer' does not mean the app stores nothing: these local records make its features work.

Your optional dream image is selected with Android's photo picker. Only content:// references are accepted by the UI; remote HTTP image URLs are rejected. The system picker or an external photo provider may have its own network behavior and privacy policy. Dreamlock's image loader is limited to the selected content URI.

Automatic Android cloud backup is disabled in this starter. Clearing app storage or uninstalling removes Dreamlock's private records; it does not delete original photos from your photo library. Your OS, device backup tools or a device administrator are outside Dreamlock's control. There is no account to delete and no server copy for the developer to recover.

## Optional accessibility counter
The counter only operates after you explicitly enable Dreamlock's service in Android Accessibility settings. The configuration limits event delivery to Instagram and YouTube. It observes scroll and window-state/content-change event types and event times to estimate scroll bursts and feed time. It never reads event text, video titles, messages, screenshots or the view tree; canRetrieveWindowContent is false. No event content is stored or sent. The app records aggregate counts and time locally.

This is an estimate: it cannot reliably distinguish reels/shorts from home-feed scrolling and can miss untouched autoplay. You can switch the service off in Android Settings at any time. App-launch detection and blocking are planned, not implemented in v0.3. Any future change to access or data handling must update this disclosure before release.

## Before public release
This policy describes inspected starter source, not a certified production build. Verify the merged release manifest, dependency behavior, permissions and actual device traffic before publishing. The app must display a prominent in-app accessibility disclosure and obtain informed consent appropriate to the final functionality; opening Android Settings alone is not a claim of store-policy compliance. The final store privacy-policy URL and Data safety answers must match the shipped binary. No store approval is promised here.

For source/privacy issues, contact the maintainer through the Dreamlock project: https://github.com/nadeem9986/Dreamlock (do not include private dream records in public issues).
