package zero.friends.gostopcalculator.di.entrypoint

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import zero.friends.gostopcalculator.di.factory.BoardViewModelFactory
import zero.friends.gostopcalculator.di.factory.PrePareViewModelFactory
import zero.friends.gostopcalculator.di.factory.SellingViewModelFactory

@EntryPoint
@InstallIn(ActivityComponent::class)
interface EntryPoint {
    fun boardFactory(): BoardViewModelFactory
    fun prepareFactory(): PrePareViewModelFactory
    fun sellingFactory(): SellingViewModelFactory
}

