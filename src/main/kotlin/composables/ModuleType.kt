package composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.ModuleType

@Composable
fun ModuleType(
    selected: ModuleType,
    onSelectedChange: (ModuleType) -> Unit
) {

    val radioGroupOptions = listOf(
        ModuleType.KOTLIN,
        ModuleType.ANDROID
    )

    Column(modifier = Modifier.padding(top = 16.dp)) {
        radioGroupOptions.forEach { moduleType ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (moduleType == selected),
                        onClick = {
                            onSelectedChange(moduleType)
                        }
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (moduleType == selected),
                    onClick = {
                        onSelectedChange(moduleType)
                    }
                )
                Text(
                    text = moduleType.name,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}
