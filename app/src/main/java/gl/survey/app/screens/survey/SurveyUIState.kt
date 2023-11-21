package gl.survey.app.screens.survey

import gl.survey.app.utils.UIText

sealed class SurveyUIState {
    data object Loading : SurveyUIState()
    data object Empty: SurveyUIState()
    class LoadingError(val errorText: UIText): SurveyUIState()
    class ActiveQuestion(
        val question: String,
        val answer: String,
        val isSubmitEnable: Boolean,
        val score: UIText
    ) : SurveyUIState()
}

data class QuestionSectionState(
    val currentText: UIText,
    val isNextEnabled: Boolean,
    val isPrevEnable: Boolean,
)