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
        if (!mpsDir.exists()) {
            println("MPS Project does not exist, creating it")
            val mpsInternalDir = File(mpsDir, ".mps")
            mpsInternalDir.mkdirs()

            val nameFile = File(mpsInternalDir, ".name")
            nameFile.writeText(project.name)

            val modulesFile = File(mpsInternalDir, "modules.xml")
            modulesFile.writeText("""<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="MPSProject">
    <projectModules>
    </projectModules>
  </component>
</project>""")
        }
    }

    companion object {
        const val TASK_NAME = "generateMpsProject"
    }
}