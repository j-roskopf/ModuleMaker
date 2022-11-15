package composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectedSettingsGradle(
    text: String,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Selected settings.gradle(.kts) : ",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )
    }
}
