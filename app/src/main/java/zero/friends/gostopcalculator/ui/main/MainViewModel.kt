package zero.friends.gostopcalculator.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import zero.friends.gostopcalculator.model.Game
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    fun getGameHistory(): List<Game> {
        return emptyList()
    }

}