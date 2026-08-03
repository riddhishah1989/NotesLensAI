package com.portfolio.notelensai.di

import android.content.Context
import com.portfolio.notelensai.BuildConfig
import com.portfolio.notelensai.data.ai.DemoSummaryRepository
import com.portfolio.notelensai.data.ai.FirebaseSummaryRepository
import com.portfolio.notelensai.data.mlkit.MlKitTextRecognitionRepository
import com.portfolio.notelensai.domain.repository.SummaryRepository
import com.portfolio.notelensai.domain.repository.TextRecognitionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DemoMode

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTextRecognitionRepository(
        @ApplicationContext context: Context,
    ): TextRecognitionRepository {
        return MlKitTextRecognitionRepository(
            context = context,
        )
    }

    @Provides
    @Singleton
    fun provideSummaryRepository(): SummaryRepository {
        return if (BuildConfig.FIREBASE_CONFIGURED) {
            FirebaseSummaryRepository(
                modelName = BuildConfig.GEMINI_MODEL_NAME,
            )
        } else {
            DemoSummaryRepository()
        }
    }

    @Provides
    @DemoMode
    fun provideDemoMode(): Boolean {
        return !BuildConfig.FIREBASE_CONFIGURED
    }
}