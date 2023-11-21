package gl.survey.app

import android.app.Application
import gl.survey.app.di.ComponentsModule
import gl.survey.app.di.ViewModelModules
import org.koin.core.context.startKoin

class SurveyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(ViewModelModules.viewModels, ComponentsModule.components)
        }
    }
}