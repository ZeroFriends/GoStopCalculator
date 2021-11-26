package zero.friends.gostopcalculator.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.EntryPointAccessors

@Composable
inline fun <reified T> getEntryPointFromActivity(): T {
    return EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        T::class.java
    )
}