package zero.friends.gostopcalculator

/**
 * iOS Swift Application Integration Reference:
 *
 * In your iOS Xcode project (SwiftUI App):
 * ```swift
 * import SwiftUI
 * import SharedUI
 *
 * struct ComposeView: UIViewControllerRepresentable {
 *     func makeUIViewController(context: Context) -> UIViewController {
 *         return MainViewControllerKt.MainViewController()
 *     }
 *
 *     func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
 * }
 *
 * @main
 * struct iOSApp: App {
 *     var body: some Scene {
 *         WindowGroup {
 *             ComposeView()
 *                 .ignoresSafeArea(.all)
 *         }
 *     }
 * }
 * ```
 */
object IosAppBridge
