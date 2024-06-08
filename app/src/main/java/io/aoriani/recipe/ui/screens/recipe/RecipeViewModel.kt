package io.aoriani.recipe.ui.screens.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.aoriani.recipe.ui.screens.recipe.network.RecipeService
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private var ingredients: List<String> = emptyList()
    private var description: String? = null


    fun setIngredients(ingredients: List<String>, description: String) {
        if (this.ingredients == ingredients && this.description == description ) {
            return
        }

        println("$ingredients -> $description")
        viewModelScope.launch {
            val result = RecipeService.findRecipe(ingredients, description)
            Log.d("ORIANI", result.toString())
        }
    }
}