package io.aoriani.recipe.ui.screens.assistant

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.aoriani.recipe.BuildConfig
import io.aoriani.recipe.domain.OpenAiTextToSpeech
import io.aoriani.recipe.domain.TtsUseCase
import io.aoriani.recipe.ui.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class AssistantViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = mutableStateOf<AssistantUiState>(AssistantUiState.Loading)
    val uiState: State<AssistantUiState> = _uiState
    private var assistantArgs: Routes.Assistant? = null
    private val openAi = OpenAI(
        token = BuildConfig.openAiKey,
        timeout = Timeout(socket = 60.seconds),
    )
    private val ttsUseCase by lazy { TtsUseCase() }
    private val openAiTextToSpeech by lazy { OpenAiTextToSpeech(openAi) }

    fun setScript(assistantArgs: Routes.Assistant) {
        if (assistantArgs == this.assistantArgs) return

        this.assistantArgs = assistantArgs

        viewModelScope.launch {
            val stepsIllustrations = withContext(Dispatchers.IO) {
                checkNotNull(this@AssistantViewModel.assistantArgs)
                this@AssistantViewModel.assistantArgs!!.step_illustrations.map { async { genImage(it) } }
                    .map { it.await() }
            }
            _uiState.value = AssistantUiState.Loaded(
                steps = this@AssistantViewModel.assistantArgs!!.steps,
                stepsUrls = stepsIllustrations,
                onClickAudio = ::playAudioStep,
            )
        }

    }


    private suspend fun genImage(description: String): String? {
        return try {
            val urls = openAi.imageURL(
                creation = ImageCreation(
                    prompt = description,
                    model = ModelId("dall-e-3"),
                    n = 1,
                    size = ImageSize.is1024x1024
                )
            )
            urls.firstOrNull()?.url
        } catch (t: Throwable) {
            null
        }
    }

    private fun playAudioStep(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val rawAudio = openAiTextToSpeech.synthesize(text)

            ttsUseCase.playAudio(
                rawAudio = rawAudio,
                context = getApplication<Application>().applicationContext,
            )
        }
    }
}


@Immutable
sealed interface AssistantUiState {
    @Immutable
    data object Loading : AssistantUiState

    @Immutable
    data class Loaded(
        val steps: List<String>,
        val stepsUrls: List<String?>,
        val onClickAudio: (String) -> Unit,
    ) : AssistantUiState
}