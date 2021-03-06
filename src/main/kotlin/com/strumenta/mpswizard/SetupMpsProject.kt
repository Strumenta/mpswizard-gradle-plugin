package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class SetupMpsProject @Inject constructor(@Internal val mpsWizard: MpsWizardPlugin,
                                               @Internal val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.dependsOn.add(ValidateMpsWizardConfiguration.TASK_NAME)
        this.dependsOn.add("resolveMps")
        this.dependsOn.add("resolveMpsArtifacts")
        this.dependsOn.add(GenerateMpsProject.TASK_NAME)
        this.dependsOn.add(GenerateGitignore.TASK_NAME)
        this.group = MPSWIZARD_TASKS_GROUP
        this.description = "Prepare the whole project"
    }

    @TaskAction
    fun execute() {
    }
}