package gl.survey.app

import gl.survey.app.data.models.Answer
import gl.survey.app.data.models.Question
import gl.survey.app.data.repository.IQuestionRepository

class MockedQuestionsSuceess : IQuestionRepository {
    override suspend fun getQuestions(): Result<List<Question>> {
        return Result.success(
            listOf(
                Question(1, "Question 1"),
                Question(2, "Question 2"),
                Question(3, "Question 3"),
                Question(4, "Question 4"),
                Question(5, "Question 5")
            )
        )
    }

    override suspend fun submitAnswer(answer: Answer): Result<Unit> {
        return Result.success(Unit)
    }
}

class MockedQuestionsEmpty : IQuestionRepository {
    override suspend fun getQuestions(): Result<List<Question>> {
        return Result.success(
            emptyList()
        )
    }

    override suspend fun submitAnswer(answer: Answer): Result<Unit> {
        return Result.success(Unit)
    }
}

class MockedQuestionsFailed : IQuestionRepository {
    override suspend fun getQuestions(): Result<List<Question>> {
        return Result.failure(RuntimeException("Something went wrong"))
    }

    override suspend fun submitAnswer(answer: Answer): Result<Unit> {
        return Result.failure(RuntimeException("Something went wrong"))
    }
}

class MockedQuestionsSubmissionFailed : IQuestionRepository {
    override suspend fun getQuestions(): Result<List<Question>> {
        return Result.success(
            listOf(
                Question(1, "Question 1"),
                Question(2, "Question 2"),
                Question(3, "Question 3"),
                Question(4, "Question 4"),
                Question(5, "Question 5")
            )
        )
    }

    override suspend fun submitAnswer(answer: Answer): Result<Unit> {
        return Result.failure(RuntimeException("Something went wrong"))
    }
}
