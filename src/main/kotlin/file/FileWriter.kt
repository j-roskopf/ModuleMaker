package file

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import data.ModuleType
import template.TemplateWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

/**
 * This class is responsible for writing files into the project
 */
class FileWriter {

    private val templateWriter = TemplateWriter()

    fun createModule(
        settingsGradleFile: File,
        workingDirectory: File,
        modulePathAsString: String,
        moduleType: ModuleType,
        showErrorDialog: MutableState<Boolean>,
        showSuccessDialog: MutableState<Boolean>,
    ) {
        val fileReady = modulePathAsString.replace(":", "/")

        val path = Paths.get(workingDirectory.toURI())
        val modulePath = Paths.get(path.toString(), fileReady)
        val moduleFile = File(modulePath.absolutePathString())

        // get the actual module name, not the path. at this point, it will be something like :experiences:foo
        val moduleName = modulePathAsString.split(":").last()

        if (moduleName.isEmpty()) {
            // display alert
            showErrorDialog.value = true
            return
        }

        // create if it doesn't exist
        moduleFile.mkdirs()

        // add to settings.gradle.kts
        addToSettingsAtCorrectLocation(
            modulePathAsString = modulePathAsString,
            settingsGradleFile = settingsGradleFile,
        )

        // create gradle files
        templateWriter.createGradleFile(
            moduleFile = moduleFile,
            moduleName = moduleName,
            moduleType = moduleType,
        )

        // create readme file
        templateWriter.createReadmeFile(
            moduleFile = moduleFile,
            moduleName = moduleName,
        )

        if (moduleType == ModuleType.ANDROID) {
            // only android modules need a manifest
            templateWriter.createManifest(
                moduleFile = moduleFile,
                moduleName = moduleName,
            )
        }

        // create default packages
        createDefaultPackages(
            moduleFile = moduleFile,
        )

        showSuccessDialog.value = true
    }

    /**
     * Creates the default package name
     *
     * Gives the module a src/main/kotlin folder with com.<group> name
     */
    private fun createDefaultPackages(moduleFile: File) {
        // create src/main
        val srcPath = Paths.get(moduleFile.absolutePath, "src/main/kotlin").toFile()
        srcPath.mkdirs()

        // create default package
        val stringBuilder = StringBuilder()

        Paths.get(srcPath.absolutePath, stringBuilder.toString()).toFile().mkdirs()
    }

    /**
     * Inserts the entry into settings.gradle.kts at the correct spot to maintain alphabetical order
     *
     * This assumes the file was in alphabetical order to begin with
     */
    private fun addToSettingsAtCorrectLocation(
        settingsGradleFile: File,
        modulePathAsString: String,
    ) {

        val settingsFile = Files.readAllLines(Paths.get(settingsGradleFile.toURI()))

        val includeProject = "includeProject"
        val include = "include"

        // TODO - add ability to specify keyword
        val projectIncludeKeyword = if (settingsFile.contains(includeProject)) {
            includeProject
        } else {
            include
        }

        // get the first and last line numbers for an include statement
        val firstLineNumberOfFirstIncludeProjectStatement = settingsFile.indexOfFirst {
            it.contains("$projectIncludeKeyword(\"")
        }

        val lastLineNumberOfFirstIncludeProjectStatement = settingsFile.indexOfLast {
            it.contains("$projectIncludeKeyword(\"")
        }

        // sub list them and create a new list so we aren't modifying the original
        val includeProjectStatements = settingsFile.subList(firstLineNumberOfFirstIncludeProjectStatement, lastLineNumberOfFirstIncludeProjectStatement + 1).toMutableList()

        val textToWrite = "$projectIncludeKeyword(\"".plus(modulePathAsString).plus("\")")

        // the spot we want to insert it is the first line we find that is after it alphabetically
        val insertionIndex = includeProjectStatements.indexOfFirst {
            it.isNotEmpty() && it.toLowerCase(Locale.current) >= textToWrite.toLowerCase(Locale.current)
        }

        if (insertionIndex < 0) {
            // insert it at the end as nothing is past it
            settingsFile.add(lastLineNumberOfFirstIncludeProjectStatement + 1, textToWrite)
        } else {
            // insert it in our original list adding the original offset of the first line
            settingsFile.add(insertionIndex + firstLineNumberOfFirstIncludeProjectStatement, textToWrite)
        }

        Files.write(Paths.get(settingsGradleFile.toURI()), settingsFile)
    }
}
