package io.aoriani.recipe.domain

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class TtsUseCase {
    private val mediaPlayer = MediaPlayer()

    suspend fun playAudio(rawAudio:ByteArray, context: Context) {
        mediaPlayer.reset()
        try {
            val fileDescriptor = withContext(Dispatchers.IO) {
                val tempMp3 = File.createTempFile("temp", "mp3", context.cacheDir)
                tempMp3.deleteOnExit()

                val output = FileOutputStream(tempMp3)
                output.write(rawAudio)
                output.close()

                val input = FileInputStream(tempMp3)
                input.getFD()
            }
            mediaPlayer.setDataSource(fileDescriptor)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (exception: IOException) {
            Log.d("RecipeViewModel", "Error playing tts audio")
        }
    }
}
