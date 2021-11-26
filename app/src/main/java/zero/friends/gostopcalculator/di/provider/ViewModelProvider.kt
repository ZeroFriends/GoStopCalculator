package zero.friends.gostopcalculator.di.provider

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import zero.friends.gostopcalculator.di.factory.ViewModelFactory

object ViewModelProvider {
    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface FactoryEntryPoint {
        fun boardFactory(): ViewModelFactory.BoardViewModelFactory
    }
}

