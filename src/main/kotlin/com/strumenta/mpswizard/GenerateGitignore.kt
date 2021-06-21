package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*
import javax.inject.Inject

open class GenerateGitignore @Inject constructor(
    @Internal val mpsWizard: MpsWizardPlugin, @Internal val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.dependsOn.add(ValidateMpsWizardConfiguration.TASK_NAME)
        this.group = MPSWIZARD_TASKS_GROUP
        this.description = "Generate .gitignore file for MPS project"
    }

    private fun entry(name: String, dirName: String) : String {
        return """<entry key="$name">
          <value>
            <Library>
              <option name="name" value="$name" />
              <option name="path" value="${'$'}PROJECT_DIR${'$'}/artifacts/$dirName" />
            </Library>
          </value>
        </entry>"""
    }

    @TaskAction
    fun execute() {
        val gitignore = File(project.rootDir, ".gitignore")
        val lines = if (gitignore.exists()) gitignore.readLines().map { it.trim() } else emptyList()
        val expectedLines = listOf(
                "artifacts",
                "**/classes_gen",
                "**/source_gen",
                "**/source_gen.caches",
                "**/test_gen",
                "**/test_gen.caches",
                ".gradle",
                ".mps/workspace.xml",
                ".mps/terminal.xml",
                "build"
        )
        val missingLines = expectedLines.filter { !lines.contains(it) }
        if (missingLines.isNotEmpty()) {
            gitignore.appendText(missingLines.joinToString("\n"))
            println("updating .gitignore file")
        }

    }

    companion object {
        const val TASK_NAME = "generateGitignore"
    }
}