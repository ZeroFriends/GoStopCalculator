package zero.friends.gostopcalculator

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetWorkChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mainScope = CoroutineScope(Dispatchers.Main)

    operator fun invoke(onLost: () -> Unit, onAvailable: () -> Unit) {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                mainScope.launch { onLost() }
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                mainScope.launch { onAvailable() }
            }
        })
    }
}
