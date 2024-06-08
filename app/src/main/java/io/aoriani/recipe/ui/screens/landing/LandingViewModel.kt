package io.aoriani.recipe.ui.screens.landing

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import io.aoriani.recipe.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LandingViewModel(application: Application) : AndroidViewModel(application) {
    private val _uistate =
        mutableStateOf<LandingUiState>(
            LandingUiState.Init(
                onTextChanged = ::onTextChanged,
                setImageBitmap = ::setImage,
                setImageUri = ::setImage,
                send = ::send
            )
        )
    val uiState: State<LandingUiState> = _uistate

    private val model = GenerativeModel(
        "gemini-1.0-pro-vision-latest",
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        BuildConfig.geminiKey,
        generationConfig = generationConfig {
            temperature = 0.4f
            topK = 32
            topP = 1f
            maxOutputTokens = 4096
            responseMimeType = "text/plain"
        },
    )

    private var bitmap: Bitmap? = null
    private var description: String = ""

    private fun onTextChanged(text: String) {
        check(_uistate.value is LandingUiState.Init)
        description = text.trim()
        _uistate.value = (_uistate.value as LandingUiState.Init).copy(text = text)
    }

    private fun setImage(bmp: Bitmap) {
        check(_uistate.value is LandingUiState.Init)
        bitmap = bmp
        _uistate.value = (_uistate.value as LandingUiState.Init).copy(sendEnabled = true)
    }

    private fun setImage(uri: Uri) {
        check(_uistate.value is LandingUiState.Init)
        val source = ImageDecoder.createSource(getApplication<Application>().contentResolver, uri)
        val bmp =
            ImageDecoder.decodeBitmap(source) { decoder, _, _ -> decoder.setTargetSampleSize(2) }
        bitmap = bmp
        _uistate.value = (_uistate.value as LandingUiState.Init).copy(sendEnabled = true)
    }

    private fun send() {
        val bmp = checkNotNull(bitmap)
        _uistate.value = LandingUiState.Resolving
        viewModelScope.launch(Dispatchers.Main) {
            val ingredients = withContext(Dispatchers.IO) {
                findIngredientsIn(bmp)
            }
            if (ingredients.isEmpty()) {
                _uistate.value = LandingUiState.NoIngredients(onDismiss = ::reset)
            } else {
                _uistate.value = LandingUiState.IngredientsFound(
                    ingredients,
                    description = description.trim().takeIf { it.isNotBlank() } ?: "Pick Any",
                    consume = ::reset
                )
            }
        }
    }

    private fun reset() {
        _uistate.value = LandingUiState.Init(
            onTextChanged = ::onTextChanged,
            setImageBitmap = ::setImage,
            setImageUri = ::setImage,
            send = ::send
        )
    }

    private suspend fun findIngredientsIn(bitmap: Bitmap): List<String> {
        return try {
            val response = model.generateContent(
                content {
                    text(
                        """ 
                        You are an agent that identifies food ingredients in images. Identify all ingredients in the 
                        picture and return the response as a JSON list. If no ingredient is found in the image return an empty list. 
                        Ingredients should be in lowercase. Do not mention brand names. The output should be formatted as a JSON instance that conforms to the JSON schema below.

                        As an example, for the schema {"properties": {"foo": {"title": "Foo", "description": "a list of strings", "type": "array", "items": {"type": "string"}}}, "required": ["foo"]}
                        the object {"foo": ["bar", "baz"]} is a well-formatted instance of the schema. The object {"properties": {"foo": ["bar", "baz"]}} is not well-formatted.

                        Here is the output schema:
                        ```
                        {"properties": {"ingredients": {"title": "Ingredients", "description": "A list of ingredients", "default": [], "type": "array", "items": {"type": "string"}}}}
                        ```
                    """.trimIndent()
                    )
                    image(bitmap)
                }
            )

            val jsonMarkdown = response.text
            val regex = Regex("""```json\s*(\{.*?\})\s*```""", RegexOption.DOT_MATCHES_ALL)
            val matchResult = regex.find(jsonMarkdown.orEmpty())
            val jsonString = matchResult?.groups?.get(1)?.value
            if (jsonString != null) {
                val jsonObject = JSONObject(jsonString)
                val ingredients = jsonObject.getJSONArray("ingredients")
                val list = arrayListOf<String>()
                for (i in 0..<ingredients.length()) {
                    list += ingredients.get(i) as String
                }
                list
            } else {
                emptyList()
            }
        } catch (t: Throwable) {
            emptyList()
        }

    }
}

@Immutable
sealed interface LandingUiState {

    @Immutable
    data class Init(
        val text: String = "",
        val sendEnabled: Boolean = false,
        val onTextChanged: (String) -> Unit = {},
        val setImageBitmap: (Bitmap) -> Unit = {},
        val setImageUri: (Uri) -> Unit = {},
        val send: () -> Unit = {}
    ) : LandingUiState


    @Immutable
    data object Resolving : LandingUiState

    @Immutable
    data class NoIngredients(val onDismiss: () -> Unit = {}) : LandingUiState

    @Immutable
    data class IngredientsFound(
        val ingredients: List<String>,
        val description: String,
        val consume: () -> Unit = {}
    ) : LandingUiState

}