package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class SetupMpsProject @Inject constructor(val mpsWizard: MpsWizardPlugin,
                                               val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.dependsOn.add(ValidateMpsWizardConfiguration.TASK_NAME)
        this.dependsOn.add("resolveMps")
        this.dependsOn.add("resolveMpsArtifacts")
    }

    @TaskAction
    fun execute() {
    }
}