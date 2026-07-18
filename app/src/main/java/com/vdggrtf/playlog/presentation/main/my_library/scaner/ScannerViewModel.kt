package com.vdggrtf.playlog.presentation.main.my_library.scaner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.usecase.main.ai.ScanAndImportLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scanAndImportLibraryUseCase: ScanAndImportLibraryUseCase
) : ViewModel() {

    private val _statusText = MutableStateFlow<String?>(null)
    val statusText = _statusText.asStateFlow()

    fun scanAndImportLibrary(imageBytes: ByteArray) {
        viewModelScope.launch {
            scanAndImportLibraryUseCase(imageBytes).collect { statusMessage ->
                _statusText.value = statusMessage
            }
        }
    }

    fun clearStatus() {
        _statusText.value = null
    }
}