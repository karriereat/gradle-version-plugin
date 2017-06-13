package at.karriere.gradle.plugins.task

import at.karriere.gradle.plugins.domain.Version
import at.karriere.gradle.plugins.provider.GitProvider
import at.karriere.gradle.plugins.provider.VersionProvider
import org.gradle.api.Task

class SetVersionTask extends BaseVersionTask {

    public static final String BUGFIX = "bugfix"
    public static final String MASTER = "master"
    public static final String SNAPSHOT_POSTFIX = "-SNAPSHOT"

    GitProvider gitProvider
    VersionProvider versionProvider

    SetVersionTask() {
        versionProvider = new VersionProvider(project)
        gitProvider = new GitProvider()
        outputs.upToDateWhen {
            return false
        }
    }

    @Override
    Task configure(Closure closure) {
        Version version = versionProvider.getVersion(versionFilePath)
        checkVersion(version)

        if (version.snapshotVersion != null) {
            project.version = version.snapshotVersion
        } else {
            project.version = version.getVersionString()
        }

        return super.configure(closure)
    }

    private void checkVersion(Version version) {
        if (version.snapshotVersion == null) {
            String branch = gitProvider.getGitBranch()
            if (branch.equals(MASTER)) {
                return
            }

            if (branch.startsWith(BUGFIX)) {
                version.upgrade = PATCH
            }

            String baseName = branch.substring(branch.indexOf("/") + 1)
            version.snapshotVersion = baseName + SNAPSHOT_POSTFIX

            versionProvider.writeVersionFile(versionFilePath, version)
        }
    }
}
