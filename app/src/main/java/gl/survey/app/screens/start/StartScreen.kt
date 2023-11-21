package gl.survey.app.screens.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gl.survey.app.R

@Composable
fun StartScreen(
    onNavigateToSurvey: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.start_screen_welcome_username),
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.size(50.dp))
        Button(onClick = { onNavigateToSurvey() }) {
            Text(
                text = stringResource(R.string.start_screen_check_survey),
                fontSize = 30.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    StartScreen()
}