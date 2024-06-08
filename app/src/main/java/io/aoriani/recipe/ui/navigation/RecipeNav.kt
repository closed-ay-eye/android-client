package io.aoriani.recipe.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.aoriani.recipe.ui.screens.landing.Landing
import io.aoriani.recipe.ui.screens.landing.LandingViewModel
import io.aoriani.recipe.ui.screens.recipe.RecipeViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Landing) {

        composable<Routes.Landing> {
            val landingViewModel: LandingViewModel = viewModel()
            val uitState by landingViewModel.uiState
            Landing(uitState, onIngredientsFound = { ingredients, desc ->
                Log.d("ORIANI","onIngredientsFound" )
                navController.navigate(Routes.Recipe(ingredients, desc))
            })
        }

        composable<Routes.Recipe> { navBackStackEntry ->
            val recipeViewModel: RecipeViewModel = viewModel()
            val (ingredients, desc) = navBackStackEntry.toRoute<Routes.Recipe>()
            Log.d("ORIANI","composable<Routes.Recipe>" )
            recipeViewModel.setIngredients(ingredients, desc)

        }
    }
}