package io.aoriani.recipe.ui.screens.assistant

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.aoriani.recipe.BuildConfig
import io.aoriani.recipe.ui.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class AssistantViewModel: ViewModel() {
    private val _uiState = mutableStateOf<AssistantUiState>(AssistantUiState.Loading)
    val uiState: State<AssistantUiState> = _uiState
    private var assistantArgs: Routes.Assistant? = null
    private val openAi = OpenAI(
        token = BuildConfig.openAiKey,
        timeout = Timeout(socket = 60.seconds),
    )


    fun setScript(assistantArgs: Routes.Assistant) {
        if (assistantArgs == this.assistantArgs) return

        this.assistantArgs = assistantArgs

        viewModelScope.launch {
            val stepsIllustrations = withContext(Dispatchers.IO) {
                checkNotNull(this@AssistantViewModel.assistantArgs)
                this@AssistantViewModel.assistantArgs!!.step_illustrations.map { genImage(it) }
            }
            _uiState.value = AssistantUiState.Loaded(stepsIllustrations)
        }

    }


    private suspend fun genImage(description: String): String? {
        return try {
            val urls = openAi.imageURL(creation = ImageCreation(
                prompt = description,
                model = ModelId("dall-e-3"),
                n = 1,
                size = ImageSize.is1024x1024
            ))
            urls.firstOrNull()?.url
        } catch (t: Throwable) {
            null
        }
    }
}


@Immutable
sealed interface AssistantUiState {
    @Immutable
    data object Loading: AssistantUiState

    @Immutable
    data class Loaded(val stepsUrls: List<String?>): AssistantUiState
}