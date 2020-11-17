package com.strumenta.mpswizard

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import java.io.File
import java.net.URI
import java.nio.file.Files

private fun Project.findConfiguration(name: String) = this.configurations.find { it.name == name }
private fun Project.findMpsConfiguration() = this.findConfiguration("mps")
private fun Project.findMpsArtifactsConfiguration() = this.findConfiguration("mpsArtifacts")

// Configuration:
//
// mpssetup { autosetconfigurations: true, mpsVersion: 2020.1.4 }

class MpsWizardPlugin : Plugin<Project> {
    var autosettersRun = HashSet<String>()

    override fun apply(project: Project) {
        // TODO check in configuration if this is necessary
        //autoSetConfigurations(project)
        //autoSetDependencies(project)

        if (project.tasks.findByName("resolveMps") != null) {
            println("not adding resolveMps task, as it exists already")
        } else {
            project.tasks.register("resolveMps", ResolveMps::class.java, this)
        }

        if (project.tasks.findByName("setupMpsProject") != null) {
            println("not adding setupMpsProject task, as it exists already")
        } else {
            project.tasks.register("setupMpsProject", SetupMpsProject::class.java, this)
        }

//        project.task("resolveMps") {
//            it.
////            it.
//        }

//        project.task('resolveMps', type= ResolveMps)
//        project.task('resolveMpsArtifacts', type: ResolveMpsArtifacts)
//        project.task('setupMpsProject') {
//            //dependsOn project.resolveMps, project.resolveMpsArtifacts
//        }
    }

    private fun getMpsVersion(project: Project) : String {
        // TODO support configuration in mpssetup
        // TODO verify if mps dependencies is set and it contains an explicit version
        val defaultMpsVersion = "2020.1.3"
        println("using default MPS version: $defaultMpsVersion")
        return defaultMpsVersion
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

    fun autoSetDependencies(project: Project) {
        if (checkOnlyOneRun("dependencies")) return
        // If mps dependencies are already set, do nothing, otherwise set the desired MPS version
        val mpsConf = project.findMpsConfiguration() ?: throw GradleException("no mps configuration")
        if (mpsConf.allDependencies.isEmpty()) {
            println("no mps configuration dependency, adding default one")
            project.dependencies.add("mps", "com.jetbrains:mps:${getMpsVersion(project)}")
        } else {
            println("mps configuration dependency present, not adding")
        }
    }

    fun autoSetConfigurations(project: Project) {
        if (checkOnlyOneRun("configurations")) return
        val mpsConf = project.findMpsConfiguration()
        if (mpsConf == null) {
            println("adding mps to configurations")
            project.configurations.create("mps")
        } else {
            println("mps configuration already present, not adding")
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