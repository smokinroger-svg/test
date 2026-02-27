package com.example.mindtower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindtower.ui.AppViewModel
import com.example.mindtower.ui.Screen
import com.example.mindtower.ui.screens.HomeScreen
import com.example.mindtower.ui.screens.SettingsScreen
import com.example.mindtower.ui.screens.StateLogScreen
import com.example.mindtower.ui.screens.TasksScreen
import com.example.mindtower.ui.screens.TowerScreen
import com.example.mindtower.ui.screens.TrainingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MindTowerAppUi((application as MindTowerApp).repository) }
    }
}

@Composable
private fun MindTowerAppUi(repository: com.example.mindtower.domain.AppRepository) {
    val navController = rememberNavController()
    val vm: AppViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(repository) as T
    })
    val tabs = listOf(Screen.Home, Screen.Tasks, Screen.StateLog, Screen.Training, Screen.Tower, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val current = navBackStackEntry?.destination
                tabs.forEach { screen ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(screen.icon) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = Screen.Home.route, modifier = Modifier.padding(padding)) {
            composable(Screen.Home.route) { HomeScreen(vm) }
            composable(Screen.Tasks.route) { TasksScreen(vm) }
            composable(Screen.StateLog.route) { StateLogScreen(vm) }
            composable(Screen.Training.route) { TrainingScreen(vm) }
            composable(Screen.Tower.route) { TowerScreen(vm) }
            composable(Screen.Settings.route) { SettingsScreen(vm) }
        }
    }
}
