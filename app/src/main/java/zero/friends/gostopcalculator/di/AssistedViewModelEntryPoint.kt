package zero.friends.gostopcalculator.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import zero.friends.gostopcalculator.ui.board.main.BoardViewModel
import zero.friends.gostopcalculator.ui.board.result.DetailViewModel

@EntryPoint
@InstallIn(ActivityComponent::class)
interface AssistedViewModelEntryPoint {
    fun boardFactory(): BoardViewModel.Factory
    fun detailFactory(): DetailViewModel.Factory
}

