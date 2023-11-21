package gl.survey.app.data.repository

import gl.survey.app.data.models.Answer
import gl.survey.app.data.models.Question
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface QuestionsAPI {
    @GET("questions")
    suspend fun getQuestions() : Result<List<Question>>

    @POST("question/submit")
    suspend fun submitAnswer(@Body answer: Answer) : Result<Unit>
}