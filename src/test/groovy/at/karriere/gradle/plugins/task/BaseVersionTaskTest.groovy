package at.karriere.gradle.plugins.task

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Java6Assertions.assertThat

class BaseVersionTaskTest {
    BaseVersionTask task

    @Before
    void setup() {
        task = ProjectBuilder.builder().build().tasks.create('testTask', BaseVersionTask) {}
    }

    @Test
    void testGroup() {
        assertThat(task.group).isEqualTo('versioning')
    }


}
