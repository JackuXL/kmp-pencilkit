# iPadOS Demo App

This folder contains a SwiftUI demo app for manual testing of the KMP PencilKit bridge.

## Prerequisites

- macOS with Xcode 15+
- `xcodegen` (install with `brew install xcodegen`)

## Run

1. Generate the Xcode project:

   ```bash
   cd demo-ios
   ./generate-project.sh
   ```

2. Open `demo-ios/PencilKitBridgeDemo.xcodeproj` in Xcode.
3. Select an iPad simulator (or an iPad device).
4. Build and Run.

The target has a pre-build script that runs:

```bash
./gradlew :pencilkit-bridge:embedAndSignAppleFrameworkForXcode
```

So the latest Kotlin framework is always used by the demo app.

## Background image testing

Inside the demo app, tap **Select Background** to pick an image from the photo library.
The selected image becomes the canvas background, and PencilKit strokes are drawn on top.
Tap **Remove Background** to reset to the default background.
