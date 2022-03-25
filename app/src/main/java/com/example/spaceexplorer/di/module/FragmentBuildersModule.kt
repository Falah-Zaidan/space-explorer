package com.example.spaceexplorer.di.module

import com.example.spaceexplorer.ui.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeListFragment(): RoverImageFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailFragment(): RoverImageDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeApodFragment(): ApodFragment

    @ContributesAndroidInjector
    abstract fun contributeFavouriteFragment(): FavouriteFragment

    @ContributesAndroidInjector
    abstract fun contributeCommentFragment(): CommentFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateCommentFragment(): CreateCommentFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    abstract fun contributeSpiritFragment(): SpiritRoverFragment

    @ContributesAndroidInjector
    abstract fun contributeOpportunityFragment(): Opp

    @ContributesAndroidInjector
    abstract fun contributeCuriosityFragment(): CuriosityRoverFragment
}

