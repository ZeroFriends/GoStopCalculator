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
            NativeNavigationContainer()
                .ignoresSafeArea(.all)
        } else {
            ComposeView()
                .ignoresSafeArea(.all)
        }
    }
}

@available(iOS 26.0, *)
struct NativeNavigationContainer: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> NativeNavigationController {
        NativeNavigationController()
    }

    func updateUIViewController(_ uiViewController: NativeNavigationController, context: Context) {}
}

@available(iOS 26.0, *)
final class NativeRootHostViewController: UIViewController {
    private let contentViewController: UIViewController

    init(onNavigate: @escaping (NativeNavCommand) -> Void) {
        self.contentViewController = MainViewControllerKt.RootViewController(onNavigate: onNavigate)
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        .darkContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        overrideUserInterfaceStyle = .light
        view.backgroundColor = .systemBackground

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
}

@available(iOS 26.0, *)
final class NativeHostViewController: UIViewController {
    let route: Route
    private let contentViewController: UIViewController
    private let insetController: NativeInsetController
    private weak var navController: NativeNavigationController?
    private var config: NativeTopBarConfig
    private var lastMeasuredInset: CGFloat = -1
    private var barItemsInitialized = false
    private var lastIsRedBackground: Bool
    private var lastShowBack: Bool
    private var lastActionType: NativeTopBarActionType
    private var lastActionText: String?

    private let titleLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: 16, weight: .semibold)
        label.textAlignment = .center
        label.lineBreakMode = .byTruncatingTail
        label.accessibilityTraits = .header
        return label
    }()

    var swipeBackEnabled: Bool {
        config.swipeBackEnabled
    }

    init(
        route: Route,
        navController: NativeNavigationController,
        initialConfig: NativeTopBarConfig
    ) {
        self.route = route
        self.navController = navController
        self.config = initialConfig
        self.lastIsRedBackground = initialConfig.isRedBackground
        self.lastShowBack = initialConfig.showBack
        self.lastActionType = initialConfig.actionType
        self.lastActionText = initialConfig.actionText

        let insetController = NativeInsetController()
        self.insetController = insetController

        weak var weakNav = navController
        weak var weakSelf: NativeHostViewController?
        let composeVC = MainViewControllerKt.ScreenViewController(
            route: route,
            insetController: insetController,
            onNavigate: { command in
                weakNav?.handle(command)
            },
            onGoBack: {
                weakNav?.pop()
            },
            onUpdateTopBar: { newConfig in
                DispatchQueue.main.async {
                    weakSelf?.apply(config: newConfig)
                }
            }
        )
        self.contentViewController = composeVC
        super.init(nibName: nil, bundle: nil)
        weakSelf = self
        self.navigationItem.titleView = titleLabel
        self.navigationItem.hidesBackButton = true
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        config.isRedBackground ? .lightContent : .darkContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        overrideUserInterfaceStyle = .light

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

        apply(config: config)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        applyBarAppearance()
        updateTopInsetIfNeeded()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        applyBarAppearance()
        updateTopInsetIfNeeded()
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
        let appearanceChanged = !barItemsInitialized || lastIsRedBackground != config.isRedBackground
        let leftChanged = !barItemsInitialized || lastShowBack != config.showBack || lastIsRedBackground != config.isRedBackground
        let rightChanged = !barItemsInitialized
            || lastIsRedBackground != config.isRedBackground
            || lastActionType != config.actionType
            || lastActionText != config.actionText
        let titleChanged = !barItemsInitialized || self.config.title != config.title

        self.config = config
        self.lastIsRedBackground = config.isRedBackground
        self.lastShowBack = config.showBack
        self.lastActionType = config.actionType
        self.lastActionText = config.actionText
        self.barItemsInitialized = true

        let foregroundColor: UIColor = config.isRedBackground ? .white : .black
        let accentActionColor: UIColor = config.isRedBackground
            ? .white
            : UIColor(red: 1.0, green: 82.0 / 255.0, blue: 82.0 / 255.0, alpha: 1.0)

        if isViewLoaded {
            view.backgroundColor = config.isRedBackground
                ? UIColor(red: 1.0, green: 82.0 / 255.0, blue: 82.0 / 255.0, alpha: 1.0)
                : .systemBackground
        }

        if titleChanged || appearanceChanged {
            titleLabel.text = config.title
            titleLabel.textColor = foregroundColor
            titleLabel.accessibilityLabel = config.title
            titleLabel.accessibilityIdentifier = config.title
            titleLabel.sizeToFit()
            titleLabel.invalidateIntrinsicContentSize()
            if let navBar = navigationController?.navigationBar, navigationController?.topViewController === self {
                navBar.setNeedsLayout()
            }
        }

        if leftChanged {
            if config.showBack {
                let backItem = UIBarButtonItem(
                    image: UIImage(systemName: "chevron.backward"),
                    style: .plain,
                    target: self,
                    action: #selector(handleBackTap)
                )
                backItem.tintColor = foregroundColor
                backItem.accessibilityLabel = "Back"
                backItem.accessibilityIdentifier = "Back"
                navigationItem.leftBarButtonItem = backItem
            } else {
                navigationItem.leftBarButtonItem = nil
            }
        }

        if rightChanged {
            switch config.actionType {
            case .closeIcon:
                let closeItem = UIBarButtonItem(
                    image: UIImage(systemName: "xmark"),
                    style: .plain,
                    target: self,
                    action: #selector(handleActionTap)
                )
                closeItem.tintColor = foregroundColor
                closeItem.accessibilityLabel = "Close"
                closeItem.accessibilityIdentifier = "Close"
                navigationItem.rightBarButtonItem = closeItem
            case .text:
                if let actionText = config.actionText {
                    let actionItem = UIBarButtonItem(
                        title: actionText,
                        style: .plain,
                        target: self,
                        action: #selector(handleActionTap)
                    )
                    actionItem.tintColor = accentActionColor
                    actionItem.accessibilityLabel = actionText
                    let attrs: [NSAttributedString.Key: Any] = [
                        .font: UIFont.systemFont(ofSize: 14, weight: .bold),
                        .foregroundColor: accentActionColor,
                    ]
                    actionItem.setTitleTextAttributes(attrs, for: .normal)
                    actionItem.setTitleTextAttributes(attrs, for: .highlighted)
                    navigationItem.rightBarButtonItem = actionItem
                } else {
                    navigationItem.rightBarButtonItem = nil
                }
            default:
                navigationItem.rightBarButtonItem = nil
            }
        }

        if appearanceChanged {
            let appearance = UINavigationBarAppearance()
            if config.isRedBackground {
                appearance.configureWithTransparentBackground()
                appearance.backgroundColor = .clear
                appearance.shadowColor = .clear
                appearance.titleTextAttributes = [.foregroundColor: UIColor.white]
            } else {
                appearance.configureWithDefaultBackground()
                appearance.titleTextAttributes = [.foregroundColor: UIColor.black]
            }
            navigationItem.standardAppearance = appearance
            navigationItem.scrollEdgeAppearance = appearance
            navigationItem.compactAppearance = appearance

            if navigationController?.topViewController === self {
                applyBarAppearance()
            }
        }
    }

    private func applyBarAppearance() {
        navigationController?.navigationBar.barStyle = config.isRedBackground ? .black : .default
        setNeedsStatusBarAppearanceUpdate()
        navigationController?.setNeedsStatusBarAppearanceUpdate()
    }

    private func updateTopInsetIfNeeded() {
        guard isViewLoaded else { return }
        let topInset: CGFloat
        if let navController = navigationController, !navController.isNavigationBarHidden {
            let navBar = navController.navigationBar
            if navBar.window != nil, view.window != nil {
                let navBarFrame = navBar.convert(navBar.bounds, to: view)
                let fallback = view.safeAreaInsets.top > 0 ? view.safeAreaInsets.top : 44
                topInset = navBarFrame.maxY > 0 ? navBarFrame.maxY : fallback
            } else {
                topInset = view.safeAreaInsets.top > 0 ? view.safeAreaInsets.top : 44
            }
        } else {
            topInset = view.safeAreaInsets.top
        }

        if abs(topInset - lastMeasuredInset) > 0.5 {
            lastMeasuredInset = topInset
            insetController.updateTopInset(points: Float(topInset))
        }
    }

    @objc private func handleBackTap() {
        if config.onBack != nil {
            config.invokeBack()
        } else {
            navController?.pop()
        }
    }

    @objc private func handleActionTap() {
        config.invokeAction()
    }
}

@available(iOS 26.0, *)
final class NativeNavigationController: UINavigationController, UINavigationControllerDelegate, UIGestureRecognizerDelegate {
    private lazy var rootHostVC = NativeRootHostViewController { [weak self] command in
        self?.handle(command)
    }

    override var childForStatusBarStyle: UIViewController? {
        topViewController
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        overrideUserInterfaceStyle = .light
        delegate = self
        interactivePopGestureRecognizer?.delegate = self
        interactivePopGestureRecognizer?.isEnabled = true
        setViewControllers([rootHostVC], animated: false)
    }

    func handle(_ command: NativeNavCommand) {
        DispatchQueue.main.async {
            let targetRoute = command.route
            if NativeNavigationKt.isHistoryRoute(route: targetRoute) {
                self.popToRootViewController(animated: true)
                return
            }

            let current = self.viewControllers
            var newControllers = current

            switch command.popTarget {
            case .history:
                newControllers = [self.rootHostVC]
            case .boardMain:
                if let matchIndex = current.lastIndex(where: { vc in
                    guard let hostVC = vc as? NativeHostViewController else { return false }
                    return NativeNavigationKt.isBoardMainRoute(route: hostVC.route)
                }) {
                    let matchVC = current[matchIndex] as! NativeHostViewController
                    if NativeNavigationKt.isSameBoardMainRoute(first: matchVC.route, second: targetRoute) {
                        self.popToViewController(matchVC, animated: true)
                        return
                    }
                    let keepCount = command.popInclusive ? matchIndex : matchIndex + 1
                    newControllers = Array(current.prefix(keepCount))
                }
            default:
                break
            }

            let initialConfig = NativeNavigationKt.defaultNativeTopBarConfig(route: targetRoute)
            let destVC = NativeHostViewController(
                route: targetRoute,
                navController: self,
                initialConfig: initialConfig
            )

            if newControllers.count == current.count {
                self.pushViewController(destVC, animated: true)
            } else {
                newControllers.append(destVC)
                self.setViewControllers(newControllers, animated: true)
            }
        }
    }

    func pop() {
        DispatchQueue.main.async {
            if self.viewControllers.count > 1 {
                self.popViewController(animated: true)
            }
        }
    }

    func navigationController(
        _ navigationController: UINavigationController,
        willShow viewController: UIViewController,
        animated: Bool
    ) {
        let isRoot = (viewController === rootHostVC)
        setNavigationBarHidden(isRoot, animated: animated)
        if isRoot {
            navigationBar.barStyle = .default
            setNeedsStatusBarAppearanceUpdate()
        }
    }

    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        guard viewControllers.count > 1,
              presentedViewController == nil,
              topViewController?.presentedViewController == nil else {
            return false
        }
        if let hostVC = topViewController as? NativeHostViewController {
            return hostVC.swipeBackEnabled
        }
        return true
    }
}
