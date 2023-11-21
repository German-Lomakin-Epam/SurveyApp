package gl.survey.app.screens.survey

sealed class SubmissionMessages {
    data object Empty: SubmissionMessages()
    data object BlankAnswer: SubmissionMessages()
    data object SubmissionSuccess : SubmissionMessages()
    data object SubmissionFails : SubmissionMessages()
}