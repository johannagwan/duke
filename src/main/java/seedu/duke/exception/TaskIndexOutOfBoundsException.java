package seedu.duke.exception;

public class TaskIndexOutOfBoundsException extends DukeException {
    @Override
    public String toString() {
        return "You keyed in an invalid index!";
    }
}
