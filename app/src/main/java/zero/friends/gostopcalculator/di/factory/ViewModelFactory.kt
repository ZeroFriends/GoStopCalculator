package zero.friends.gostopcalculator.di.factory

import zero.friends.gostopcalculator.ui.board.BoardViewModel
import zero.friends.gostopcalculator.ui.board.detail.DetailViewModel

@dagger.assisted.AssistedFactory
fun interface BoardViewModelFactory {
    fun createBoardViewModel(gameId: Long): BoardViewModel
}

@dagger.assisted.AssistedFactory
fun interface DetailViewModelFactory {
    fun createDetailViewModel(gameId: Long): DetailViewModel
}