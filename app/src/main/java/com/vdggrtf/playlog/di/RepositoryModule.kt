package com.vdggrtf.playlog.di

import com.vdggrtf.playlog.data.repositoryimpl.AiRepositoryImpl
import com.vdggrtf.playlog.data.repositoryimpl.AuthRepositoryImpl
import com.vdggrtf.playlog.data.repositoryimpl.ChallengeRepositoryImpl
import com.vdggrtf.playlog.data.repositoryimpl.GameRepositoryImpl
import com.vdggrtf.playlog.data.repositoryimpl.LibraryRepositoryImpl
import com.vdggrtf.playlog.data.repositoryimpl.PlaylistRepositoryImpl
import com.vdggrtf.playlog.data.repositoryimpl.RetroAchievementsRepositoryImpl
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.AuthRepository
import com.vdggrtf.playlog.domain.repository.ChallengeRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import com.vdggrtf.playlog.domain.repository.PlaylistRepository
import com.vdggrtf.playlog.domain.repository.RetroAchievementsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        repositoryImpl: GameRepositoryImpl,
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindLibraryRepository(
        repositoryImpl: LibraryRepositoryImpl,
    ): LibraryRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        repositoryImpl: AiRepositoryImpl,
    ): AiRepository

    @Binds
    @Singleton
    abstract fun bindChallengeRepository(
        repositoryImpl: ChallengeRepositoryImpl,
    ): ChallengeRepository

    @Binds
    @Singleton
    abstract fun bindRetroAchievementsRepository(
        repositoryImpl: RetroAchievementsRepositoryImpl,
    ): RetroAchievementsRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(
        impl: PlaylistRepositoryImpl
    ): PlaylistRepository
}