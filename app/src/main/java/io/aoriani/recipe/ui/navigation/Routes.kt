package io.aoriani.recipe.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Landing

    @Serializable
    data class Recipe(val ingredients: List<String>, val description: String)

    @Serializable
    data class Assistant(
        val ingredients: String,
        val steps: List<String>,
        val step_illustrations: List<String>
    )

}