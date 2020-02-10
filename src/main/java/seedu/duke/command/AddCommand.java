package seedu.duke.command;

import seedu.duke.exception.*;
import seedu.duke.storage.Storage;
import seedu.duke.task.TaskList;
import seedu.duke.ui.Ui;
import seedu.duke.enums.TaskTypes;
import seedu.duke.task.Deadline;
import seedu.duke.task.Event;
import seedu.duke.task.Task;
import seedu.duke.task.Todo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * Represents a command object to add task to the list.
 */
public class AddCommand extends Command {
    private TaskTypes taskType;
    private String[] inputs;

    /**
     * Represents an AddCommand object.
     *
     * @param taskType The type of the task.
     * @param inputs The user input.
     */
    public AddCommand(TaskTypes taskType, String[] inputs) {
        this.taskType = taskType;
        this.inputs = inputs;
    }

    /**
     * Adds a task into the list.
     *
     * @param taskList The TaskList object.
     * @param ui The User Interface object.
     * @param storage The hard disk object.
     * @throws EmptyDescriptionException If the description of a task is empty.
     * @throws InvalidCommandException If the command inputted is not todo, deadline, event, list, delete, or done.
     * @throws InvalidTaskInputException If an invalid task command is input.
     * @throws InvalidDateException If a date is input in a wrong format.
     * @throws IOException If an input or output exception occurred.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws EmptyDescriptionException,
            InvalidCommandException, InvalidTaskInputException, InvalidDateException, IOException {
        if (inputs.length == 1) {
            throw new EmptyDescriptionException();
        }
        String desc = inputs[1];
        Task task = null;

        switch (taskType) {
        case TODO:
            task = new Todo(desc);
            break;
        case DEADLINE:
            String[] deadlineDescs = desc.split(" /by |\\|");
            if (deadlineDescs.length == 1) { // invalid Deadline input format
                throw new InvalidTaskInputException();
            }

            String deadlineDesc = deadlineDescs[0].trim();
            String deadlineDate = deadlineDescs[1].trim();
            LocalDate formattedDeadlineDate = null;
            if (isValidDate(deadlineDate)) {
                formattedDeadlineDate = LocalDate.parse(deadlineDate);
            } else {
                throw new InvalidDateException();
            }
            task = new Deadline(deadlineDesc, formattedDeadlineDate);
            break;
        case EVENT:
            String[] eventDescs = desc.split(" /at |\\|");
            if (eventDescs.length == 1) { // invalid Event input format
                throw new InvalidTaskInputException();
            }
            String eventDesc = eventDescs[0].trim();
            String eventDate = eventDescs[1].trim();
            LocalDate formattedEventDate = null;
            if (isValidDate(eventDate)) {
                formattedEventDate = LocalDate.parse(eventDate);
            } else {
                throw new InvalidDateException();
            }
            task = new Event(eventDesc, formattedEventDate);
            break;
        default:
            throw new InvalidCommandException();
        }

        taskList.addTask(task);
        storage.addToStorage(task);
        storage.sortStorage();
        ui.printAddToList();
        ui.print(task.toString());
        ui.printNumTask(taskList.getTasks());
    }

    @Override
    public boolean hasNextCommand() {
        return true;
    }

    /**
     * Checks if an input date is written in a valid date format.
     *
     * @param inDate The input date.
     * @return true if the input date is written in a valid date format.
     */
    private boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}