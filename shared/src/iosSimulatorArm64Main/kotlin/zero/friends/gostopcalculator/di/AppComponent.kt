package zero.friends.gostopcalculator.di

import zero.friends.data.di.DataComponent
import zero.friends.data.di.create

actual fun createAppComponent(): AppComponent = AppComponent::class.create(DataComponent::class.create())
