package zero.friends.gostopcalculator.di.factory

import zero.friends.gostopcalculator.ui.board.BoardViewModel

@dagger.assisted.AssistedFactory
fun interface BoardViewModelFactory {
    fun createBoardViewModel(gameId: Long): BoardViewModel
}
