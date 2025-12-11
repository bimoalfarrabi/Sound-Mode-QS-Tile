# SoundModeQSTile

A simple Android Quick Settings Tile to toggle between Ring, Vibrate, and Silent sound modes.

## Features

- **Toggle Sound Modes:** Quickly switch between Ring, Vibrate, and Silent (Do Not Disturb) with a single tap from your Quick Settings panel.
- **Status Bar Icon:** Utilizes the official system Silent mode to display the Silent indicator icon in the status bar.
- **No UI:** Runs entirely headless without a launcher icon or main activity, keeping the app lightweight.
- **Automatic Sync:** Stays synchronized with sound mode changes made via physical volume buttons or other system settings.

## Installation

1.  **Build (or Download APK):**
    ```bash
    ./gradlew assembleDebug
    ```
    The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.
2.  **Install on Device:**
    ```bash
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    ```

## Usage

1.  **Grant "Do Not Disturb" Access:**
    *   After installation, tap the **Sound Mode** Quick Settings tile for the first time.
    *   The app will open your device's "Do Not Disturb Access" settings.
    *   Find **Sound Mode** in the list and enable the toggle to grant permission.
    *   This permission is required for the app to control the system's Silent mode and display its status bar icon.
2.  **Add Quick Settings Tile:**
    *   Pull down your device's notification shade completely (you might need to swipe down twice).
    *   Tap the **pencil icon** (edit button) to customize your Quick Settings tiles.
    *   Locate the **Sound Mode** tile (it might be in the "inactive" section).
    *   Drag and drop the **Sound Mode** tile to your active Quick Settings tiles area.
3.  **Toggle Modes:**
    *   Tap the **Sound Mode** tile to cycle through Ring, Vibrate, and Silent modes.
    *   When in Silent mode, the corresponding icon should appear in your status bar.

## Contributing

Feel free to open issues or submit pull requests on the GitHub repository.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
