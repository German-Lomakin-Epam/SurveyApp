package gl.survey.app.data.repository

import gl.survey.app.data.models.Answer
import gl.survey.app.data.models.Question

interface IQuestionRepository {
    suspend fun getQuestions(): Result<List<Question>>

    suspend fun submitAnswer(answer: Answer): Result<Unit>
}