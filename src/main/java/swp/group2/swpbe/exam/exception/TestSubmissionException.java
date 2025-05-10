package swp.group2.swpbe.exam.exception;

public class TestSubmissionException extends RuntimeException {
    public TestSubmissionException(String message) {
        super(message);
    }

    public TestSubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}