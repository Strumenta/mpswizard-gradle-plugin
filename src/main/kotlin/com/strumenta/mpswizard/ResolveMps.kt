package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class ResolveMps @Inject constructor(val mpsWizard: MpsWizardPlugin) : DefaultTask() {

    @TaskAction
    fun execute() {
        mpsWizard.autoSetRepositories(project)
        mpsWizard.autoSetConfigurations(project)
        mpsWizard.autoSetDependencies(project)
        mpsWizard.installConfiguration(project, "mps")
    }
}