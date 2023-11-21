package gl.survey.app.di

import gl.survey.app.screens.survey.SurveyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModules {
    val viewModels = module {
        viewModel { SurveyViewModel(get()) }
    }
}