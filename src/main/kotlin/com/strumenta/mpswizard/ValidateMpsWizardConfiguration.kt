package com.strumenta.mpswizard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.lang.RuntimeException
import javax.inject.Inject

open class ValidateMpsWizardConfiguration @Inject constructor(val mpsWizard: MpsWizardPlugin,
                                                              val extension: MpsWizardExtension) : DefaultTask() {

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