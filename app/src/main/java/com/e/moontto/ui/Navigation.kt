package com.e.moontto.ui

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.e.moontto.MainViewModel
import com.e.moontto.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Analysis : Screen("analysis", R.string.analysis)
    object Moontto : Screen("moontto", R.string.moontto)
}

@Composable
fun BottomNavi(
    context: Context,
    mainViewModel: MainViewModel
) {
    Log.d("ㅇㅇㅇ BottomNavi", "BottomNavi")
    val navController = rememberNavController()

    val items = listOf(
        Screen.Analysis,
        Screen.Moontto,
    )
    
    Scaffold(
        bottomBar = {
            BottomNavigation {
                Log.d("ㅇㅇㅇ BottomNavigation", "BottomNavigation")
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = false

                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, Screen.Moontto.route, Modifier.padding(innerPadding)) {
            composable(Screen.Analysis.route) {
                Log.d("ㅇㅇㅇ NavHost", "Analysis")
                MoonttoTable(mainViewModel)
            }
            composable(Screen.Moontto.route) {
                Log.d("ㅇㅇㅇ NavHost", "Moontto")
                TitleLayout(context, mainViewModel)
            }
        }
    }
}