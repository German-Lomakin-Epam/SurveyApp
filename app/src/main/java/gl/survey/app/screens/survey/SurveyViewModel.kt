package gl.survey.app.screens.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gl.survey.app.R
import gl.survey.app.data.models.Answer
import gl.survey.app.data.models.Question
import gl.survey.app.data.repository.IQuestionRepository
import gl.survey.app.utils.UIText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class SurveyViewModel(private val repository: IQuestionRepository) : ViewModel() {

    private var currentIndex = 0
    private val questions = mutableListOf<SurveyQuestion>()

    private val _uiState = MutableStateFlow<SurveyUIState>(SurveyUIState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _userMessages = MutableStateFlow<SubmissionMessages>(SubmissionMessages.Empty)
    val userMessages = _userMessages.asStateFlow()

    private val _questionSectionState =
        MutableStateFlow(
            QuestionSectionState(
            UIText.Empty,
            isNextEnabled = false,
            isPrevEnable = false
        )
        )
    val questionSectionState = _questionSectionState.asStateFlow()

    init {
        loadQuestions()
    }

    fun nextQuestion() {
        viewModelScope.launch {
            cleanMessage()
            if (currentIndex < questions.lastIndex) currentIndex++
            updateCurrentQuestion()
            updateQuestionSelection()
        }
    }

    fun prevQuestion() {
        viewModelScope.launch {
            cleanMessage()
            if (currentIndex > 0) currentIndex--
            updateCurrentQuestion()
            updateQuestionSelection()
        }
    }

    fun updateAnswer(answer: String) {
        viewModelScope.launch {
            cleanMessage()
            if (questions[currentIndex].isSubmitEnable) {
                questions[currentIndex].userAnswer = answer
                updateCurrentQuestion()
            }
        }
    }

    fun submitAnswer() {
        viewModelScope.launch {
            cleanMessage()
            val currentQuestion = questions[currentIndex]
            if (validateAnswer(currentQuestion.userAnswer)) {
                currentQuestion.isSubmitEnable = false
                updateCurrentQuestion()
                repository.submitAnswer(Answer(currentQuestion.id, currentQuestion.userAnswer))
                    .onSuccess {
                        currentQuestion.isSubmitEnable = false
                        updateCurrentQuestion()
                        _userMessages.emit(SubmissionMessages.SubmissionSuccess)
                    }
                    .onFailure {
                        currentQuestion.isSubmitEnable = true
                        updateCurrentQuestion()
                        _userMessages.emit(SubmissionMessages.SubmissionFails)
                    }
            } else {
                _userMessages.emit(SubmissionMessages.BlankAnswer)
            }
        }
    }

    fun messageShown() {
        viewModelScope.launch {
            cleanMessage()
        }
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.emit(SurveyUIState.Loading)
            cleanMessage()
            questions.clear()
            currentIndex = 0

            repository.getQuestions()
                .onSuccess {
                    it.forEach { item -> questions.add(SurveyQuestion(item)) }
                    if (questions.isNotEmpty()) {
                        updateCurrentQuestion()
                        updateQuestionSelection()
                    } else {
                        _uiState.emit(SurveyUIState.Empty)
                    }
                }
                .onFailure {
                    when (it.cause) {
                        is UnknownHostException -> _uiState.emit(
                            SurveyUIState.LoadingError(
                                UIText.StringResource(
                                    R.string.error_check_network_connection
                                )
                            )
                        )

                        else -> _uiState.emit(SurveyUIState.LoadingError(UIText.StringResource(R.string.error_server_error)))
                    }
                }
        }
    }

    private suspend fun updateQuestionSelection() {
        val index = "${currentIndex + 1}/${questions.size}"

        _questionSectionState.emit(
            QuestionSectionState(
            currentText = UIText.StringResource(R.string.survey_screen_question_indicator, index),
            isNextEnabled = (currentIndex < questions.lastIndex),
            isPrevEnable = (currentIndex > 0)
        )
        )
    }

    private suspend fun updateCurrentQuestion() {
        val question = questions[currentIndex]
        val submitted = questions.sumOf { if (it.isSubmitEnable) 0L else 1L }
        val scoreText = "$submitted/${questions.size}"

        _uiState.emit(
            SurveyUIState.ActiveQuestion(
                question.question,
                question.userAnswer,
                isSubmitEnable = question.isSubmitEnable,
                UIText.StringResource(R.string.survey_screen_score_indicator, scoreText)
            )
        )
    }

    private suspend fun cleanMessage() {
        _userMessages.emit(SubmissionMessages.Empty)
    }

    private fun validateAnswer(answer: String): Boolean = answer.isNotBlank()

    private class SurveyQuestion(
        val id: Int,
        val question: String,
        var userAnswer: String = "",
        var isSubmitEnable: Boolean = true
    ) {
        constructor(question: Question) : this(question.id, question.question)
    }
}