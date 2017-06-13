package at.karriere.gradle.plugins.domain

class Version {
    int major = 1
    int minor = 0
    int patch = 0
    String snapshotVersion = null
    /**
     * The part of the version that should be upgraded. Possible values are major, minor and patch
     */
    String upgrade = "minor"

    @Override
    String toString() {
        return "Version: ${major}.${minor}.${patch}; Snapshot-Version: ${snapshotVersion}; Upgrade: ${upgrade}"
    }

    String getVersionString() {
        return "${major}.${minor}.${patch}"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Version version = (Version) o

        if (major != version.major) return false
        if (minor != version.minor) return false
        if (patch != version.patch) return false
        if (snapshotVersion != version.snapshotVersion) return false
        if (upgrade != version.upgrade) return false

        return true
    }

    int hashCode() {
        int result
        result = major
        result = 31 * result + minor
        result = 31 * result + patch
        result = 31 * result + (snapshotVersion != null ? snapshotVersion.hashCode() : 0)
        result = 31 * result + (upgrade != null ? upgrade.hashCode() : 0)
        return result
    }
}
