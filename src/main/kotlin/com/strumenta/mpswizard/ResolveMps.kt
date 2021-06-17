package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class ResolveMps @Inject constructor(@field:Input val mpsWizard: MpsWizardPlugin, @field:Input val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.dependsOn.add(ValidateMpsWizardConfiguration.TASK_NAME)
        this.group = MPSWIZARD_TASKS_GROUP
        this.description = "Download a copy of MPS for running tests from the command line"
    }

    @TaskAction
    fun execute() {
        println("MpsWizard > ResolveMps")
        mpsWizard.autoSetRepositories(project)
        mpsWizard.autoSetConfigurations(project)
        mpsWizard.autoSetDependencies(project, extension)
        mpsWizard.installConfiguration(project, "mps")
    }
}