package com.example.spaceexplorer.di.module

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.spaceexplorer.BuildConfig
import com.example.spaceexplorer.MainActivity
import com.example.spaceexplorer.cache.dao.FavouriteDao
import com.example.spaceexplorer.cache.dao.LoginDao
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.db.NASADatabase
import com.example.spaceexplorer.di.viewmodel.ViewModelFactory
import com.example.spaceexplorer.di.viewmodel.ViewModelKey
import com.example.spaceexplorer.remote.*
import com.example.spaceexplorer.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @IntoMap
    @ViewModelKey(ListViewModel::class)
    abstract fun bindListViewModel(listViewModel: ListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavouriteViewModel::class)
    abstract fun bindFavouritesViewModel(favouriteViewModel: FavouriteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindRegisterViewModel(registerViewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditorsPickViewModel::class)
    abstract fun bindEditorsPickViewModel(editorsPickViewModel: EditorsPickViewModel): ViewModel

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @Binds
    abstract fun bindContext(application: Application): Context

    @Module
    companion object {

        @Singleton
        @Provides
        @JvmStatic
        fun providePhotoDatabase(application: Application): NASADatabase {
            return Room.databaseBuilder(
                application.applicationContext,
                NASADatabase::class.java, "photos.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Singleton
        @Provides
        @JvmStatic
        fun providePhotoService(): PhotoService {
            return ServiceFactory.makePhotoService(BuildConfig.DEBUG)
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideFavouriteService(): DjangoService {
            return ServiceFactory.makeFavouriteService(BuildConfig.DEBUG)
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideLoginService(): LoginService {
            return ServiceFactory.makeLoginService(BuildConfig.DEBUG)
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideRegisterService(): RegisterService {
            return ServiceFactory.makeRegisterService(BuildConfig.DEBUG)
        }

        @Singleton
        @Provides
        @JvmStatic
        fun providePhotoDao(db: NASADatabase): PhotoDao {
            return db.photoDao()
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideFavouriteDao(db: NASADatabase): FavouriteDao {
            return db.favouriteDao()
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideLoginDao(db: NASADatabase): LoginDao {
            return db.loginDao()
        }
    }

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}

