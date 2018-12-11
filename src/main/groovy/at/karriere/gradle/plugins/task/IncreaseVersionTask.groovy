package at.karriere.gradle.plugins.task

import at.karriere.gradle.plugins.domain.Version
import at.karriere.gradle.plugins.provider.GitProvider
import at.karriere.gradle.plugins.provider.VersionProvider
import org.gradle.api.tasks.TaskAction

class IncreaseVersionTask extends BaseVersionTask {

    VersionProvider versionProvider
    GitProvider gitProvider
    boolean versionIncreased = true
    String customMessage = ''

    IncreaseVersionTask() {
        versionProvider = new VersionProvider(project)
        gitProvider = new GitProvider()
        outputs.upToDateWhen { return versionIncreased }
    }

    @TaskAction
    Version increaseVersion() {
        def commitMessage = gitProvider.getLastCommitMessage()
        if (commitMessage.startsWith(GitProvider.COMMIT_MESSAGE)) {
            def message = "The 'increase version' task wasn't executed since it was already up to date."
            logger.lifecycle(message)
            versionIncreased = false
            return null
        }

        Version version = versionProvider.getVersion(versionFilePath)

        increaseVersion(version)
        logger.lifecycle("Increased version to ${version.getVersionString()}")

        versionProvider.writeVersionFile(versionFilePath, version)

        gitProvider.commitVersionFile(version, customMessage)
        return version
    }

    void increaseVersion(Version version) {
        if (version.upgrade.equals(MINOR)) {
            version.minor++
            version.patch = 0
        } else {
            if (version.upgrade.equals(PATCH)) {
                version.patch++
            } else if (version.upgrade.equals(MAJOR)) {
                version.major++
                version.minor = 0
                version.patch = 0
            }
            version.upgrade = 'minor'
        }
        version.snapshotVersion = null
    }

    void setCustomMessage(String customMessage) {
        this.customMessage = customMessage
    }
}
