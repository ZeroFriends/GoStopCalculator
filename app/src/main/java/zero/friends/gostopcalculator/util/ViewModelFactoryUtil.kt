package zero.friends.gostopcalculator.util

import androidx.lifecycle.ViewModel

fun viewModelFactory(construct: (Class<out ViewModel>) -> ViewModel) =
    object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            construct(modelClass) as T
    }