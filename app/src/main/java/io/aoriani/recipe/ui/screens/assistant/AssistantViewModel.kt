package io.aoriani.recipe.ui.screens.assistant

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.aoriani.recipe.ui.navigation.Routes

class AssistantViewModel: ViewModel() {

    private val _uiState = mutableStateOf<AssistantUiState>(AssistantUiState.Loading)
    val uiState: State<AssistantUiState> = _uiState
    private var assistantArgs: Routes.Assistant? = null


    fun setScript(assistantArgs: Routes.Assistant) {
        if (assistantArgs == this.assistantArgs) return

    }


}


@Immutable
sealed interface AssistantUiState {
    @Immutable
    data object Loading: AssistantUiState

    @Immutable
    data object Loaded: AssistantUiState
}