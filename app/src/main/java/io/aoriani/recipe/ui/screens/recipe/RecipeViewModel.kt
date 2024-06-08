package io.aoriani.recipe.ui.screens.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.aoriani.recipe.ui.screens.recipe.network.RecipeService
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {



    fun setIngredients(ingredients: List<String>, description: String) {
        println("$ingredients -> $description")
        viewModelScope.launch {
            val result = RecipeService.findRecipe(ingredients, description)
            Log.d("ORIANI", result.toString())
        }
    }
}