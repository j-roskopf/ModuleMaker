package file

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import composables.settings.API_KEY
import composables.settings.GLUE_KEY
import composables.settings.IMPL_KEY
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
        enhancedModuleCreationStrategy: Boolean,
        useKtsBuildFile: Boolean,
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
            enhancedModuleCreationStrategy = enhancedModuleCreationStrategy,
        )

        if (enhancedModuleCreationStrategy) {
            createEnhancedModuleStructure(
                moduleFile = moduleFile,
                moduleType = moduleType,
                useKtsBuildFile = useKtsBuildFile,
            )
        } else {
            createDefaultModuleStructure(
                moduleFile = moduleFile,
                moduleName = moduleName,
                moduleType = moduleType,
                useKtsBuildFile = useKtsBuildFile,
            )
        }

        showSuccessDialog.value = true
    }

    private fun createEnhancedModuleStructure(
        moduleFile: File,
        moduleType: ModuleType,
        useKtsBuildFile: Boolean
    ) {
        // make the 3 module
        moduleFile.toPath().resolve("glue").toFile().apply {
            mkdirs()
            // create the gradle file
            templateWriter.createGradleFile(
                moduleFile = this,
                moduleName = "glue",
                moduleType = moduleType,
                useKtsBuildFile = useKtsBuildFile,
                defaultKey = GLUE_KEY,
            )

            // create default packages
            createDefaultPackages(
                moduleFile = this,
            )
        }
        moduleFile.toPath().resolve("impl").toFile().apply {
            mkdirs()
            templateWriter.createGradleFile(
                moduleFile = this,
                moduleName = "impl",
                moduleType = moduleType,
                useKtsBuildFile = useKtsBuildFile,
                defaultKey = IMPL_KEY,
            )

            // create default packages
            createDefaultPackages(
                moduleFile = this,
            )
        }
        moduleFile.toPath().resolve("api").toFile().apply {
            mkdirs()
            templateWriter.createGradleFile(
                moduleFile = this,
                moduleName = "api",
                moduleType = moduleType,
                useKtsBuildFile = useKtsBuildFile,
                defaultKey = API_KEY,
            )

            // create readme file for the api module
            templateWriter.createReadmeFile(
                moduleFile = this,
                moduleName = "api",
            )

            // create default packages
            createDefaultPackages(
                moduleFile = this,
            )
        }
    }

    private fun createDefaultModuleStructure(
        moduleFile: File,
        moduleName: String,
        moduleType: ModuleType,
        useKtsBuildFile: Boolean,
    ) {
        // create gradle files
        templateWriter.createGradleFile(
            moduleFile = moduleFile,
            moduleName = moduleName,
            moduleType = moduleType,
            useKtsBuildFile = useKtsBuildFile,
            defaultKey = null,
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
        enhancedModuleCreationStrategy: Boolean,
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
        val includeProjectStatements = settingsFile.subList(
            firstLineNumberOfFirstIncludeProjectStatement,
            lastLineNumberOfFirstIncludeProjectStatement + 1
        ).toMutableList()

        val textToWrite = if (enhancedModuleCreationStrategy) {
            "$projectIncludeKeyword(\"".plus(modulePathAsString.plus(":api")).plus("\")").plus("\n")
                .plus("$projectIncludeKeyword(\"".plus(modulePathAsString.plus(":impl")).plus("\")")).plus("\n")
                .plus("$projectIncludeKeyword(\"".plus(modulePathAsString.plus(":glue")).plus("\")"))
        } else {
            "$projectIncludeKeyword(\"".plus(modulePathAsString).plus("\")")
        }

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
