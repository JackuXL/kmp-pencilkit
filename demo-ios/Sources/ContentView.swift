import Foundation
import SwiftUI
import PhotosUI
import UIKit

struct ContentView: View {
    @StateObject private var model = DemoCanvasModel()
    @State private var selectedTool: DemoTool = .pencil
    @State private var strokeWidth: Double = 6.0
    @State private var drawingEnabled = true
    @State private var rulerEnabled = false
    @State private var selectedPhotoItem: PhotosPickerItem?
    @State private var backgroundImage: UIImage?

    var body: some View {
        VStack(spacing: 12) {
            PencilCanvasHostView(bridge: model.bridge, backgroundImage: backgroundImage)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .stroke(Color.secondary.opacity(0.4), lineWidth: 1)
                )
                .padding(.horizontal, 12)
                .frame(maxHeight: .infinity)

            controls
                .padding(.horizontal, 12)
                .padding(.bottom, 12)
        }
        .navigationTitle("PencilKit Demo")
        .onAppear {
            model.activate(tool: selectedTool, width: strokeWidth)
            model.setDrawingEnabled(drawingEnabled)
            model.setRulerActive(rulerEnabled)
        }
        .onChange(of: selectedPhotoItem) { item in
            Task {
                await loadSelectedBackgroundImage(from: item)
            }
        }
    }

    private var controls: some View {
        VStack(alignment: .leading, spacing: 10) {
            Picker("Tool", selection: $selectedTool) {
                ForEach(DemoTool.allCases) { tool in
                    Text(tool.rawValue).tag(tool)
                }
            }
            .pickerStyle(.segmented)
            .onChange(of: selectedTool) { tool in
                model.activate(tool: tool, width: strokeWidth)
            }

            HStack(spacing: 12) {
                Text("Width \(Int(strokeWidth))")
                    .font(.subheadline)
                    .frame(width: 90, alignment: .leading)
                Slider(value: $strokeWidth, in: 1...24, step: 1)
                    .onChange(of: strokeWidth) { width in
                        model.updateWidth(width)
                    }
            }

            HStack(spacing: 16) {
                Toggle("Drawing", isOn: $drawingEnabled)
                    .onChange(of: drawingEnabled) { enabled in
                        model.setDrawingEnabled(enabled)
                    }
                Toggle("Ruler", isOn: $rulerEnabled)
                    .onChange(of: rulerEnabled) { enabled in
                        model.setRulerActive(enabled)
                    }
            }

            HStack {
                PhotosPicker(selection: $selectedPhotoItem, matching: .images) {
                    Label("Select Background", systemImage: "photo")
                }
                .buttonStyle(.bordered)

                if backgroundImage != nil {
                    Button("Remove Background") {
                        backgroundImage = nil
                        selectedPhotoItem = nil
                    }
                    .buttonStyle(.bordered)
                }

                Spacer()
                Button("Clear Canvas") {
                    model.clear()
                }
                .buttonStyle(.borderedProminent)
            }
        }
    }

    @MainActor
    private func loadSelectedBackgroundImage(from item: PhotosPickerItem?) async {
        guard let item else {
            return
        }

        guard let imageData = try? await item.loadTransferable(type: Data.self),
              let image = UIImage(data: imageData) else {
            return
        }

        backgroundImage = image
    }
}

#Preview {
    ContentView()
}
