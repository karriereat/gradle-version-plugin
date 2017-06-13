package at.karriere.gradle.plugins.provider

import at.karriere.gradle.plugins.domain.Version
import at.karriere.gradle.plugins.exception.CannotReadFileException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Java6Assertions.assertThat
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

class VersionProviderTest {
    public static final String FILE_PATH = 'version.properties'
    VersionProvider versionProvider
    Project project
    private File file

    @Before
    void setup() {

        def writerMock = new StringWriter()
        File.metaClass.withWriter = { Closure c ->
            c.call(writerMock)
        }
        file = mock(File)
        when(file.exists()).thenReturn(true)
        when(file.canRead()).thenReturn(true)
        when(file.getPath()).thenReturn('test')

        project = mock(Project)
        when(project.file(any())).thenReturn(this.file)
        when(project.logger).thenReturn(mock(Logger))
        versionProvider = new VersionProvider(project)
    }

    @Test
    void testBuildVersion() {
        Properties properties = new Properties()
        properties.put('version', '1.0.2')
        properties.put('version.upgrade', 'minor')
        properties.put('version.snapshot', 'test-SNAPSHOT')
        Version version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(2)
        assertThat(version.upgrade).isEqualTo('minor')
        assertThat(version.snapshotVersion).isEqualTo('test-SNAPSHOT')
    }

    @Test
    void testBuildVersionNoSnapshot() {
        Properties properties = new Properties()
        properties.put('version', '1.0.2')
        properties.put('version.upgrade', 'patch')
        Version version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(2)
        assertThat(version.upgrade).isEqualTo('patch')
        assertThat(version.snapshotVersion).isNull()
    }

    @Test
    void testBuildVersionInvalidVersion() {
        Properties properties = new Properties()
        properties.put('version', 'invalid')
        properties.put('version.upgrade', 'major')
        Version version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
        assertThat(version.upgrade).isEqualTo('major')
        assertThat(version.snapshotVersion).isNull()

        properties = new Properties()
        properties.put('version', 'invalid.version.here')
        properties.put('version.upgrade', 'major')
        version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)

        properties = new Properties()
        properties.put('version', '1.0.1-RC1')
        properties.put('version.upgrade', 'major')
        version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    void testBuildVersionNoUpgrade() {
        Properties properties = new Properties()
        properties.put('version', '1.0.2')
        Version version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(2)
        assertThat(version.upgrade).isEqualTo('minor')
        assertThat(version.snapshotVersion).isNull()
    }

    @Test
    void testBuildVersionInvalidUpgrade() {
        Properties properties = new Properties()
        properties.put('version', '1.0.2')
        properties.put('version.upgrade', 'error')
        Version version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(2)
        assertThat(version.upgrade).isEqualTo('minor')
        assertThat(version.snapshotVersion).isNull()
    }

    @Test
    void testGetVersionNoVersionExists() {
        Properties properties = new Properties()
        Version version = versionProvider.buildVersion(properties)
        assertThat(version.major).isEqualTo(1)
        assertThat(version.minor).isEqualTo(0)
        assertThat(version.patch).isEqualTo(0)
    }

    @Test
    void testGetVersionFile() {
        File f = versionProvider.getVersionFile(FILE_PATH)
        assertThat(f).isEqualTo(file)
    }

    @Test
    void testGetNotExistingVersionFile() {
        boolean fileExists = false
        when(file.exists()).then() { invocation ->
            boolean result = fileExists
            fileExists = true
            return result
        }
        versionProvider.getVersionFile(FILE_PATH)
        verify(file).createNewFile()
    }

    @Test(expected = CannotReadFileException)
    void testGetExistingNotReadableVersionFile() {
        when(file.canRead()).thenReturn(false)
        versionProvider.getVersionFile(FILE_PATH)
    }
}
