import SwiftUI
import UIKit
import SharedUI

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        if #available(iOS 26.0, *) {
            NativeNavContentView()
        } else {
            ComposeView()
                .ignoresSafeArea(.all)
        }
    }
}

@available(iOS 26.0, *)
struct RouteWrapper: Hashable, Identifiable {
    let id = UUID()
    let route: Route

    static func == (lhs: RouteWrapper, rhs: RouteWrapper) -> Bool {
        lhs.id == rhs.id
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}

@available(iOS 26.0, *)
final class IosNavigationCoordinator: ObservableObject {
    @Published var path: [RouteWrapper] = []

    func handle(_ command: NativeNavCommand) {
        DispatchQueue.main.async {
            let targetRoute = command.route
            if NativeNavigationKt.isHistoryRoute(route: targetRoute) {
                self.path.removeAll()
                return
            }

            switch command.popTarget {
            case .history:
                self.path.removeAll()
            case .boardMain:
                if let matchIndex = self.path.lastIndex(where: { wrapper in
                    NativeNavigationKt.isBoardMainRoute(route: wrapper.route)
                }) {
                    if NativeNavigationKt.isSameBoardMainRoute(
                        first: self.path[matchIndex].route,
                        second: targetRoute
                    ) {
                        let removeCount = self.path.count - matchIndex - 1
                        if removeCount > 0 {
                            self.path.removeLast(removeCount)
                        }
                        return
                    } else {
                        let targetCount = command.popInclusive ? matchIndex : matchIndex + 1
                        let removeCount = self.path.count - targetCount
                        if removeCount > 0 {
                            self.path.removeLast(removeCount)
                        }
                    }
                }
            default:
                break
            }

            self.path.append(RouteWrapper(route: targetRoute))
        }
    }

    func pop() {
        DispatchQueue.main.async {
            if !self.path.isEmpty {
                self.path.removeLast()
            }
        }
    }
}

@available(iOS 26.0, *)
struct NativeRootComposeView: UIViewControllerRepresentable {
    let coordinator: IosNavigationCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.RootViewController(
            onNavigate: { command in
                self.coordinator.handle(command)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@available(iOS 26.0, *)
final class NativeHostViewController: UIViewController, UIGestureRecognizerDelegate {
    private let contentViewController: UIViewController
    private let insetController: NativeInsetController
    private var swipeBackEnabled: Bool
    private var isRedBackground: Bool
    private var lastMeasuredInset: CGFloat = -1
    private weak var previousPopDelegate: UIGestureRecognizerDelegate?

    init(
        contentViewController: UIViewController,
        insetController: NativeInsetController,
        initialConfig: NativeTopBarConfig
    ) {
        self.contentViewController = contentViewController
        self.insetController = insetController
        self.swipeBackEnabled = initialConfig.swipeBackEnabled
        self.isRedBackground = initialConfig.isRedBackground
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        isRedBackground ? .lightContent : .darkContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = isRedBackground
            ? UIColor(red: 1.0, green: 82.0 / 255.0, blue: 82.0 / 255.0, alpha: 1.0)
            : .systemBackground

        addChild(contentViewController)
        contentViewController.view.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(contentViewController.view)
        NSLayoutConstraint.activate([
            contentViewController.view.topAnchor.constraint(equalTo: view.topAnchor),
            contentViewController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            contentViewController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            contentViewController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
        ])
        contentViewController.didMove(toParent: self)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        applyBarAppearance()
        updateTopInsetIfNeeded()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        configureInteractivePopGesture()
        applyBarAppearance()
        updateTopInsetIfNeeded()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if let popGesture = navigationController?.interactivePopGestureRecognizer,
           popGesture.delegate === self {
            popGesture.delegate = previousPopDelegate
            popGesture.isEnabled = true
        }
    }

    override func viewSafeAreaInsetsDidChange() {
        super.viewSafeAreaInsetsDidChange()
        updateTopInsetIfNeeded()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateTopInsetIfNeeded()
    }

    func apply(config: NativeTopBarConfig) {
        swipeBackEnabled = config.swipeBackEnabled
        if let popGesture = navigationController?.interactivePopGestureRecognizer,
           popGesture.delegate === self {
            popGesture.isEnabled = config.swipeBackEnabled
        }

        let styleChanged = isRedBackground != config.isRedBackground
        isRedBackground = config.isRedBackground
        if isViewLoaded {
            view.backgroundColor = isRedBackground
                ? UIColor(red: 1.0, green: 82.0 / 255.0, blue: 82.0 / 255.0, alpha: 1.0)
                : .systemBackground
        }
        if styleChanged {
            applyBarAppearance()
        }
    }

    private func configureInteractivePopGesture() {
        guard let popGesture = navigationController?.interactivePopGestureRecognizer else { return }
        if popGesture.delegate !== self {
            previousPopDelegate = popGesture.delegate
            popGesture.delegate = self
        }
        popGesture.isEnabled = swipeBackEnabled
    }

    private func applyBarAppearance() {
        navigationController?.navigationBar.barStyle = isRedBackground ? .black : .default
        setNeedsStatusBarAppearanceUpdate()
        navigationController?.setNeedsStatusBarAppearanceUpdate()
    }

    private func updateTopInsetIfNeeded() {
        let topInset: CGFloat
        if let navController = navigationController, !navController.isNavigationBarHidden {
            let navBar = navController.navigationBar
            if navBar.window != nil, view.window != nil {
                let navBarFrame = navBar.convert(navBar.bounds, to: view)
                let fallback = view.safeAreaInsets.top + 44
                topInset = navBarFrame.maxY > 0 ? navBarFrame.maxY : fallback
            } else {
                topInset = view.safeAreaInsets.top + 44
            }
        } else {
            topInset = view.safeAreaInsets.top
        }

        if abs(topInset - lastMeasuredInset) > 0.5 {
            lastMeasuredInset = topInset
            insetController.updateTopInset(points: Float(topInset))
        }
    }

    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        guard swipeBackEnabled, let nav = navigationController else {
            return false
        }
        return nav.viewControllers.count > 1 && nav.presentedViewController == nil
    }
}

@available(iOS 26.0, *)
struct DetailComposeView: UIViewControllerRepresentable {
    let route: Route
    let coordinator: IosNavigationCoordinator
    let topBarConfig: NativeTopBarConfig
    let onUpdateTopBar: (NativeTopBarConfig) -> Void

    func makeUIViewController(context: Context) -> NativeHostViewController {
        let insetController = NativeInsetController()
        let composeVC = MainViewControllerKt.ScreenViewController(
            route: route,
            insetController: insetController,
            onNavigate: { command in
                self.coordinator.handle(command)
            },
            onGoBack: {
                self.coordinator.pop()
            },
            onUpdateTopBar: { config in
                self.onUpdateTopBar(config)
            }
        )
        return NativeHostViewController(
            contentViewController: composeVC,
            insetController: insetController,
            initialConfig: topBarConfig
        )
    }

    func updateUIViewController(_ uiViewController: NativeHostViewController, context: Context) {
        uiViewController.apply(config: topBarConfig)
    }
}

@available(iOS 26.0, *)
struct NativeDestinationView: View {
    let route: Route
    @ObservedObject var coordinator: IosNavigationCoordinator
    @State private var topBarConfig: NativeTopBarConfig

    init(route: Route, coordinator: IosNavigationCoordinator) {
        self.route = route
        self.coordinator = coordinator
        _topBarConfig = State(initialValue: NativeNavigationKt.defaultNativeTopBarConfig(route: route))
    }

    var body: some View {
        let foregroundColor: Color = topBarConfig.isRedBackground ? .white : .black
        let accentActionColor: Color = topBarConfig.isRedBackground
            ? .white
            : Color(red: 1.0, green: 82.0 / 255.0, blue: 82.0 / 255.0)

        DetailComposeView(
            route: route,
            coordinator: coordinator,
            topBarConfig: topBarConfig,
            onUpdateTopBar: { newConfig in
                DispatchQueue.main.async {
                    self.topBarConfig = newConfig
                }
            }
        )
        .ignoresSafeArea(.all)
        .navigationTitle(topBarConfig.title)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .toolbarColorScheme(topBarConfig.isRedBackground ? .dark : .light, for: .navigationBar)
        .toolbarBackground(topBarConfig.isRedBackground ? .hidden : .automatic, for: .navigationBar)
        .toolbar {
            if topBarConfig.showBack {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: {
                        if topBarConfig.onBack != nil {
                            topBarConfig.invokeBack()
                        } else {
                            coordinator.pop()
                        }
                    }) {
                        Image(systemName: "chevron.backward")
                            .font(.system(size: 17, weight: .semibold))
                            .foregroundStyle(foregroundColor)
                    }
                    .accessibilityLabel("Back")
                    .accessibilityIdentifier("Back")
                }
            }

            ToolbarItem(placement: .principal) {
                Text(topBarConfig.title)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundStyle(foregroundColor)
                    .lineLimit(1)
            }

            if topBarConfig.actionType == .closeIcon {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: {
                        topBarConfig.invokeAction()
                    }) {
                        Image(systemName: "xmark")
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundStyle(foregroundColor)
                    }
                    .accessibilityLabel("Close")
                    .accessibilityIdentifier("Close")
                }
            } else if topBarConfig.actionType == .text, let actionText = topBarConfig.actionText {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: {
                        topBarConfig.invokeAction()
                    }) {
                        Text(actionText)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(accentActionColor)
                    }
                }
            }
        }
    }
}

@available(iOS 26.0, *)
struct NativeNavContentView: View {
    @StateObject private var coordinator = IosNavigationCoordinator()

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            NativeRootComposeView(coordinator: coordinator)
                .ignoresSafeArea(.all)
                .toolbar(.hidden, for: .navigationBar)
                .navigationDestination(for: RouteWrapper.self) { wrapper in
                    NativeDestinationView(route: wrapper.route, coordinator: coordinator)
                }
        }
    }
}
