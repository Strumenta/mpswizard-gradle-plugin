package com.strumenta.mpswizard

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import java.io.File
import java.net.URI
import java.nio.file.Files

private fun Project.findConfiguration(name: String) = this.configurations.find { it.name == name }
private fun Project.findMpsConfiguration() = this.findConfiguration("mps")
private fun Project.findMpsArtifactsConfiguration() = this.findConfiguration("mpsArtifacts")

const val MPSWIZARD_TASKS_GROUP = "MPS Wizard"

// Configuration:
//
// mpssetup { autosetconfigurations: true, mpsVersion: 2020.1.4 }

class MpsWizardPlugin : Plugin<Project> {
    var autosettersRun = HashSet<String>()

    private fun <T: Task>addTaskIfDoesNotExist(project: Project, name: String, clazz: Class<T>, extension: MpsWizardExtension) {
        if (project.tasks.findByName(name) != null) {
            println("not adding $name task, as it exists already")
        } else {
            project.tasks.register(name, clazz, this, extension)
        }
    }

    override fun apply(project: Project) {

        val extension = project.extensions.create("mpsWizard", MpsWizardExtension::class.java)

        // TODO check in configuration if this is necessary

        addTaskIfDoesNotExist(project, ValidateMpsWizardConfiguration.TASK_NAME, ValidateMpsWizardConfiguration::class.java, extension)
        addTaskIfDoesNotExist(project, "resolveMps", ResolveMps::class.java, extension)
        addTaskIfDoesNotExist(project, "resolveMpsArtifacts", ResolveMpsArtifacts::class.java, extension)
        addTaskIfDoesNotExist(project, GenerateMpsProject.TASK_NAME, GenerateMpsProject::class.java, extension)
        addTaskIfDoesNotExist(project, GenerateLibrariesConf.TASK_NAME, GenerateLibrariesConf::class.java, extension)
        addTaskIfDoesNotExist(project, GenerateGitignore.TASK_NAME, GenerateGitignore::class.java, extension)
        addTaskIfDoesNotExist(project, "setupMpsProject", SetupMpsProject::class.java, extension)

    }

    private fun checkOnlyOneRun(name: String) : Boolean {
        return if (autosettersRun.contains(name)) {
            true
        } else {
            autosettersRun.add(name)
            false
        }
    }

    fun autoSetRepositories(project: Project) {
        if (checkOnlyOneRun("repositiories")) return
        println("adding itemis mbeddr repository")
        project.repositories.maven { it.url = URI("https://projects.itemis.de/nexus/content/repositories/mbeddr") }
    }

    private fun addDependencyIfNotPresent(project: Project, configuration: Configuration,
                                          group: String, name: String, versionCalculator: () -> String) {
        val matchingDependency = configuration.dependencies.find {
            it.group == group && it.name == name
        }
        if (matchingDependency == null) {
            val version = versionCalculator()
            println("${group}.${name} not present among dependencies, adding version $version")
            project.dependencies.add(configuration.name, "$group:$name:$version")
        } else {
            println("${group}.${name} present among dependencies, not adding")
        }
    }

    fun autoSetDependencies(project: Project, extension: MpsWizardExtension) {
        if (checkOnlyOneRun("dependencies")) return

        // If mps dependencies are already set, do nothing, otherwise set the desired MPS version
        val mpsConf = project.findMpsConfiguration() ?: throw GradleException("no mps configuration")
        if (mpsConf.allDependencies.isEmpty()) {
            println("no mps configuration dependency, adding version ${extension.actualMpsVersion}")
            project.dependencies.add("mps", "com.jetbrains:mps:${extension.actualMpsVersion}")
        } else {
            println("mps configuration dependency present, not adding")
        }

        val mpsArtifactsConf = project.findMpsArtifactsConfiguration()!!
        if (extension.actualUseMbeddr) {
            addDependencyIfNotPresent(project, mpsArtifactsConf, "com.mbeddr", "platform") { extension.actualMbeddrVersion }
            addDependencyIfNotPresent(project, mpsArtifactsConf, "com.mbeddr", "allScripts") { extension.actualMbeddrVersion }
        }
        if (extension.actualUseIets3) {
            addDependencyIfNotPresent(project, mpsArtifactsConf, "org.iets3", "opensource") { extension.actualIets3Version }
        }
        if (extension.actualMPSServer) {
            addDependencyIfNotPresent(project, mpsArtifactsConf, "com.strumenta.mpsserver", "mpsserver-core") { extension.actualMPSServerVersion }
        }
    }

    fun autoSetConfigurations(project: Project) {
        if (checkOnlyOneRun("configurations")) return
        val mpsConf = project.findMpsConfiguration()
        if (mpsConf == null) {
            println("adding mps to configurations")
            project.configurations.create("mps")
        } else {
            println("mps configuration already present")
        }
        val mpsArtifactsConf = project.findMpsArtifactsConfiguration()
        if (mpsArtifactsConf == null) {
            println("adding mpsArtifacts to configurations (with transitive = false)")
            project.configurations.create("mpsArtifacts") {
                it.isTransitive = false
            }
        } else {
            println("mpsArtifacts configuration already present, not adding")
        }
    }

    fun mpsDir(project: Project) : File {
        return File(project.rootDir, "mps")
    }

    fun artifactsDir(project: Project) : File {
        return File(project.rootDir, "artifacts")
    }

    fun configurationDir(project: Project, configurationName: String) : File {
        return File(artifactsDir(project), configurationName)
    }

    fun installConfiguration(project: Project, configurationName: String) {
        installConfiguration(project, configurationName, configurationDir(project, configurationName))
    }

    fun installConfiguration(project: Project, configurationName: String, targetDir: File) {
        if (targetDir.exists()) {
            targetDir.delete()
        }
        val res = project.findConfiguration(configurationName)?.resolve()//.collect { project.zipTree(it) }
        println("installing dependencies for $configurationName: ${res}")
        res?.forEach {
            val fileTree = project.zipTree(it)
            fileTree.visit(object : FileVisitor {
                override fun visitDir(dirDetails: FileVisitDetails) {

                }

                override fun visitFile(fileVisitDetails: FileVisitDetails) {
                    val dstFile = File("${targetDir.getAbsolutePath()}/${fileVisitDetails.relativePath}")
                    if (!dstFile.parentFile.exists()) {
                        dstFile.parentFile.mkdirs()
                    }
                    if (!dstFile.exists()) {
                        Files.copy(fileVisitDetails.file.toPath(), dstFile.toPath())
                    }
                }

            })
        }
    }
}