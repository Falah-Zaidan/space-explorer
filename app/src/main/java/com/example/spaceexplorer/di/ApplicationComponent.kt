package com.example.spaceexplorer.di

import android.app.Application
import com.example.spaceexplorer.SpaceExplorerApp
import com.example.spaceexplorer.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class
    ]
)
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(SpaceExplorerApp: SpaceExplorerApp)
}