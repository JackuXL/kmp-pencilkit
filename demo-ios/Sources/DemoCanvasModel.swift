import Foundation
import Combine
import PencilKitBridge

enum DemoTool: String, CaseIterable, Identifiable {
    case pen = "Pen"
    case pencil = "Pencil"
    case marker = "Marker"
    case vectorEraser = "Vector Eraser"
    case bitmapEraser = "Bitmap Eraser"
    case lasso = "Lasso"

    var id: String { rawValue }
}

final class DemoCanvasModel: ObservableObject {
    let bridge = PencilKitBridge()
    private(set) var activeTool: DemoTool = .pencil
    private let demoColor = (red: 0.12, green: 0.35, blue: 0.92, alpha: 1.0)

    func activate(tool: DemoTool, width: Double) {
        activeTool = tool
        switch tool {
        case .pen:
            bridge.usePenInk(
                red: demoColor.red,
                green: demoColor.green,
                blue: demoColor.blue,
                alpha: demoColor.alpha,
                width: width
            )
        case .pencil:
            bridge.usePencilInk(
                red: demoColor.red,
                green: demoColor.green,
                blue: demoColor.blue,
                alpha: demoColor.alpha,
                width: width
            )
        case .marker:
            bridge.useMarkerInk(
                red: demoColor.red,
                green: demoColor.green,
                blue: demoColor.blue,
                alpha: demoColor.alpha,
                width: width
            )
        case .vectorEraser:
            bridge.useVectorEraser()
        case .bitmapEraser:
            bridge.useBitmapEraser()
        case .lasso:
            bridge.useLasso()
        }
    }

    func updateWidth(_ width: Double) {
        switch activeTool {
        case .pen, .pencil, .marker:
            activate(tool: activeTool, width: width)
        case .vectorEraser, .bitmapEraser, .lasso:
            break
        }
    }

    func setRulerActive(_ enabled: Bool) {
        bridge.setRulerActive(active: enabled)
    }

    func setDrawingEnabled(_ enabled: Bool) {
        bridge.setDrawingEnabled(enabled: enabled)
    }

    func clear() {
        bridge.clear()
    }
}
