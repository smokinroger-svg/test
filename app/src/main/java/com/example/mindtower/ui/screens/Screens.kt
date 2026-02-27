package com.example.mindtower.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mindtower.data.TaskType
import com.example.mindtower.ui.AppViewModel

@Composable
fun HomeScreen(vm: AppViewModel) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("MindTower Home")
        Text("Tasks: ${state.tasks.size}")
        Text("State logs: ${state.logsCount}")
        Text("Rooms: ${state.roomsCount}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(vm: AppViewModel) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) }
    val types = TaskType.entries
    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            types.forEachIndexed { idx, t -> Tab(selected = tab == idx, onClick = { tab = idx }, text = { Text(t.name) }) }
        }
        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.addTask(types[tab]) }) { Text("Add") }
        }
        LazyColumn(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.tasks.filter { it.type == types[tab] }) { task ->
                Card(Modifier.fillMaxWidth().padding(4.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text(task.title)
                        Text("status=${task.status}")
                        if (task.type == TaskType.PERIODIC) {
                            Button(onClick = { vm.markPeriodicDone(task) }) { Text("Done (shift next_due_at)") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StateLogScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("State logging journal")
        Text("Fields: energy/focus/stress/mood (0..5), free text, emotions, task snapshots")
        Button(onClick = { vm.addStateLog() }) { Text("Create quick state log") }
    }
}

@Composable
fun TrainingScreen(vm: AppViewModel) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Focus", "Refusal", "Walk/Break")
    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            tabs.forEachIndexed { i, t -> Tab(selected = i == tab, onClick = { tab = i }, text = { Text(t) }) }
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            when (tab) {
                0 -> {
                    Text("Interval timer with progression (work/rest, planned sessions)")
                    Button(onClick = { vm.addFocusSession(null) }) { Text("Save focus session") }
                }
                1 -> {
                    Text("Refusal training widget: impulse, urge 0..5, delay, resist")
                    Button(onClick = { vm.addRefusal() }) { Text("Quick refusal log") }
                }
                else -> {
                    Text("Walk / prefrontal unload reminder timer")
                    Button(onClick = { vm.addWalkBreak() }) { Text("Log walk break") }
                }
            }
        }
    }
}

@Composable
fun TowerScreen(vm: AppViewModel) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Tower visualization")
        Text("Rooms with notes and projects")
        Button(onClick = { vm.addRoom() }) { Text("Add room") }
    }
}

@Composable
fun SettingsScreen(vm: AppViewModel) {
    val context = LocalContext.current
    val message by vm.message.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Backup/Restore")
        Button(onClick = { vm.exportBackup(context) }) { Text("Export JSON backup") }
        Text("Import uses demo payload for offline restore flow")
        Button(onClick = { vm.importBackup("{\"tasks\":[],\"logs\":[],\"emotions\":[],\"snapshots\":[],\"sessions\":[],\"refusals\":[],\"walks\":[],\"rooms\":[],\"notes\":[],\"projects\":[],\"projectItems\":[]}") }) {
            Text("Import sample JSON")
        }
        Text("Raw DB file: ${vm.rawDbPath(context)}")
        Text(message)
        Text("Notifications use WorkManager periodic reminder worker (and may trigger AlarmManager bridge as needed).")
    }
}
