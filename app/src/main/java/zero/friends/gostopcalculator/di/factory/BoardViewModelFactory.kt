package zero.friends.gostopcalculator.di.factory

import zero.friends.gostopcalculator.ui.board.BoardViewModel
import zero.friends.gostopcalculator.ui.board.prepare.PrepareViewModel
import zero.friends.gostopcalculator.ui.board.selling.SellingViewModel

@dagger.assisted.AssistedFactory
fun interface BoardViewModelFactory {
    fun createBoardViewModel(gameId: Long): BoardViewModel
}

@dagger.assisted.AssistedFactory
fun interface PrePareViewModelFactory {
    fun createPrepareViewModel(gameId: Long): PrepareViewModel
}

@dagger.assisted.AssistedFactory
fun interface SellingViewModelFactory {
    fun createSellingViewModel(roundId: Long): SellingViewModel
}