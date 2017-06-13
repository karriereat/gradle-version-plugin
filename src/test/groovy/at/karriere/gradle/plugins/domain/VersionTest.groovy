package at.karriere.gradle.plugins.domain

import org.junit.Test

import static org.assertj.core.api.Java6Assertions.assertThat

class VersionTest {
    @Test
    void testDefaultVersionToString() {
        Version version = new Version()
        assertThat(version.getVersionString()).isEqualTo('1.0.0')
    }

    @Test
    void testCustomVersionToString() {
        Version version = new Version()
        version.major = 2
        version.minor = 2
        version.patch = 2
        assertThat(version.getVersionString()).isEqualTo('2.2.2')
    }
}
