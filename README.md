# SoundModeQSTile

A simple Android Quick Settings Tile to toggle between Ring, Vibrate, and Silent sound modes.

## Branches

This repository contains two main branches, each offering a different approach to Silent mode implementation:

-   **`main` branch (Official Silent Mode):**
    *   **Method:** Uses the Android system's official `RINGER_MODE_SILENT` for Silent mode.
    *   **Behavior:** This method reliably displays the Silent indicator icon in the status bar.
    *   **Requirement:** Requires "Do Not Disturb" (DND) access permission, as Android links `RINGER_MODE_SILENT` to DND functionalities on modern devices.
    *   **APK:** A pre-built debug APK for this branch will be provided in the repository's [Releases](https://github.com/bimoalfarrabi/Sound-Mode-QS-Tile/releases) section.

-   **`true-silent-implementation` branch (Volume 0 Method):**
    *   **Method:** Achieves Silent mode by setting the ringtone and notification volumes to 0 (while keeping the ringer mode as `NORMAL`).
    *   **Behavior:** This method effectively mutes the device without requiring DND permission. However, it does **not** display the Silent indicator icon in the status bar, as the system still considers the device to be in `NORMAL` ringer mode (just with zero volume).
    *   **Requirement:** Does **not** require "Do Not Disturb" access permission.
    *   **APK:** No pre-built APK will be provided for this branch. You must build it from source if you wish to use this implementation.

## Features

-   **Toggle Sound Modes:** Quickly switch between Ring, Vibrate, and Silent with a single tap from your Quick Settings panel.
-   **No UI:** Runs entirely headless without a launcher icon or main activity, keeping the app lightweight.
-   **Automatic Sync:** Stays synchronized with sound mode changes made via physical volume buttons or other system settings.

## Installation

For the `main` branch (Official Silent Mode):

1.  **Download APK:** Download the pre-built debug APK from the [Releases](https://github.com/bimoalfarrabi/Sound-Mode-QS-Tile/releases) section.
2.  **Install on Device:**
    ```bash
    adb install -r path/to/downloaded/app-debug.apk
    ```

For the `true-silent-implementation` branch (Volume 0 Method) or to build from source:

1.  **Clone Repository:**
    ```bash
    git clone https://github.com/bimoalfarrabi/Sound-Mode-QS-Tile.git
    cd Sound-Mode-QS-Tile
    ```
2.  **Switch Branch (if not `main`):**
    ```bash
    git checkout true-silent-implementation # or any other branch
    ```
3.  **Build:**
    ```bash
    ./gradlew assembleDebug
    ```
    The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.
4.  **Install on Device:**
    ```bash
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    ```

## Usage

(Applies to both branches, but DND permission steps are only for the `main` branch)

1.  **Grant "Do Not Disturb" Access (only for `main` branch):**
    *   After installing the `main` branch's APK, tap the **Sound Mode** Quick Settings tile for the first time.
    *   The app will open your device's "Do Not Disturb Access" settings.
    *   Find **Sound Mode** in the list and enable the toggle to grant permission.
    *   This permission is required for the `main` branch to control the system's Silent mode and display its status bar icon.
2.  **Add Quick Settings Tile:**
    *   Pull down your device's notification shade completely (you might need to swipe down twice).
    *   Tap the **pencil icon** (edit button) to customize your Quick Settings tiles.
    *   Locate the **Sound Mode** tile (it might be in the "inactive" section).
    *   Drag and drop the **Sound Mode** tile to your active Quick Settings tiles area.
3.  **Toggle Modes:**
    *   Tap the **Sound Mode** tile to cycle through Ring, Vibrate, and Silent modes.
    *   When in Silent mode on the `main` branch, the corresponding icon should appear in your status bar.

## Contributing

Feel free to open issues or submit pull requests on the GitHub repository.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.