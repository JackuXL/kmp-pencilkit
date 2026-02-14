# kmp-pencilkit

Kotlin Multiplatform bridge for iPadOS [PencilKit](https://developer.apple.com/documentation/pencilkit).

## What is included

- `pencilkit-bridge` KMP module
- `commonMain` API for tool selection and bridge control
- `iosMain` implementation backed by `PKCanvasView`

## Core API

`commonMain` exposes:

- `PencilKitBridge`
- `PencilKitTool` (`Ink`, `Eraser`, `Lasso`)
- `PencilInkType`, `PencilEraserType`
- `PencilColor`

## iPadOS usage example

```kotlin
import io.github.jackuxl.pencilkit.PencilColor
import io.github.jackuxl.pencilkit.PencilInkType
import io.github.jackuxl.pencilkit.PencilKitBridge
import io.github.jackuxl.pencilkit.PencilKitTool

val bridge = PencilKitBridge()
bridge.attachTo(containerView) // containerView is UIView on iOS

bridge.setTool(
    PencilKitTool.Ink(
        type = PencilInkType.Pencil,
        color = PencilColor(red = 0.1, green = 0.1, blue = 0.1),
        width = 7.0
    )
)

val bytes = bridge.exportDrawingData()
bridge.importDrawingData(bytes)
```

## Build

```bash
./gradlew :pencilkit-bridge:assemble
```