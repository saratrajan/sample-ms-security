package io.github.saratrajan.samplems.error;

public class DownstreamServiceException extends RuntimeException {

    private final String service;
    private final int status;
    private final boolean retryable;

    public DownstreamServiceException(String service, int status, boolean retryable) {
        super("Downstream " + service + " returned " + status);
        this.service = service;
        this.status = status;
        this.retryable = retryable;
    }

    public String getService() {
        return service;
    }

    public int getStatus() {
        return status;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
