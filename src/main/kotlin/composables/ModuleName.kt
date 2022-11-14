package composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ModuleName(
    textChanged: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }

    return Row(modifier = Modifier.padding(top = 16.dp)) {
        TextField(
            value = TextFieldValue(
                text = text,
                // always set to end of input
                selection = TextRange(text.length)
            ),
            modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),
            label = { Text("Enter module name (example :repository:database)") },
            onValueChange = {
                text = it.text
                textChanged(it.text)
            }
        )
    }
}