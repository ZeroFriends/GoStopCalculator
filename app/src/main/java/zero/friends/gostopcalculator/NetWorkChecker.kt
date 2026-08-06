package zero.friends.gostopcalculator

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NetWorkChecker(
    private val context: Context
) {
    private val mainScope = CoroutineScope(Dispatchers.Main)

    fun isCurrentlyConnected(): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    operator fun invoke(onConnectChanged: (Boolean) -> Unit) {
        val isConnected = isCurrentlyConnected()
        onConnectChanged(isConnected)

        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) ?: return
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                mainScope.launch { onConnectChanged(false) }
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                mainScope.launch { onConnectChanged(true) }
            }
        })
    }
}
