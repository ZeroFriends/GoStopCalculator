package zero.friends.gostopcalculator.di.factory

import zero.friends.gostopcalculator.ui.board.BoardViewModel

object ViewModelFactory {
    @dagger.assisted.AssistedFactory
    interface BoardViewModelFactory {
        fun create(gameId: Long): BoardViewModel
    }
}