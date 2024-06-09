package io.aoriani.recipe.ui.screens.recipe

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.aoriani.recipe.ui.screens.recipe.network.RecipeRequestResult
import io.aoriani.recipe.ui.screens.recipe.network.RecipeService
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    private var ingredients: List<String> = emptyList()
    private var description: String? = null
    private var result: RecipeRequestResult? = null

    private val _uiState = mutableStateOf<RecipeUiState>(RecipeUiState.Finding)
    val uiState: State<RecipeUiState> = _uiState


    fun setIngredients(ingredients: List<String>, description: String) {
        if (this.ingredients == ingredients && this.description == description) {
            return
        }

        println("$ingredients -> $description")
        viewModelScope.launch {
            try {
                val result = RecipeService.findRecipe(ingredients, description)
                this@RecipeViewModel.result = result
                if (result.error != null) {
                    val message = when (result.error.code) {
                        "NO_RECIPE" -> result.error.description
                        "NO_INGREDIENTS" -> "Hmmm... no ingredients"
                        else -> "Unknown Error"
                    }
                    _uiState.value = RecipeUiState.Error(message)
                } else if(result.recipe != null) {
                    _uiState.value = RecipeUiState.Found(
                        name = result.recipe.name,
                        description = result.recipe.description,
                        ingredients = result.recipe.ingredients,
                        steps = result.recipe.steps,
                        servingsSize = result.recipe.serving_size,
                        servings = result.recipe.servings,
                        image = result.recipe.image
                    )
                } else {
                    _uiState.value = RecipeUiState.Error("Server Implementation error")
                }
            } catch (t: Throwable) {
                _uiState.value = RecipeUiState.Error("Unknown Error")
            }

        }
    }
}

@Immutable
sealed interface RecipeUiState {

    @Immutable
    data object Finding : RecipeUiState

    @Immutable
    data class Found(
        val name: String,
        val description: String,
        val ingredients: List<String>,
        val steps: List<String>,
        val servings: String,
        val servingsSize: String,
        val image: String?
    ) : RecipeUiState


    @Immutable
    data class Error(val message: String) : RecipeUiState
}