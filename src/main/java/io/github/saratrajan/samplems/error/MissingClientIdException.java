package io.github.saratrajan.samplems.error;

public class MissingClientIdException extends RuntimeException {
    public MissingClientIdException(String message) {
        super(message);
    }
}
