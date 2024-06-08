package io.aoriani.recipe.ui.screens.recipe.network

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface RecipeApi {
    @POST("recipes/find")
    suspend fun findRecipe(@Body request: IngredientRequest): RecipeRequestResult
}

object RecipeService {
    private val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(60, TimeUnit.SECONDS)
        connectTimeout(60, TimeUnit.SECONDS)
        val logging = HttpLoggingInterceptor { msg -> Log.d("Network", msg) }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        addInterceptor(logging)
    }.build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://recipe-ca81.onrender.com/")
        .client(okHttpClient)
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()
            )
        )
        .build();

    private val service = retrofit.create(RecipeApi::class.java)

    suspend fun findRecipe(ingredients: List<String>, prompt: String): RecipeRequestResult {
        return service.findRecipe(IngredientRequest(ingredients, prompt))
    }
}