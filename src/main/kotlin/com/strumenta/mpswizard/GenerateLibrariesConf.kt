package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*
import javax.inject.Inject

open class GenerateLibrariesConf @Inject constructor(val mpsWizard: MpsWizardPlugin, val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.dependsOn.add(ValidateMpsWizardConfiguration.TASK_NAME)
        this.group = MPSWIZARD_TASKS_GROUP
        this.description = "Generate the libraries.xml file for the MPS Project"
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
        val mpsDir = mpsWizard.mpsDir(project)
        val mpsInternalDir = File(mpsDir, ".mps")
        if (!mpsInternalDir.exists()) {
            mpsInternalDir.mkdirs()
        }

        val entries = LinkedList<String>()

        if (extension.actualUseMbeddr) {
            entries.add(entry("mbeddr.platform", "com.mbeddr.platform"))
            entries.add(entry("mbeddr.buildscripts", "com.mbeddr.allScripts.build"))
        }

        if (extension.actualUseIets3) {
            entries.add(entry("iets3.opensource", "org.iets3.opensource"))
        }

        val librariesXml = File(mpsInternalDir, "libraries.xml")
        librariesXml.writeText("""<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectLibraryManager">
    <option name="libraries">
      <map>
      ${entries.joinToString("\n")}
      </map>
    </option>
  </component>
</project>""")
        println("${librariesXml.absolutePath} created")
    }

    companion object {
        const val TASK_NAME = "generateLibrariesConf"
    }
}