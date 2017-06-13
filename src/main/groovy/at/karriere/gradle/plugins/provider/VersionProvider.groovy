package at.karriere.gradle.plugins.provider

import at.karriere.gradle.plugins.domain.Version
import at.karriere.gradle.plugins.exception.CannotReadFileException
import org.gradle.api.Project

class VersionProvider {

    public static final String VERSION = 'version'
    public static final String VERSION_UPGRADE = 'version.upgrade'
    public static final String VERSION_SNAPSHOT = 'version.snapshot'
    private Project project

    VersionProvider(Project project) {
        this.project = project
    }

    Version getVersion(String versionFilePath) {
        File versionFile = getVersionFile(versionFilePath)
        Properties versionProperties = new Properties()
        versionProperties.load(new FileInputStream(versionFile))

        return buildVersion(versionProperties)
    }

    Version buildVersion(Properties versionProperties) {
        Version version = new Version()
        String versionTokens = versionProperties[VERSION]
        if (versionTokens != null) {
            List<String> versionParts = versionTokens.tokenize('.')
            for (int i = 0; i < versionParts.size(); i++) {
                parseVersionNumber(version, versionParts[i], i)
            }
        }
        version.upgrade = versionProperties[VERSION_UPGRADE]
        if (!isVersionUpgradeValid(version)) {
            version.upgrade = 'minor'
        }
        version.snapshotVersion = versionProperties[VERSION_SNAPSHOT]
        return version
    }

    private void parseVersionNumber(Version version, String versionPart, int index) {
        Map<Integer, String> fieldMap = new HashMap()
        fieldMap.put(0, 'major')
        fieldMap.put(1, 'minor')
        fieldMap.put(2, 'patch')
        def versionField = fieldMap.get(index)
        try {
            version."$versionField" = Integer.valueOf(versionPart)
        } catch (NumberFormatException e) {
            def v = new Version()
            def defaultValue = v."$versionField"
            project.logger.lifecycle("Error while parsing the $versionField version number. The default value [$defaultValue] was used.")
        }
    }

    private boolean isVersionUpgradeValid(Version version) {
        return version.upgrade != null && (version.upgrade.equals('major') || version.upgrade.equals('minor') || version.upgrade.equals('patch'))
    }

    File getVersionFile(String versionFilePath) {
        def versionFile = project.file(versionFilePath)
        if (!versionFile.exists()) {
            versionFile.createNewFile()
            writeVersionFile(versionFilePath, new Version())
        } else if (versionFile.exists() && !versionFile.canRead()) {
            throw new CannotReadFileException("Cannot read version properties f with path '${versionFilePath}'")
        }
        return versionFile
    }

    void writeVersionFile(String versionFilePath, Version version) {
        def versionFile = getVersionFile(versionFilePath)
        versionFile.withWriter { writer ->
            writer.println VERSION + '=' + version.getVersionString()
            writer.println VERSION_UPGRADE + '=' + version.upgrade
            if (version.snapshotVersion != null) {
                writer.println VERSION_SNAPSHOT + '=' + version.snapshotVersion
            }
        }
    }
}
