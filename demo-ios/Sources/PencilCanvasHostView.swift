import SwiftUI
import UIKit
import PencilKitBridge

struct PencilCanvasHostView: UIViewRepresentable {
    let bridge: PencilKitBridge

    func makeUIView(context: Context) -> UIView {
        let container = UIView()
        container.backgroundColor = .systemBackground
        bridge.attachTo(containerView: container)
        return container
    }

    func updateUIView(_ uiView: UIView, context: Context) {
        bridge.attachTo(containerView: uiView)
    }
}
