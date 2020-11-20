package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class ResolveMpsArtifacts @Inject constructor(val mpsWizard: MpsWizardPlugin, val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.dependsOn.add(ValidateMpsWizardConfiguration.TASK_NAME)
        this.group = MPSWIZARD_TASKS_GROUP
        this.description = "Download the dependencies used by the MPS Project"
    }

    @TaskAction
    fun execute() {
        mpsWizard.autoSetRepositories(project)
        mpsWizard.autoSetConfigurations(project)
        mpsWizard.autoSetDependencies(project, extension)
        mpsWizard.installConfiguration(project, "mpsArtifacts", mpsWizard.artifactsDir(project))
    }
}