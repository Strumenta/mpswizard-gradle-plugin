package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class GenerateMpsProject @Inject constructor(val mpsWizard: MpsWizardPlugin, val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.dependsOn.add(ValidateMpsWizardConfiguration.TASK_NAME)
        this.dependsOn.add(GenerateLibrariesConf.TASK_NAME)
        this.group = MPSWIZARD_TASKS_GROUP
        this.description = "Generate an empty MPS project"
    }

    @TaskAction
    fun execute() {
        val mpsDir = mpsWizard.mpsDir(project)
        val mpsInternalDir = File(mpsDir, ".mps")
        if (!mpsInternalDir.exists()) {
            mpsInternalDir.mkdirs()
        }

        val nameFile = File(mpsInternalDir, ".name")
        if (!nameFile.exists()) {
            nameFile.writeText(project.name)
            println("${nameFile.absolutePath} created")
        }

        val modulesFile = File(mpsInternalDir, "modules.xml")
        if (!modulesFile.exists()) {
            modulesFile.writeText("""<?xml version="1.0" encoding="UTF-8"?>
    <project version="4">
    <component name="MPSProject">
    <projectModules>
    </projectModules>
    </component>
    </project>""")
            println("${modulesFile.absolutePath} created")
        }
    }

    companion object {
        const val TASK_NAME = "generateMpsProject"
    }
}