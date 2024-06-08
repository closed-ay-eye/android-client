package io.aoriani.recipe.ui.navigation

import io.aoriani.recipe.ui.screens.landing.LandingUiState
import kotlinx.serialization.Serializable

sealed interface Routes{
    @Serializable
    data object Landing

    @Serializable
    data class Recipe(val ingredients: List<String>, val description: String)

}