package zero.friends.gostopcalculator.di.entrypoint

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import zero.friends.gostopcalculator.di.factory.BoardViewModelFactory
import zero.friends.gostopcalculator.di.factory.DetailViewModelFactory

@EntryPoint
@InstallIn(ActivityComponent::class)
interface EntryPoint {
    fun boardFactory(): BoardViewModelFactory
    fun detailFactory(): DetailViewModelFactory
}

