package filetree

import kotlinx.coroutines.CoroutineScope

val RootFolder: File = java.io.File(System.getProperty("user.dir")).parentFile.parentFile.toProjectFile()

interface File {
    val name: String
    val jvmFile: java.io.File
    val isDirectory: Boolean
    val children: List<File>
    val hasChildren: Boolean

    fun readLines(scope: CoroutineScope): TextLines
}
