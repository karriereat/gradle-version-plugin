package at.karriere.gradle.plugins

import at.karriere.gradle.plugins.task.IncreaseVersionTask
import at.karriere.gradle.plugins.task.SetVersionTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Java6Assertions.assertThat

class VersionPluginTest {
    private Project project
    private VersionPlugin versionPlugin = new VersionPlugin()

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    void testVersionPluginAddsTasksToProject() {
        versionPlugin.apply(project)

        assertThat(project.tasks.findByPath('setVersion')).isNotNull()
        assertThat(project.tasks.findByPath('increaseVersion')).isNotNull()

        assertThat(project.tasks.setVersion).isInstanceOf(SetVersionTask)
        assertThat(project.tasks.increaseVersion).isInstanceOf(IncreaseVersionTask)

        assertThat(project.tasks.setVersion.group).isEqualTo('versioning')
        assertThat(project.tasks.increaseVersion.group).isEqualTo('versioning')
    }
}
