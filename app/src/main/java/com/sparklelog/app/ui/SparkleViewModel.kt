package com.sparklelog.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sparklelog.app.data.Feeling
import com.sparklelog.app.data.MAX_FEELINGS_PER_SPARKLE
import com.sparklelog.app.data.SparkleRepository
import com.sparklelog.app.data.SparkleWithFeelings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SparkleViewModel(private val repository: SparkleRepository) : ViewModel() {

    val feelings: StateFlow<List<Feeling>> = repository.feelings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sparkles: StateFlow<List<SparkleWithFeelings>> = repository.sparklesWithFeelings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSparkle(
        text: String,
        selectedFeelingIds: Set<Long>,
        newFeelingName: String? = null,
        newFeelingEmoji: String? = null,
        onSaved: () -> Unit = {}
    ) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return
        val trimmedNewName = newFeelingName?.trim()
        viewModelScope.launch {
            val feelingIds = selectedFeelingIds.toMutableList()
            if (!trimmedNewName.isNullOrEmpty()) {
                val trimmedEmoji = newFeelingEmoji?.trim()?.ifEmpty { null }
                val feeling = repository.findOrCreateFeeling(trimmedNewName, trimmedEmoji)
                feelingIds.add(feeling.id)
            }
            if (feelingIds.isEmpty()) return@launch
            repository.addSparkle(trimmedText, feelingIds.distinct().takeLast(MAX_FEELINGS_PER_SPARKLE))
            onSaved()
        }
    }

    fun updateFeeling(feeling: Feeling, colorHex: String, emoji: String?) {
        viewModelScope.launch {
            repository.updateFeeling(feeling, colorHex, emoji)
        }
    }

    fun updateSparkle(id: Long, text: String, feelingIds: List<Long>) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty() || feelingIds.isEmpty()) return
        viewModelScope.launch {
            repository.updateSparkle(id, trimmedText, feelingIds)
        }
    }

    fun deleteSparkle(id: Long) {
        viewModelScope.launch {
            repository.deleteSparkle(id)
        }
    }
}
