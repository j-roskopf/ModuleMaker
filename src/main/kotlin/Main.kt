import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import composables.SelectedDirectory
import composables.ModuleName
import composables.ModuleType
import composables.SelectedSettingsGradle
import data.ModuleType
import file.FileWriter
import filetree.*
import filetree.ui.FileTreeView
import filetree.ui.FileTreeViewTabView
import filetree.ui.PanelState
import filetree.ui.ResizablePanel
import ui.AppTheme
import ui.PlatformTheme

val fileWriter = FileWriter()

val fileTree = FileTree(RootFolder, false)
val rootProjectFileTree = FileTree(RootFolder, true)

fun main() = singleWindowApplication(
    title = "Module Maker",
    state = WindowState(width = 1280.dp, height = 768.dp),
) {
    MaterialTheme(
        colors = AppTheme.colors.material
    ) {
        PlatformTheme {
            Surface {
                MainView()
            }
        }
    }
}

@Composable
fun MainView() {
    Row(Modifier.fillMaxSize()) {
        val currentlySelectedModuleRoot = remember { mutableStateOf(fileTree.lastSelectedFile.file.file.jvmFile) }
        val onSelectedFileChange = { file: File ->
            currentlySelectedModuleRoot.value = file.jvmFile
        }

        val currentlySelectedSettingsGradle =
            remember { mutableStateOf(rootProjectFileTree.lastSelectedFile.file.file.jvmFile) }
        val onSelectedRootProjectFileChange = { file: File ->
            currentlySelectedSettingsGradle.value = file.jvmFile
        }

        Column {
            FileTreeColumn(
                modifier = Modifier.weight(1f),
                onSelectedFileChange = onSelectedRootProjectFileChange,
                header = "Select settings.gradle(.kts) file",
                fileTree = rootProjectFileTree,
            )
            FileTreeColumn(
                modifier = Modifier.weight(1f),
                onSelectedFileChange = onSelectedFileChange,
                header = "Select root module location",
                fileTree = fileTree,
            )
        }

        ModuleMakerColumn(currentlySelectedModuleRoot.value, currentlySelectedSettingsGradle.value)
    }
}

@Composable
fun ModuleMakerColumn(currentlySelectedFile: java.io.File, settingsGradle: java.io.File) {

    val showErrorDialog = remember { mutableStateOf(false) }
    val showSuccessDialog = remember { mutableStateOf(false) }


    var selectedModuleName by remember { mutableStateOf("") }
    val onModuleNameChange = { text: String ->
        selectedModuleName = text
    }

    var selectedModuleType by remember { mutableStateOf(ModuleType.ANDROID) }
    val onModuleTypeChange = { moduleType: ModuleType ->
        selectedModuleType = moduleType
    }

    Column(Modifier.wrapContentWidth().fillMaxHeight(), Arrangement.spacedBy(5.dp)) {

        SelectedDirectory(
            currentlySelectedFile.absolutePath
        )

        SelectedSettingsGradle(
            settingsGradle.absolutePath
        )

        ModuleType(
            selected = selectedModuleType,
            onSelectedChange = onModuleTypeChange
        )

        ModuleName(
            onModuleNameChange
        )

        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                fileWriter.createModule(
                    settingsGradleFile = settingsGradle,
                    modulePathAsString = selectedModuleName,
                    moduleType = selectedModuleType,
                    showErrorDialog = showErrorDialog,
                    showSuccessDialog = showSuccessDialog,
                    workingDirectory = currentlySelectedFile
                )
            }
        ) {
            Text("Create")
        }

        if (showErrorDialog.value) {
            AlertDialog(
                title = "Error",
                body = "Please enter a valid name",
                confirmButtonText = "Okay",
                confirmOnClick = {
                    showErrorDialog.value = false
                },
                onDismissRequest = {
                    showErrorDialog.value = false
                }
            )
        }

        if (showSuccessDialog.value) {
            AlertDialog(
                title = "Success!",
                body = "Module created",
                confirmButtonText = "Okay",
                confirmOnClick = {
                    showSuccessDialog.value = false
                },
                onDismissRequest = {
                    showSuccessDialog.value = false
                }
            )
        }
    }
}

@Composable
fun FileTreeColumn(
    modifier: Modifier,
    onSelectedFileChange: (File) -> Unit,
    header: String,
    fileTree: FileTree,
) {
    val panelState = remember { PanelState() }

    val animatedSize = if (panelState.splitter.isResizing) {
        if (panelState.isExpanded) panelState.expandedSize else panelState.collapsedSize
    } else {
        animateDpAsState(
            if (panelState.isExpanded) panelState.expandedSize else panelState.collapsedSize,
            SpringSpec(stiffness = Spring.StiffnessLow)
        ).value
    }

    fileTree.setSelectedFileChangeListener(onSelectedFileChange)

    Column(modifier.wrapContentWidth(), Arrangement.spacedBy(5.dp)) {
        ResizablePanel(Modifier.width(animatedSize).fillMaxHeight(), panelState) {
            Column {
                FileTreeViewTabView(header)
                FileTreeView(fileTree)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlertDialog(
    title: String,
    body: String,
    confirmButtonText: String,
    confirmOnClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AlertDialog(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .fillMaxWidth(0.25f)
                    .fillMaxHeight(0.25f),
                onDismissRequest = {
                    onDismissRequest()
                },
                title = {
                    Text(text = title)
                },
                text = {
                    Text(text = body)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmOnClick.invoke()
                        }) {
                        Text(confirmButtonText)
                    }
                },
            )
        }
    }
}
