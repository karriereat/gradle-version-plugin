package at.karriere.gradle.plugins.task

import org.gradle.api.DefaultTask

class BaseVersionTask extends DefaultTask {

    public static final String MINOR = 'minor'
    public static final String PATCH = 'patch'
    public static final String MAJOR = 'major'

    protected String versionFilePath = 'version.properties'

    BaseVersionTask() {
        group = "versioning"
    }

}
