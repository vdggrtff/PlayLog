package com.vdggrtf.playlog.domain.usecase.main.ai

import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScanAndImportLibraryUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val gamesRepository: GameRepository,
    private val libraryRepository: LibraryRepository,
) {
    operator fun invoke(imageBytes: ByteArray): Flow<String> = flow {
        try {
            emit("✨ AI is reading the screenshot...")

            val gameNames = aiRepository.scanLibraryForGames(imageBytes)

            if (gameNames.isEmpty()) {
                emit("❌ games not fond in screenshot")
                return@flow
            }

            emit("🔎 Searching for ${gameNames.size} games in RAWG database...")

            // Find this games in RAWG parallel
            val gamesToSave = coroutineScope {
                gameNames.map { name ->
                    async {
                        // Searching for the game and taking the first relevant result.
                        gamesRepository.searchGames(name, 1).getOrNull()?.firstOrNull()
                    }
                }.awaitAll().filterNotNull()
            }

            if (gamesToSave.isEmpty()) {
                emit("❌ Games are recognized but not found in the RAWG database.")
                return@flow
            }

            emit("💾 Saving to your library...")

            gamesToSave.forEach { game ->
                val gameToSave = game.copy(
                    status = GameStatus.BACKLOG,
                    aiDifficulty = AchievementDifficulty.NONE,
                    userDifficulty = AchievementDifficulty.NONE,
                    verifiedDifficulty = AchievementDifficulty.NONE
                )
                libraryRepository.addGameToLibrary(gameToSave)
            }

            emit("✅ Successfully imported ${gamesToSave.size} games!")
        } catch (e: Exception) {
            emit("❌ Scan error: ${e.message}")
        }
    }
}