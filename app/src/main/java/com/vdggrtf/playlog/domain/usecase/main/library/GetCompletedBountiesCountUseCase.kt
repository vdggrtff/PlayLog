package com.vdggrtf.playlog.domain.usecase.main.library

import android.util.Log
import com.vdggrtf.playlog.data.network.dto.CompletedIdDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import jakarta.inject.Inject

class GetCompletedBountiesCountUseCase @Inject constructor(
    private val supabase: SupabaseClient
) {
    // 💥 ТВОЙ ТОЧНЫЙ ЗАПРОС В SUPABASE
    suspend operator fun invoke(): Int {
        return try {
            supabase.from("user_challenge_status")
                .select(columns = Columns.list("challenge_id")) {
                    filter { eq("status", "COMPLETED") }
                }.decodeList<CompletedIdDto>().size
        } catch (e: Exception) {
            Log.e("GetCompletedBounties", "Ошибка загрузки счетчика контрактов: ${e.message}")
            0
        }
    }
}