package at.karriere.gradle.plugins.exception

class CannotReadFileException extends RuntimeException {
    CannotReadFileException(String message) {
        super(message)
    }
}
