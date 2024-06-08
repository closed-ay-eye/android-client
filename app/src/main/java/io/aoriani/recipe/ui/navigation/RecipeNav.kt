package io.aoriani.recipe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.aoriani.recipe.ui.screens.landing.Landing
import io.aoriani.recipe.ui.screens.landing.LandingViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Landing) {

        composable<Routes.Landing> {
            val landingViewModel: LandingViewModel = viewModel()
            val uitState by landingViewModel.uiState
            Landing(uitState, onIngredientsFound = { ingredients, desc ->
                navController.navigate(Routes.Recipe(ingredients, desc))
            })
        }

        composable<Routes.Recipe> { navBackStackEntry ->
            val recipeRout = navBackStackEntry.toRoute<Routes.Recipe>()

        }
    }
}