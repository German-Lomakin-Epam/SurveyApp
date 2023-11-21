package gl.survey.app

import gl.survey.app.screens.survey.SubmissionMessages
import gl.survey.app.screens.survey.SurveyUIState
import gl.survey.app.screens.survey.SurveyViewModel
import gl.survey.app.utils.UIText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MainDispatcherRule @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}


class SurveyViewModelUnitTests {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test()
    fun checkDataLoadedAndFirstQuestionIsActive() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
    }

    @Test
    fun checkDataLoadFailed() = runTest {
        val vm = SurveyViewModel(MockedQuestionsFailed())
        val result = vm.uiState.value is SurveyUIState.LoadingError
        assertEquals(result, true)
    }

    @Test
    fun checkDataLoadedButEmpty() = runTest {
        val vm = SurveyViewModel(MockedQuestionsEmpty())
        val result = vm.uiState.value is SurveyUIState.Empty
        assertEquals(result, true)
    }

    @Test
    fun checkPrevButtonDisabledForFirstQuestion() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(false, vm.questionSectionState.value.isPrevEnable)
        assertEquals(true, vm.questionSectionState.value.isNextEnabled)
    }

    @Test
    fun checkNextButtonDisabledForLastQuestion() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        repeat(5) {
            vm.nextQuestion()
        }
        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 5", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(false, vm.questionSectionState.value.isNextEnabled)
        assertEquals(true, vm.questionSectionState.value.isPrevEnable)
    }

    @Test
    fun checkSelectedQuestionIndicator() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.nextQuestion()
        vm.nextQuestion()
        vm.nextQuestion()
        vm.prevQuestion()
        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 3", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, vm.questionSectionState.value.isNextEnabled)
        assertEquals(true, vm.questionSectionState.value.isPrevEnable)
        assertEquals(
            "3/5",
            (vm.questionSectionState.value.currentText as UIText.StringResource).args[0]
        )
    }

    @Test
    fun checkMultipleNextPrevsClicks() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        repeat(7) {
            vm.nextQuestion()
        }
        repeat(3) {
            vm.prevQuestion()
        }
        vm.nextQuestion()
        vm.nextQuestion()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 4", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, vm.questionSectionState.value.isNextEnabled)
        assertEquals(true, vm.questionSectionState.value.isPrevEnable)
        assertEquals(
            "4/5",
            (vm.questionSectionState.value.currentText as UIText.StringResource).args[0]
        )
    }

    @Test
    fun checkMultipleNextPrevsClicks2() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        repeat(3) {
            vm.nextQuestion()
        }
        repeat(7) {
            vm.prevQuestion()
        }
        vm.nextQuestion()
        vm.nextQuestion()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 3", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, vm.questionSectionState.value.isNextEnabled)
        assertEquals(true, vm.questionSectionState.value.isPrevEnable)
        assertEquals(
            "3/5",
            (vm.questionSectionState.value.currentText as UIText.StringResource).args[0]
        )
    }

    @Test
    fun checkSubmitBlankAnswer() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.submitAnswer()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, (vm.uiState.value as SurveyUIState.ActiveQuestion).isSubmitEnable)
        assertEquals(SubmissionMessages.BlankAnswer, vm.userMessages.value)
    }

    @Test
    fun checkSubmitSuccess() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.updateAnswer("Answer 1")
        vm.submitAnswer()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(false, (vm.uiState.value as SurveyUIState.ActiveQuestion).isSubmitEnable)
        assertEquals(SubmissionMessages.SubmissionSuccess, vm.userMessages.value)
        assertEquals("Answer 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).answer)
    }

    @Test
    fun checkSubmitFail() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSubmissionFailed())
        vm.updateAnswer("Answer 1")
        vm.submitAnswer()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, (vm.uiState.value as SurveyUIState.ActiveQuestion).isSubmitEnable)
        assertEquals(SubmissionMessages.SubmissionFails, vm.userMessages.value)
        assertEquals("Answer 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).answer)
    }

    @Test
    fun checkSubmittedQuestionsCounter() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.updateAnswer("Answer 1")
        vm.submitAnswer()
        vm.nextQuestion()
        vm.nextQuestion()
        vm.updateAnswer("Answer 2")
        vm.submitAnswer()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 3", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals("Answer 2", (vm.uiState.value as SurveyUIState.ActiveQuestion).answer)
        assertEquals(
            "2/5",
            ((vm.uiState.value as SurveyUIState.ActiveQuestion).score as UIText.StringResource).args[0]
        )
    }

    @Test
    fun checkPreviousAnswerSaved() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.updateAnswer("Answer 1")
        vm.submitAnswer()
        vm.nextQuestion()
        vm.nextQuestion()
        vm.updateAnswer("Answer 2")
        vm.submitAnswer()
        vm.prevQuestion()
        vm.prevQuestion()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals("Answer 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).answer)
        assertEquals(
            "2/5",
            ((vm.uiState.value as SurveyUIState.ActiveQuestion).score as UIText.StringResource).args[0]
        )
    }

    @Test
    fun checkNotificationDissapearAfterDelay() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.submitAnswer()
        vm.messageShown()
        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, (vm.uiState.value as SurveyUIState.ActiveQuestion).isSubmitEnable)
        assertEquals(SubmissionMessages.Empty, vm.userMessages.value)
    }

    @Test
    fun checkNotificationDissapearAfterNextPressed() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.submitAnswer()
        vm.nextQuestion()
        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 2", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, (vm.uiState.value as SurveyUIState.ActiveQuestion).isSubmitEnable)
        assertEquals(SubmissionMessages.Empty, vm.userMessages.value)
    }

    @Test
    fun checkNotificationDissapearAfterPrevPressed() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.nextQuestion()
        vm.submitAnswer()
        vm.prevQuestion()

        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, (vm.uiState.value as SurveyUIState.ActiveQuestion).isSubmitEnable)
        assertEquals(SubmissionMessages.Empty, vm.userMessages.value)
    }

    @Test
    fun checkNotificationDissapearAfterTextUpdated() = runTest {
        val vm = SurveyViewModel(MockedQuestionsSuceess())
        vm.submitAnswer()
        vm.updateAnswer("Answer 1")
        val result = vm.uiState.value is SurveyUIState.ActiveQuestion
        assertEquals(result, true)
        assertEquals("Question 1", (vm.uiState.value as SurveyUIState.ActiveQuestion).question)
        assertEquals(true, (vm.uiState.value as SurveyUIState.ActiveQuestion).isSubmitEnable)
        assertEquals(SubmissionMessages.Empty, vm.userMessages.value)
    }
}