package at.karriere.gradle.plugins.task

import at.karriere.gradle.plugins.domain.Version
import at.karriere.gradle.plugins.provider.GitProvider
import at.karriere.gradle.plugins.provider.VersionProvider
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.*

class IncreaseVersionTaskTest {

    IncreaseVersionTask increaseVersionTask
    VersionProvider versionProvider
    GitProvider gitProvider

    @Before
    void setUp() {
        versionProvider = mock(VersionProvider)
        when(versionProvider.getVersion(anyString())).thenReturn(new Version())
        doNothing().when(versionProvider).writeVersionFile(anyString(), any(Version))

        gitProvider = mock(GitProvider)
        doNothing().when(gitProvider).commitVersionFile(any(Version))
        when(gitProvider.getLastCommitMessage()).thenReturn("human commit message")

        increaseVersionTask = ProjectBuilder.builder().build().tasks.create('testTask', IncreaseVersionTask) {}
        increaseVersionTask.versionProvider = versionProvider
        increaseVersionTask.gitProvider = gitProvider
    }

    @Test
    void testIncreaseVersion() {
        Version mockVersion = getVersion(1, 2, 23, IncreaseVersionTask.MINOR, "1.2.3-SNAPSHOT")
        when(versionProvider.getVersion(anyString())).thenReturn(mockVersion)
        Version version = increaseVersionTask.increaseVersion()

        Version expectedVersion = getVersion(1, 3, 0, IncreaseVersionTask.MINOR, null)
        assertThat(version).isEqualTo(expectedVersion)
    }

    @Test
    void testIncreaseVersionPatch() {
        Version mockVersion = getVersion(2, 4, 3, IncreaseVersionTask.PATCH, "1.2.3-SNAPSHOT")
        when(versionProvider.getVersion(anyString())).thenReturn(mockVersion)
        Version version = increaseVersionTask.increaseVersion()

        Version expectedVersion = getVersion(2, 4, 4, IncreaseVersionTask.MINOR, null)
        assertThat(version).isEqualTo(expectedVersion)
    }

    @Test
    void testIncreaseVersionMajor() {
        Version mockVersion = getVersion(3, 2, 1, IncreaseVersionTask.MAJOR, "1.2.3-SNAPSHOT")
        when(versionProvider.getVersion(anyString())).thenReturn(mockVersion)
        Version version = increaseVersionTask.increaseVersion()

        Version expectedVersion = getVersion(4, 0, 0, IncreaseVersionTask.MINOR, null)
        assertThat(version).isEqualTo(expectedVersion)
    }

    @Test
    void testAbortIncreaseVersion() {
        when(gitProvider.getLastCommitMessage()).thenReturn(GitProvider.COMMIT_MESSAGE)

        Version mockVersion = getVersion(1, 2, 23, IncreaseVersionTask.MINOR, "1.2.3-SNAPSHOT")
        when(versionProvider.getVersion(anyString())).thenReturn(mockVersion)
        Version version = increaseVersionTask.increaseVersion()
        assertThat(version).isNull()
    }

    private Version getVersion(int major, int minor, int patch, String upgrade, String snapshotVersion) {
        Version mockVersion = new Version()
        mockVersion.major = major
        mockVersion.minor = minor
        mockVersion.patch = patch
        mockVersion.upgrade = upgrade
        mockVersion.snapshotVersion = snapshotVersion
        return mockVersion
    }
}
