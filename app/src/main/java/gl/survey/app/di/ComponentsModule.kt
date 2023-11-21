package gl.survey.app.di

import gl.survey.app.data.repository.IQuestionRepository
import gl.survey.app.data.repository.QuestionRepository
import gl.survey.app.data.repository.QuestionsAPI
import gl.survey.app.data.repository.ResultCallAdapterFactory
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ComponentsModule {
    val components = module {
        single { buildRetrofit() }
        single { QuestionRepository(get()) } bind IQuestionRepository::class
    }

    private fun buildRetrofit(): QuestionsAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://xm-assignment.web.app")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .build()

        return retrofit.create(QuestionsAPI::class.java)
    }
}