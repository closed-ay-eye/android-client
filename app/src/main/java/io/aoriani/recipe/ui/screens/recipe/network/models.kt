package io.aoriani.recipe.ui.screens.recipe.network

import kotlinx.serialization.Serializable

@Serializable
data class IngredientRequest(val ingredients: List<String>, val prompt: String)


@Serializable
data class RecipeClassScript(
    val ingredients: String,
    val steps: List<String>,
    val steps_illustration: List<String>
)

@Serializable
data class RecipeResult(
    val name: String,
    val image: String?,
    val rationale: String?,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val servings: String,
    val serving_size: String,
    val script: RecipeClassScript
)

@Serializable
class ErrorResult(
    val code: String,
    val description: String
)


@Serializable
data class RecipeRequestResult(
    val recipe: RecipeResult?,
    val error: ErrorResult?
)


