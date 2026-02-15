import SwiftUI
import UIKit
import PencilKitBridge

private final class CanvasHostContainerView: UIView {
    let backgroundImageView = UIImageView()
    let canvasContainer = UIView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .secondarySystemBackground

        backgroundImageView.contentMode = .scaleAspectFit
        backgroundImageView.clipsToBounds = true
        backgroundImageView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        backgroundImageView.frame = bounds
        addSubview(backgroundImageView)

        canvasContainer.backgroundColor = .clear
        canvasContainer.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        canvasContainer.frame = bounds
        addSubview(canvasContainer)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setBackgroundImage(_ image: UIImage?) {
        backgroundImageView.image = image
        backgroundColor = image == nil ? .secondarySystemBackground : .black
    }
}

struct PencilCanvasHostView: UIViewRepresentable {
    let bridge: PencilKitBridge
    let backgroundImage: UIImage?

    func makeUIView(context: Context) -> CanvasHostContainerView {
        let container = CanvasHostContainerView()
        bridge.attachTo(containerView: container.canvasContainer)
        bridge.canvasView.backgroundColor = .clear
        bridge.canvasView.isOpaque = false
        container.setBackgroundImage(backgroundImage)
        return container
    }

    func updateUIView(_ uiView: CanvasHostContainerView, context: Context) {
        uiView.setBackgroundImage(backgroundImage)
        bridge.attachTo(containerView: uiView.canvasContainer)
        bridge.canvasView.backgroundColor = .clear
        bridge.canvasView.isOpaque = false
    }
}
