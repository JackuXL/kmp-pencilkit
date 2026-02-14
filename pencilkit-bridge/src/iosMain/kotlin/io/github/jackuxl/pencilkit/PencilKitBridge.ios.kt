@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.jackuxl.pencilkit

import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.Foundation.length
import platform.PencilKit.PKCanvasView
import platform.PencilKit.PKDrawing
import platform.PencilKit.PKEraserTool
import platform.PencilKit.PKEraserTypeBitmap
import platform.PencilKit.PKEraserTypeVector
import platform.PencilKit.PKInkingTool
import platform.PencilKit.PKInkTypeMarker
import platform.PencilKit.PKInkTypePen
import platform.PencilKit.PKInkTypePencil
import platform.PencilKit.PKLassoTool
import platform.PencilKit.PKTool
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.posix.memcpy

actual typealias PencilKitContainerView = UIView

actual class PencilKitBridge actual constructor() {
    private val internalCanvasView = PKCanvasView().apply {
        autoresizingMask = UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        drawing = PKDrawing()
        tool = PKInkingTool(PKInkTypePencil, PencilColor.Black.toUIColor(), 6.0)
    }

    actual val canvasView: PencilKitContainerView
        get() = internalCanvasView

    actual fun attachTo(containerView: PencilKitContainerView) {
        if (internalCanvasView.superview === containerView) {
            return
        }

        detach()
        internalCanvasView.frame = containerView.bounds
        containerView.addSubview(internalCanvasView)
    }

    actual fun detach() {
        internalCanvasView.removeFromSuperview()
    }

    actual fun setTool(tool: PencilKitTool) {
        internalCanvasView.tool = tool.toNativeTool()
    }

    actual fun usePenInk(red: Double, green: Double, blue: Double, alpha: Double, width: Double) {
        setTool(
            PencilKitTool.Ink(
                type = PencilInkType.Pen,
                color = PencilColor(red = red, green = green, blue = blue, alpha = alpha),
                width = width,
            ),
        )
    }

    actual fun usePencilInk(red: Double, green: Double, blue: Double, alpha: Double, width: Double) {
        setTool(
            PencilKitTool.Ink(
                type = PencilInkType.Pencil,
                color = PencilColor(red = red, green = green, blue = blue, alpha = alpha),
                width = width,
            ),
        )
    }

    actual fun useMarkerInk(red: Double, green: Double, blue: Double, alpha: Double, width: Double) {
        setTool(
            PencilKitTool.Ink(
                type = PencilInkType.Marker,
                color = PencilColor(red = red, green = green, blue = blue, alpha = alpha),
                width = width,
            ),
        )
    }

    actual fun useVectorEraser() {
        setTool(PencilKitTool.Eraser(PencilEraserType.Vector))
    }

    actual fun useBitmapEraser() {
        setTool(PencilKitTool.Eraser(PencilEraserType.Bitmap))
    }

    actual fun useLasso() {
        setTool(PencilKitTool.Lasso)
    }

    actual fun clear() {
        internalCanvasView.drawing = PKDrawing()
    }

    actual fun setRulerActive(active: Boolean) {
        internalCanvasView.rulerActive = active
    }

    actual fun setDrawingEnabled(enabled: Boolean) {
        internalCanvasView.userInteractionEnabled = enabled
    }

    actual fun exportDrawingData(): ByteArray {
        return internalCanvasView.drawing.dataRepresentation().toByteArray()
    }

    actual fun importDrawingData(data: ByteArray): Boolean {
        return memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            error.value = null
            val drawing = PKDrawing(data = data.toNSData(), error = error.ptr)
            if (drawing == null || error.value != null) {
                false
            } else {
                internalCanvasView.drawing = drawing
                true
            }
        }
    }
}

private fun PencilKitTool.toNativeTool(): PKTool {
    return when (this) {
        is PencilKitTool.Ink -> PKInkingTool(type.toNativeInkType(), color.toUIColor(), width)
        is PencilKitTool.Eraser -> PKEraserTool(type.toNativeEraserType())
        PencilKitTool.Lasso -> PKLassoTool()
    }
}

private fun PencilInkType.toNativeInkType(): String {
    return when (this) {
        PencilInkType.Pen -> PKInkTypePen
        PencilInkType.Pencil -> PKInkTypePencil
        PencilInkType.Marker -> PKInkTypeMarker
    }
}

private fun PencilEraserType.toNativeEraserType(): Long {
    return when (this) {
        PencilEraserType.Vector -> PKEraserTypeVector
        PencilEraserType.Bitmap -> PKEraserTypeBitmap
    }
}

private fun PencilColor.toUIColor(): UIColor {
    return UIColor.colorWithRed(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha,
    )
}

private fun NSData.toByteArray(): ByteArray {
    if (length == 0uL) {
        return ByteArray(0)
    }

    val result = ByteArray(length.toInt())
    result.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}

private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) {
        return NSData()
    }

    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}
