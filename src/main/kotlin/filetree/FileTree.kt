package filetree

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ExpandableFile(
    val file: File,
    val level: Int,
) {
    var children: List<ExpandableFile> by mutableStateOf(emptyList())
    val canExpand: Boolean get() = file.hasChildren

    fun toggleExpanded() {
        children = if (children.isEmpty()) {
            file.children
                .map { ExpandableFile(it, level + 1) }
                .sortedWith(compareBy({ it.file.isDirectory }, { it.file.name }))
                .sortedBy { !it.file.isDirectory }
        } else {
            emptyList()
        }
    }
}

class FileTree(root: File, val allowFileSelection: Boolean) {

    var lastSelectedFile: Item
    var onSelectedFileChange: ((File) -> Unit)? = null

    init {
        lastSelectedFile = Item(ExpandableFile(root, 0), allowFileSelection)
    }

    private val expandableRoot = ExpandableFile(root, 0).apply {
        toggleExpanded()
    }

    val items: List<Item> get() = expandableRoot.toItems(allowFileSelection)

    inner class Item constructor(
        val file: ExpandableFile,
        val allowFileSelection: Boolean,
    ) {
        val name: String get() = file.file.name

        val level: Int get() = file.level

        val type: ItemType
            get() = if (file.file.isDirectory) {
                ItemType.Folder(isExpanded = file.children.isNotEmpty(), canExpand = file.canExpand)
            } else {
                ItemType.File(ext = file.file.name.substringAfterLast(".").lowercase())
            }

        fun open() = when (type) {
            is ItemType.Folder -> {
                lastSelectedFile = Item(ExpandableFile(file = file.file, level = this.level), allowFileSelection)
                onSelectedFileChange?.invoke(lastSelectedFile.file.file)
                file.toggleExpanded()
            }
            is ItemType.File -> {
                // we only want to allow file selection when selecting for the settings.gradle file
                if(allowFileSelection) {
                    lastSelectedFile = Item(ExpandableFile(file = file.file, level = this.level), true)
                    onSelectedFileChange?.invoke(lastSelectedFile.file.file)
                } else {
                    Unit
                }
            }
        }
    }

    sealed class ItemType {
        class Folder(val isExpanded: Boolean, val canExpand: Boolean) : ItemType()
        class File(val ext: String) : ItemType()
    }

    private fun ExpandableFile.toItems(allowFileSelection: Boolean): List<Item> {
        fun ExpandableFile.addTo(list: MutableList<Item>) {
            list.add(Item(this, allowFileSelection))
            for (child in children) {
                child.addTo(list)
            }
        }

        val list = mutableListOf<Item>()
        addTo(list)
        return list
    }

    fun setSelectedFileChangeListener(onSelectedFileChange: (File) -> Unit) {
        this.onSelectedFileChange = onSelectedFileChange
    }
}
