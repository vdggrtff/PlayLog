package com.vdggrtf.playlog.domain.usecase

import com.vdggrtf.playlog.domain.repository.LibraryRepository
import javax.inject.Inject

class GetTotalBountyXpUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getTotalBounty()
    }
}