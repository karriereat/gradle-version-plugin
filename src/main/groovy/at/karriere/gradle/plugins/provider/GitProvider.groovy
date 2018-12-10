package at.karriere.gradle.plugins.provider

import at.karriere.gradle.plugins.domain.Version

class GitProvider {
    static final COMMIT_MESSAGE = "automatically increased version to";

    String getGitBranch() {
        def branch = ""
        def proc = "git rev-parse --abbrev-ref HEAD".execute()
        proc.in.eachLine { line -> branch = line }
        proc.err.eachLine { line -> println line }
        proc.waitFor()
        return branch
    }

    String getLastCommitMessage() {
        def commitMessage = ""
        def proc = "git log -1 --pretty=%B origin/master".execute()
        proc.in.eachLine { line -> commitMessage += line + " " }
        proc.waitFor()

        return commitMessage.trim();
    }

    void commitVersionFile(Version version, String customMessage) {
        def proc = "git add version.properties".execute()
        proc.waitFor()

        proc = ["git", "commit", "-m " + COMMIT_MESSAGE + " ${version.getVersionString()} ${customMessage}"].execute()
        proc.waitFor()

        proc = "git push origin master".execute()
        proc.waitFor()
    }
}
