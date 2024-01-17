@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.model.OrderUiState
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum defining routes for destinations

enum class LunchTrayScreen(@StringRes val title: Int){
    Start(title = R.string.start_order),
    EntreeMenu (title = R.string.choose_entree),
    SideDishMenu(title = R.string.choose_side_dish),
    AccompanimentMenu(title = R.string.choose_accompaniment),
    Checkout(title = R.string.order_checkout)
}

// TODO: AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp() {
    // TODO: Create Controller and initialization

    val navController = rememberNavController()

    //    navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)

    val backStackEntry by navController.currentBackStackEntryAsState()


    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    val currentScreen = LunchTrayScreen.valueOf(
        backStackEntry?.destination?.route ?: LunchTrayScreen.Start.name
    )

    Scaffold(
        topBar = {
            // TODO: AppBar
            LunchTrayAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }

            )
        }
    ) {
        innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
        // NavHost is a Composable that displays other composable destinations based on a given route

        NavHost(
            navController = navController,
            startDestination = LunchTrayScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
            ) {
            // call the composable() function within the content function of a NavHost
            composable(route = LunchTrayScreen.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate( LunchTrayScreen.EntreeMenu.name)
                    }
                )
            }
            composable(route = LunchTrayScreen.EntreeMenu.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        navController.navigateUp()
                                            },
                    onNextButtonClicked = {
                        navController.navigate( LunchTrayScreen.SideDishMenu.name)
                                          },
                    onSelectionChanged = { viewModel.updateEntree(it) },
                )

            }
            composable(route = LunchTrayScreen.SideDishMenu.name) {
                SideDishMenuScreen(
                    options = (DataSource.sideDishMenuItems),
                    onCancelButtonClicked = {
                        navController.navigateUp()
                                            },
                    onNextButtonClicked = {
                        navController.navigate( LunchTrayScreen.AccompanimentMenu.name)
                                          },
                    onSelectionChanged = { viewModel.updateSideDish(it) },
                )

            }
            composable(route = LunchTrayScreen.AccompanimentMenu.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        navController.navigate( LunchTrayScreen.Start.name)
                                            },
                    onNextButtonClicked = {
                        navController.navigate( LunchTrayScreen.Checkout.name)
                    },
                    onSelectionChanged = { viewModel.updateAccompaniment(it) },
                )

            }
            composable(route = LunchTrayScreen.Checkout.name) {
                CheckoutScreen(
                    orderUiState = OrderUiState(
                        entree = DataSource.entreeMenuItems[0],
                        sideDish = DataSource.sideDishMenuItems[0],
                        accompaniment = DataSource.accompanimentMenuItems[0],
                        itemTotalPrice = 15.00,
                        orderTax = 1.00,
                        orderTotalPrice = 16.00
                    ),
                    onNextButtonClicked = {
                        navController.navigate( LunchTrayScreen.Start.name)
                                          },
                    onCancelButtonClicked = {
                        navController.navigate( LunchTrayScreen.Start.name)
                    })

            }
        }

    }
}

@Composable
fun LunchTrayAppBar(
    currentScreen: LunchTrayScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
