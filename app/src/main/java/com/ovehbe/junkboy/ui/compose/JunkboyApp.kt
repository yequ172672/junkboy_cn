package com.ovehbe.junkboy.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ovehbe.junkboy.ui.compose.screens.*
import com.ovehbe.junkboy.ui.theme.DesignColors
import com.ovehbe.junkboy.ui.theme.DesignSpacing
import com.ovehbe.junkboy.utils.PreferencesManager
import com.ovehbe.junkboy.utils.SmsAppManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JunkboyApp(
    onRequestPermissions: () -> Unit,
    permissionRefreshTrigger: Int = 0,
    checkPermissions: () -> Boolean = { true }
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val smsAppManager = remember { SmsAppManager(context) }
    val preferencesManager = remember { PreferencesManager(context) }
    
    // Check if app is default SMS app
    val isDefaultSmsApp = smsAppManager.isJunkboyDefaultSmsApp()
    
    // Check if Hub is enabled (disabled by default)
    var isHubEnabled by remember { mutableStateOf(preferencesManager.isHubEnabled()) }
    
    // Refresh hub enabled state when returning to app
    LaunchedEffect(permissionRefreshTrigger) {
        isHubEnabled = preferencesManager.isHubEnabled()
    }
    
    // Determine start destination
    val startDestination = when {
        isHubEnabled -> "hub"
        isDefaultSmsApp -> "sms"
        else -> "messages"
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DesignColors.NavigationBackground,
                contentColor = DesignColors.NavigationActive,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(72.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Navigation items: (Hub if enabled), (SMS if default), Filtered, Menu
                val items = buildList {
                    if (isHubEnabled) {
                        add(BottomNavItem("hub", "Hub", Icons.Default.Inbox))
                    }
                    if (isDefaultSmsApp) {
                        add(BottomNavItem("sms", "SMS", Icons.Default.Message))
                    }
                    add(BottomNavItem("messages", "Filtered", Icons.Default.FilterList))
                    add(BottomNavItem("menu", "Menu", Icons.Default.Menu))
                }

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                item.icon, 
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DesignColors.NavigationActive,
                            selectedTextColor = DesignColors.NavigationActive,
                            unselectedIconColor = DesignColors.NavigationInactive,
                            unselectedTextColor = DesignColors.NavigationInactive,
                            indicatorColor = DesignColors.ActiveBackground
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("hub") {
                HubScreen(permissionRefreshTrigger = permissionRefreshTrigger)
            }
            composable("sms") {
                SmsScreen()
            }
            composable("messages") {
                MessagesScreen()
            }
            composable("menu") {
                MenuScreen(
                    onRequestPermissions = onRequestPermissions,
                    onNavigateToMessages = { navController.navigate("messages") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToStats = { navController.navigate("stats") },
                    onNavigateToTest = { navController.navigate("test") },
                    onNavigateToDashboard = { navController.navigate("dashboard") },
                    refreshTrigger = permissionRefreshTrigger,
                    checkPermissions = checkPermissions
                )
            }
            composable("dashboard") {
                DashboardScreen(
                    onRequestPermissions = onRequestPermissions,
                    onNavigateToMessages = { navController.navigate("messages") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    refreshTrigger = permissionRefreshTrigger
                )
            }
            composable("test") {
                TestFilterScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
            composable("stats") {
                StatsScreen()
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
