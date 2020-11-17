package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class SetupMpsProject @Inject constructor(val mpsWizard: MpsWizardPlugin) : DefaultTask() {

    init {
        this.dependsOn.add("resolveMps")
    }

    @TaskAction
    fun execute() {
    }
}