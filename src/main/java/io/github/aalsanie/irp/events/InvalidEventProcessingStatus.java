package io.github.aalsanie.irp.events;

public class InvalidEventProcessingStatus extends RuntimeException {
    public InvalidEventProcessingStatus(String processingStatus) {
        super("Invalid processing status: " + processingStatus);
    }
}
