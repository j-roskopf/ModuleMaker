package template

import data.ModuleType
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import java.io.*
import java.nio.file.Paths

class TemplateWriter {

    /**
     * Creates gradle file for the module from base gradle template file
     */
    fun createGradleFile(
        moduleFile: File,
        moduleName: String,
        moduleType: ModuleType,
    ) {
        val cfg = Configuration()

        try {
            // load gradle file from template folder
            val gradleTemplate: Template = cfg.getTemplate("src/main/resources/moduleGradle.ftl")

            // Build the data-model
            val data: MutableMap<String, Any> = HashMap()
            when (moduleType) {
                // TODO - allow for custom templates
                ModuleType.KOTLIN -> data["isKotlinLibrary"] = true
                ModuleType.ANDROID -> data["isAndroidLibrary"] = true
                ModuleType.UNKNOWN -> throw IllegalArgumentException("Unknown module type")
            }

            // File output
            val file: Writer = FileWriter(Paths.get(moduleFile.absolutePath, moduleName.plus(".gradle")).toFile())
            gradleTemplate.process(data, file)
            file.flush()
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TemplateException) {
            e.printStackTrace()
        }
    }

    /**
     * Creates manifest from base manifest template file
     */
    fun createManifest(
        moduleFile: File,
        moduleName: String,
    ) {
        val cfg = Configuration()

        try {
            val manifestTemplate: Template = cfg.getTemplate("src/main/resources/moduleManifest.ftl")

            val data: MutableMap<String, Any> = HashMap()

            // dashes are not valid characters for a manifest. there are others, but hopefully sensible naming conventions
            // prevent those from showing up
            data["moduleName"] = moduleName.replace("-","_")

            // create directory for manifest
            val manifestFile = Paths.get(moduleFile.absolutePath, "src/main/").toFile()
            manifestFile.mkdirs()

            // File output
            val file: Writer = FileWriter(Paths.get(manifestFile.absolutePath, "AndroidManifest.xml").toFile())
            manifestTemplate.process(data, file)
            file.flush()
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TemplateException) {
            e.printStackTrace()
        }
    }

    fun createReadmeFile(moduleFile: File, moduleName: String) {
        val cfg = Configuration()

        try {
            val manifestTemplate: Template = cfg.getTemplate("src/main/resources/moduleReadme.ftl")

            val data: MutableMap<String, Any> = HashMap()

            data["moduleName"] = moduleName

            // create directory for the readme
            val manifestFile = Paths.get(moduleFile.absolutePath).toFile()
            manifestFile.mkdirs()

            // File output
            val file: Writer = FileWriter(Paths.get(manifestFile.absolutePath, "README.md").toFile())
            manifestTemplate.process(data, file)
            file.flush()
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TemplateException) {
            e.printStackTrace()
        }
    }
}