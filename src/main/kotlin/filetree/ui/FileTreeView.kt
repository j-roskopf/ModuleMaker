package filetree.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import filetree.FileTree
import ui.AppTheme

@Composable
fun FileTreeViewTabView(
    header: String
) = Surface {
    Row(
        Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            header,
            color = LocalContentColor.current.copy(alpha = 0.60f),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun FileTreeView(model: FileTree) = Surface(
    modifier = Modifier.fillMaxSize()
) {
    with(LocalDensity.current) {
        Box {
            val scrollState = rememberLazyListState()
            val fontSize = 14.sp
            val lineHeight = fontSize.toDp() * 1.5f

            LazyColumn(
                modifier = Modifier.fillMaxSize().withoutWidthConstraints(),
                state = scrollState
            ) {
                items(model.items.size) {
                    FileTreeItemView(fontSize, lineHeight, model.items[it], model.lastSelectedFile)
                }
            }

            VerticalScrollbar(
                Modifier.align(Alignment.CenterEnd),
                scrollState,
            )
        }
    }
}

@Composable
fun VerticalScrollbar(
    modifier: Modifier,
    scrollState: LazyListState,
) = VerticalScrollbar(
    remember(scrollState) {
        ScrollbarAdapter(scrollState)
    },
    modifier
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FileTreeItemView(fontSize: TextUnit, height: Dp, model: FileTree.Item, lastSelectedFile: FileTree.Item) =
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .clickable { model.open() }
            .padding(start = 24.dp * model.level)
            .height(height)
            .fillMaxWidth()
    ) {
        val active = remember { mutableStateOf(false) }

        FileItemIcon(Modifier.align(Alignment.CenterVertically), model)
        Surface(
            color = if (model.name == lastSelectedFile.name && model.level == lastSelectedFile.level) AppTheme.colors.backgroundLight else Color.Transparent,
            modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth()
        ) {
            Text(
                text = model.name,
                color = if (active.value) LocalContentColor.current.copy(alpha = 0.60f) else LocalContentColor.current,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clipToBounds()
                    .onPointerEvent(PointerEventType.Enter) {
                        active.value = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        active.value = false
                    },
                softWrap = true,
                fontSize = fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }

@Composable
private fun FileItemIcon(modifier: Modifier, model: FileTree.Item) = Box(modifier.size(24.dp).padding(4.dp)) {
    when (val type = model.type) {
        is FileTree.ItemType.Folder -> when {
            !type.canExpand -> Unit
            type.isExpanded -> Icon(
                Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LocalContentColor.current
            )

            else -> Icon(
                Icons.Default.KeyboardArrowRight, contentDescription = null, tint = LocalContentColor.current
            )
        }

        is FileTree.ItemType.File -> when (type.ext) {
            "kt" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF3E86A0))
            "java" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF3E86A0))
            "xml" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFFC19C5F))
            "txt" -> Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF87939A))
            "md" -> Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF87939A))
            "gitignore" -> Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color(0xFF87939A))
            "gradle" -> Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF87939A))
            "kts" -> Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF3E86A0))
            "properties" -> Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF62B543))
            "bat" -> Icon(Icons.Default.Launch, contentDescription = null, tint = Color(0xFF87939A))
            else -> Icon(Icons.Default.TextSnippet, contentDescription = null, tint = Color(0xFF87939A))
        }
    }
}