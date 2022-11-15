package composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ModuleName(
    textChanged: (String) -> Unit
) {
    var text = remember {
        mutableStateOf("")
    }

    return Row(modifier = Modifier.padding(top = 16.dp)) {
        TextField(
            value = TextFieldValue(
                text = text.value,
                // always set to end of input
                selection = TextRange(text.value.length)
            ),
            modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
            label = { Text("Enter module name (example :repository:database)") },
            onValueChange = {
                text.value = it.text
                textChanged(it.text)
            }
        )
    }
}
