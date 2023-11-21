package gl.survey.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import gl.survey.app.screens.start.StartScreen
import gl.survey.app.screens.survey.SurveyScreen

@Composable
fun SurveyNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SurveyRoutes.START_SCREEN,
        modifier = modifier) {
        composable(SurveyRoutes.START_SCREEN) {
            StartScreen(onNavigateToSurvey = { navController.navigate(SurveyRoutes.SURVEY_SCREEN) })
        }
        composable(SurveyRoutes.SURVEY_SCREEN) { SurveyScreen(onGoBackPressed = { navController.popBackStack() }) }
    }
}