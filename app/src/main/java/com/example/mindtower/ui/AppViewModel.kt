package com.example.mindtower.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindtower.data.TaskEntity
import com.example.mindtower.data.TaskType
import com.example.mindtower.domain.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val tasks: List<TaskEntity> = emptyList(),
    val logsCount: Int = 0,
    val roomsCount: Int = 0
)

sealed class Screen(val route: String, val label: String, val icon: String) {
    data object Home : Screen("home", "Home", "🏠")
    data object Tasks : Screen("tasks", "Tasks", "🗂")
    data object StateLog : Screen("state", "State", "🧠")
    data object Training : Screen("training", "Training", "⏱")
    data object Tower : Screen("tower", "Tower", "🗼")
    data object Settings : Screen("settings", "Settings", "⚙")
}

class AppViewModel(private val repository: AppRepository) : ViewModel() {
    private val _message = MutableStateFlow("Ready")
    val message: StateFlow<String> = _message.asStateFlow()

    val uiState = combine(repository.tasks, repository.stateLogs, repository.rooms) { t, l, r ->
        UiState(tasks = t, logsCount = l.size, roomsCount = r.size)
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), UiState())

    fun addTask(type: TaskType) = viewModelScope.launch {
        repository.addTask("New ${type.name.lowercase()} task", type)
    }

    fun markPeriodicDone(task: TaskEntity) = viewModelScope.launch { repository.markPeriodicDone(task) }
    fun addStateLog() = viewModelScope.launch { repository.addStateLog(45, "Quick log") }
    fun addFocusSession(taskId: Long?) = viewModelScope.launch { repository.addFocusSession(taskId) }
    fun addRefusal() = viewModelScope.launch { repository.addRefusalLog("Social media") }
    fun addWalkBreak() = viewModelScope.launch { repository.addWalkBreak(600) }
    fun addRoom() = viewModelScope.launch { repository.addRoom("New room") }

    fun exportBackup(context: Context) = viewModelScope.launch {
        val file = repository.exportBackup(context)
        _message.value = "Backup exported: ${file.absolutePath}"
    }

    fun importBackup(json: String) = viewModelScope.launch {
        repository.importBackup(json)
        _message.value = "Backup imported"
    }

    fun rawDbPath(context: Context): String = repository.rawDbPath(context).absolutePath
}
