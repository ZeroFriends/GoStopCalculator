package zero.friends.gostopcalculator

import zero.friends.domain.usecase.calculate.audit.CalculationAuditReporter
import zero.friends.domain.usecase.calculate.audit.CalculationAuditReporterRegistry

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
 *     init() {
 *         // 추후 iOS Firebase Crashlytics 연동 시 아래 훅에 구현체를 등록:
 *         // IosAppBridgeKt.installCalculationAuditReporter(reporter: IosCrashlyticsCalculationAuditReporter())
 *     }
 *
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

fun installCalculationAuditReporter(reporter: CalculationAuditReporter) {
    CalculationAuditReporterRegistry.install(reporter)
}
