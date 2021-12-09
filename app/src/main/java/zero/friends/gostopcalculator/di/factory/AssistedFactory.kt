package zero.friends.gostopcalculator.di.factory

import zero.friends.gostopcalculator.ui.board.BoardViewModel

@dagger.assisted.AssistedFactory
interface AssistedFactory {
    fun create(gameId: Long): BoardViewModel
}