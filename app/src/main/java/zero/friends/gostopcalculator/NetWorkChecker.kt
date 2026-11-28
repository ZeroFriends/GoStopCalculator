package zero.friends.gostopcalculator

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import zero.friends.gostopcalculator.util.log
import java.util.concurrent.atomic.AtomicBoolean

@Inject
class NetWorkChecker(
    private val context: Context
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.e("NetWorkChecker callback failed: ${throwable.message}")
    }
    private val mainScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob() + exceptionHandler)

    private fun NetworkCapabilities?.isValidatedInternet(): Boolean {
        return this != null &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun isCurrentlyConnected(): Boolean {
        return runCatching {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java) ?: return false
            val network = connectivityManager.activeNetwork ?: return false
            connectivityManager.getNetworkCapabilities(network).isValidatedInternet()
        }.getOrElse { error ->
            log.e("Failed to check network connectivity: ${error.message}")
            false
        }
    }

    operator fun invoke(onConnectChanged: (Boolean) -> Unit) {
        val initialConnected = isCurrentlyConnected()
        val lastEmittedConnected = AtomicBoolean(initialConnected)
        onConnectChanged(initialConnected)

        val connectivityManager = runCatching {
            context.getSystemService(ConnectivityManager::class.java)
        }.getOrNull() ?: return

        fun emitIfChanged(next: Boolean) {
            if (lastEmittedConnected.compareAndSet(!next, next)) {
                mainScope.launch { onConnectChanged(next) }
            }
        }

        runCatching {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    emitIfChanged(isCurrentlyConnected())
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    emitIfChanged(isCurrentlyConnected())
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    emitIfChanged(networkCapabilities.isValidatedInternet())
                }
            })
            emitIfChanged(isCurrentlyConnected())
        }.onFailure { error ->
            log.e("Failed to register network callback: ${error.message}")
        }
    }
}
