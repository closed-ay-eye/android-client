package io.aoriani.recipe.domain

import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI


class OpenAiTextToSpeech(
    private val openAI: OpenAI,
) {

    suspend fun synthesize(text: String) = openAI.speech(
        request = SpeechRequest(
            model = ModelId("tts-1"),
            input = text,
            voice = Voice.Nova,
        )
    )
}
