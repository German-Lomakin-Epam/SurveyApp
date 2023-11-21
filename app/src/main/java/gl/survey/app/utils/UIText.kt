package gl.survey.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UIText {
    data object Empty : UIText()
    class StringText(val text: String) : UIText()
    class StringResource(val id: Int, vararg val args: Any) : UIText()

    @Composable
    fun asString(): String {
        return when(this) {
            is StringResource -> stringResource(id = this.id, formatArgs = this.args)
            is StringText -> this.text
            Empty -> ""
        }
    }
}