package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.lang.RuntimeException
import javax.inject.Inject

open class ValidateMpsWizardConfiguration @Inject constructor(@field:Input val mpsWizard: MpsWizardPlugin,
                                                              @field:Input val extension: MpsWizardExtension) : DefaultTask() {

    init {
        this.group = MPSWIZARD_TASKS_GROUP
        this.description = "Validate the MPS Wizard configuration"
    }

    @TaskAction
    fun execute() {
        val errors = extension.validate()
        errors.forEach {
            System.err.println("MPS Wizard configuration error: $it")
        }
        if (errors.isNotEmpty()) {
            throw RuntimeException("Invalid MPS Wizard configuration")
        }
    }

    companion object {
        const val TASK_NAME = "validateMpsWizardConfiguration"
    }
}