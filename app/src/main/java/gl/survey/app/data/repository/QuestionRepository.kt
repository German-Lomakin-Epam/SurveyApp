package gl.survey.app.data.repository

import gl.survey.app.data.models.Answer
import gl.survey.app.data.models.Question

class QuestionRepository(private val api: QuestionsAPI): IQuestionRepository {

    override suspend fun getQuestions(): Result<List<Question>> {
        return api.getQuestions()
    }

    override suspend fun submitAnswer(answer: Answer): Result<Unit> {
        return api.submitAnswer(answer)
    }
}