package at.karriere.gradle.plugins.task

import at.karriere.gradle.plugins.domain.Version
import at.karriere.gradle.plugins.provider.GitProvider
import at.karriere.gradle.plugins.provider.VersionProvider
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.*

class SetVersionTaskTest {
    public static final String MASTER = 'master'
    public static final String BUGFIX = 'bugfix/branch'
    public static final String FEATURE = 'feature/branch'
    public static final String VERSION_PROPERTIES = 'version.properties'
    VersionProvider versionProvider
    GitProvider gitProvider
    SetVersionTask setVersionTask
    Project project

    @Before
    void setup() {
        versionProvider = mock(VersionProvider)
        when(versionProvider.getVersion(anyString())).thenReturn(new Version())
        doNothing().when(versionProvider).writeVersionFile(anyString(), any(Version))

        gitProvider = mock(GitProvider)
        doNothing().when(gitProvider).commitVersionFile(any(Version))
        when(gitProvider.getGitBranch()).thenReturn(MASTER)

        project = ProjectBuilder.builder().build()

        setVersionTask = project.tasks.create('testTask', SetVersionTask) {}
        setVersionTask.versionProvider = versionProvider
        setVersionTask.gitProvider = gitProvider
    }

    @Test
    void testConfigure() {
        setVersionTask.configure {}

        assertThat(project.version).isEqualTo("1.0.0")
        verify(versionProvider, never()).writeVersionFile(anyString(), any(Version))
    }

    @Test
    void testConfigureBugfixBranch() {
        Version expectedVersion = new Version()
        expectedVersion.major = 1
        expectedVersion.minor = 0
        expectedVersion.patch = 0
        expectedVersion.snapshotVersion = "branch-SNAPSHOT"
        expectedVersion.upgrade = "patch"

        when(gitProvider.getGitBranch()).thenReturn(BUGFIX)
        setVersionTask.gitProvider = gitProvider

        setVersionTask.configure {}

        assertThat(project.version).isEqualTo("branch-SNAPSHOT")
        verify(versionProvider).writeVersionFile(VERSION_PROPERTIES, expectedVersion)
    }

    @Test
    void testConfigureFeatureBranch() {
        Version expectedVersion = new Version()
        expectedVersion.major = 1
        expectedVersion.minor = 0
        expectedVersion.patch = 0
        expectedVersion.snapshotVersion = "branch-SNAPSHOT"
        expectedVersion.upgrade = "minor"

        when(gitProvider.getGitBranch()).thenReturn(FEATURE)

        setVersionTask.configure {}

        assertThat(project.version).isEqualTo("branch-SNAPSHOT")
        verify(versionProvider).writeVersionFile(VERSION_PROPERTIES, expectedVersion)
    }
}
