package gl.survey.app.screens.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gl.survey.app.R
import gl.survey.app.utils.UIText
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SurveyScreen(
    viewModel: SurveyViewModel = koinViewModel(),
    onGoBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.userMessages.collectAsState()
    val questionSectionState by viewModel.questionSectionState.collectAsState()

    when (uiState) {
        is SurveyUIState.LoadingError ->
            ErrorItem(
                errorText = (uiState as SurveyUIState.LoadingError).errorText,
                viewModel::loadQuestions
            )

        is SurveyUIState.ActiveQuestion ->
            QuestionSection(
                uiState as SurveyUIState.ActiveQuestion,
                messages,
                questionSectionState,
                viewModel::messageShown,
                viewModel::submitAnswer,
                viewModel::updateAnswer,
                viewModel::nextQuestion,
                viewModel::prevQuestion
            )

        SurveyUIState.Loading -> LoadingPlaceholder()
        SurveyUIState.Empty -> EmptySurvey(onGoBackPressed)
    }
}

@Composable
fun QuestionSection(
    question: SurveyUIState.ActiveQuestion,
    messages: SubmissionMessages,
    questionSectionState: QuestionSectionState,
    onSubmissionShown: () -> Unit,
    onSubmitPressed: () -> Unit,
    onAnswerChanged: (String) -> Unit,
    onNextPressed: () -> Unit,
    onPrevPressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MessageSection(
            messages = messages,
            onSubmissionShown = onSubmissionShown,
            onRetrySubmission = onSubmitPressed
        )
        ActiveQuestionItem(
            question = question,
            onSubmitPressed = onSubmitPressed,
            onAnswerChanged = onAnswerChanged,
        )
        Spacer(modifier = Modifier.weight(1f))
        QuestionSelectionSection(
            questionSectionState = questionSectionState,
            onNextPressed = onNextPressed,
            onPrevPressed = onPrevPressed
        )
    }
}

@Composable
fun MessageSection(
    messages: SubmissionMessages,
    onSubmissionShown: () -> Unit,
    onRetrySubmission: () -> Unit
) {
    when (messages) {
        SubmissionMessages.BlankAnswer -> BlankAnswerWarning(onSubmissionShown)
        SubmissionMessages.Empty -> { /* no-op */
        }

        SubmissionMessages.SubmissionFails -> SubmissionFailsMessage(
            onSubmissionShown,
            onRetrySubmission
        )

        SubmissionMessages.SubmissionSuccess -> SubmissionSuccessMessage(onSubmissionShown)
    }
}

@Composable
fun BlankAnswerWarning(
    onSubmissionShown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Yellow)
            .padding(16.dp)
    ) {
        LaunchedEffect(key1 = Unit) {
            delay(3000)
            onSubmissionShown()
        }
        Text(text = stringResource(id = R.string.validation_empty_string), fontSize = 20.sp)
    }
}

@Composable
fun SubmissionSuccessMessage(
    onSubmissionShown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green)
            .padding(16.dp)
    ) {
        LaunchedEffect(key1 = Unit) {
            delay(3000)
            onSubmissionShown()
        }
        Text(text = stringResource(id = R.string.success_answer_submited), fontSize = 20.sp)
    }
}

@Composable
fun SubmissionFailsMessage(
    onSubmissionShown: () -> Unit,
    onRetrySubmission: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LaunchedEffect(key1 = Unit) {
            delay(3000)
            onSubmissionShown()
        }
        Text(
            modifier = Modifier.weight(.6f),
            text = stringResource(id = R.string.error_submission_fails),
            fontSize = 20.sp
        )
        Button(
            modifier = Modifier.weight(.4f),
            onClick = onRetrySubmission
        ) {
            Text(text = stringResource(R.string.survey_screen_try_again), fontSize = 20.sp)
        }
    }
}

@Composable
fun ActiveQuestionItem(
    question: SurveyUIState.ActiveQuestion,
    onSubmitPressed: () -> Unit,
    onAnswerChanged: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = question.question,
            fontSize = 20.sp
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            enabled = question.isSubmitEnable,
            value = question.answer,
            onValueChange = onAnswerChanged
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = question.score.asString(),
                fontSize = 20.sp
            )
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterEnd),
                enabled = question.isSubmitEnable,
                onClick = onSubmitPressed
            ) {
                Text(text = stringResource(R.string.survey_screen_submit), fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun QuestionSelectionSection(
    questionSectionState: QuestionSectionState,
    onNextPressed: () -> Unit,
    onPrevPressed: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Button(
            modifier = Modifier.align(Alignment.CenterStart),
            enabled = questionSectionState.isPrevEnable,
            onClick = onPrevPressed
        ) {
            Text(text = stringResource(R.string.survey_screen_prev), fontSize = 20.sp)
        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = questionSectionState.currentText.asString(),
            fontSize = 20.sp
        )
        Button(
            modifier = Modifier.align(Alignment.CenterEnd),
            enabled = questionSectionState.isNextEnabled,
            onClick = onNextPressed
        ) {
            Text(text = stringResource(R.string.survey_screen_next), fontSize = 20.sp)
        }
    }
}

@Composable
fun LoadingPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(128.dp)
                .padding(16.dp)
        )

        Text(
            text = stringResource(R.string.survey_screen_loading),
            fontSize = 30.sp
        )
    }
}

@Composable
fun ErrorItem(
    errorText: UIText,
    onRetryPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorText.asString(),
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.size(30.dp))
        Button(onClick = onRetryPressed) {
            Text(text = stringResource(id = R.string.survey_screen_try_again), fontSize = 30.sp)
        }
    }
}

@Composable
fun EmptySurvey(
    onGoBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.survey_screen_no_available_questions_for_now),
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.size(30.dp))
        Button(onClick = onGoBackPressed) {
            Text(text = stringResource(R.string.survey_screen_go_back), fontSize = 30.sp)
        }
    }
}

//======================Preview's===========================

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    LoadingPlaceholder()
}

@Preview(showBackground = true)
@Composable
fun LoadingErrorPreview() {
    ErrorItem(UIText.StringResource(R.string.error_check_network_connection), {})
}

@Preview(showBackground = true)
@Composable
fun EmptySurveyPreview() {
    EmptySurvey {}
}

@Preview(showBackground = true)
@Composable
fun SurveyPreviewRegular() {
    QuestionSection(
        question = SurveyUIState.ActiveQuestion(
            "What is your Favourite Color?",
            "blah",
            isSubmitEnable = true,
            UIText.StringText("0/5 Submitted")
        ),
        questionSectionState = QuestionSectionState(UIText.StringText("2/5 Question"), true, true),
        onSubmissionShown = {},
        onSubmitPressed = {},
        onAnswerChanged = {},
        onNextPressed = {},
        onPrevPressed = {},
        messages = SubmissionMessages.Empty
    )
}

@Preview(showBackground = true)
@Composable
fun SurveyPreviewBlankWarning() {
    QuestionSection(
        question = SurveyUIState.ActiveQuestion(
            "What is your Favourite Color?",
            "blah",
            isSubmitEnable = true,
            UIText.StringText("0/5 Submitted")
        ),
        questionSectionState = QuestionSectionState(UIText.StringText("2/5 Question"), true, true),
        onSubmissionShown = {},
        onSubmitPressed = {},
        onAnswerChanged = {},
        onNextPressed = {},
        onPrevPressed = {},
        messages = SubmissionMessages.BlankAnswer
    )
}

@Preview(showBackground = true)
@Composable
fun SurveyPreviewSubmitSuccess() {
    QuestionSection(
        question = SurveyUIState.ActiveQuestion(
            "What is your Favourite Color?",
            "blah",
            isSubmitEnable = true,
            UIText.StringText("0/5 Submitted")
        ),
        questionSectionState = QuestionSectionState(UIText.StringText("2/5 Question"), true, true),
        onSubmissionShown = {},
        onSubmitPressed = {},
        onAnswerChanged = {},
        onNextPressed = {},
        onPrevPressed = {},
        messages = SubmissionMessages.SubmissionSuccess
    )
}

@Preview(showBackground = true)
@Composable
fun SurveyPreviewSubmitFails() {
    QuestionSection(
        question = SurveyUIState.ActiveQuestion(
            "What is your Favourite Color?",
            "blah",
            isSubmitEnable = true,
            UIText.StringText("0/5 Submitted")
        ),
        questionSectionState = QuestionSectionState(UIText.StringText("2/5 Question"), true, true),
        onSubmissionShown = {},
        onSubmitPressed = {},
        onAnswerChanged = {},
        onNextPressed = {},
        onPrevPressed = {},
        messages = SubmissionMessages.SubmissionFails
    )
}