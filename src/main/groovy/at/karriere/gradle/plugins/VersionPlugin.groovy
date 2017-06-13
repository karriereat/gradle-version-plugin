package at.karriere.gradle.plugins

import at.karriere.gradle.plugins.task.IncreaseVersionTask
import at.karriere.gradle.plugins.task.SetVersionTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.task('setVersion', type: SetVersionTask) {
        }

        project.task('increaseVersion', type: IncreaseVersionTask) {
        }

        project.afterEvaluate {
            if (project.tasks.hasProperty('classes')) {
                project.tasks['classes'].dependsOn project.setVersion
            }
        }
    }


}
