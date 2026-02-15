package io.github.jackuxl.pencilkit

/**
 * Unified tool definitions used by common KMP code.
 */
sealed interface PencilKitTool {
    data class Ink(
        val type: PencilInkType = PencilInkType.Pencil,
        val color: PencilColor = PencilColor.Black,
        val width: Double = 6.0,
    ) : PencilKitTool

    data class Eraser(
        val type: PencilEraserType = PencilEraserType.Vector,
    ) : PencilKitTool

    data object Lasso : PencilKitTool
}

enum class PencilInkType {
    Pen,
    Pencil,
    Marker,
}

enum class PencilEraserType {
    Vector,
    Bitmap,
}

data class PencilColor(
    val red: Double,
    val green: Double,
    val blue: Double,
    val alpha: Double = 1.0,
) {
    init {
        require(red in 0.0..1.0) { "red must be between 0.0 and 1.0" }
        require(green in 0.0..1.0) { "green must be between 0.0 and 1.0" }
        require(blue in 0.0..1.0) { "blue must be between 0.0 and 1.0" }
        require(alpha in 0.0..1.0) { "alpha must be between 0.0 and 1.0" }
    }

    companion object {
        val Black = PencilColor(0.0, 0.0, 0.0, 1.0)
        val White = PencilColor(1.0, 1.0, 1.0, 1.0)
    }
}

/**
 * Type-erased native view used as an attachment point for [PencilKitBridge].
 */
expect class PencilKitContainerView

/**
 * iPadOS PencilKit bridge facade for common code.
 */
expect class PencilKitBridge() {
    val canvasView: PencilKitContainerView

    fun attachTo(containerView: PencilKitContainerView)

    fun detach()

    fun setTool(tool: PencilKitTool)

    fun usePenInk(
        red: Double,
        green: Double,
        blue: Double,
        alpha: Double,
        width: Double,
    )

    fun usePencilInk(
        red: Double,
        green: Double,
        blue: Double,
        alpha: Double,
        width: Double,
    )

    fun useMarkerInk(
        red: Double,
        green: Double,
        blue: Double,
        alpha: Double,
        width: Double,
    )

    fun useVectorEraser()

    fun useBitmapEraser()

    fun useLasso()

    fun clear()

    fun setRulerActive(active: Boolean)

    fun setDrawingEnabled(enabled: Boolean)

    fun exportDrawingData(): ByteArray

    fun importDrawingData(data: ByteArray): Boolean
}
