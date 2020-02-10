package seedu.duke.storage;

import seedu.duke.exception.InvalidDateException;
import seedu.duke.exception.InvalidTaskInputException;
import seedu.duke.task.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a hard disk that allows user to add, delete, or modify the content of the file.
 */
public class Storage {
    String path;
    List<Task> tasks;

    /**
     * Creates a hard disk to store and load the user's task list.
     *
     * @param path Relative path to the file that is being opened.
     */
    public Storage(String path) {
        this.path = path;
    }

    /**
     * Loads the content of the hard disk into a list of tasks that user can see.
     *
     * @return A task list with content from the hard disk.
     * @throws IOException If an input or output exception occurred.
     * @throws InvalidTaskInputException If an invalid task command is input.
     * @throws InvalidDateException If a date is input in a wrong format.
     */
    public List<Task> load() throws IOException, InvalidTaskInputException, InvalidDateException {
        tasks = new ArrayList<>();
        String inputLine;
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        while ((inputLine = br.readLine()) != null) {
            String[] inputs = inputLine.split("\\|", 3);
            String type = inputs[0].trim();
            String doneStatus = inputs[1].trim();
            String desc = inputs[2].trim();
            if (type.equalsIgnoreCase("T")) {
                addTodo(desc, doneStatus);
            } else if (type.equalsIgnoreCase("D")) {
                addDeadline(desc, doneStatus);
            } else if (type.equalsIgnoreCase("E")) {
                addEvent(desc, doneStatus);
            } else {
                throw new InvalidTaskInputException();
            }
        }

        Collections.sort(tasks);
        deleteAllInStorage();
        addAllToStorage(tasks);
        return tasks;
    }

    public void sortStorage() throws InvalidTaskInputException, InvalidDateException, IOException {
        List<Task> tasksInStorage = new ArrayList<>();
        tasksInStorage = load();
        Collections.sort(tasksInStorage);
        deleteAllInStorage();
        addAllToStorage(tasksInStorage);
    }

    public void deleteAllInStorage() throws IOException {
        String data = "";
        FileOutputStream fileOutputStr = new FileOutputStream(path);
        fileOutputStr.write(data.getBytes());
        fileOutputStr.close();
    }

    public void addAllToStorage(List<Task> sortedTasks) throws IOException {
        for (Task task : sortedTasks) {
            addToStorage(task);
        }
    }

    /**
     * Loads a todo task into the task list.
     *
     * @param desc The details of the todo task.
     * @param doneStatus An indicator which shows whether a todo task has been completed or not.
     */
    private void addTodo(String desc, String doneStatus) {
        Task todo = new Todo(desc);
        if (doneStatus.equalsIgnoreCase("Y")) {
            todo.markAsDone();
            assert todo.isDone();
        }
        tasks.add(todo);
    }

    /**
     * Loads a deadline task into the task list.
     *
     * @param desc The details of the deadline task.
     * @param doneStatus An indicator which shows whether a deadline task has been completed or not.
     * @throws InvalidTaskInputException If an invalid task command is input.
     * @throws InvalidDateException If a date is input in a wrong format.
     */
    private void addDeadline(String desc, String doneStatus)
            throws InvalidTaskInputException, InvalidDateException {
        String[] descs = desc.split(" /by |\\|");
        if (descs.length == 1) { // invalid Deadline input format
            throw new InvalidTaskInputException();
        }

        String deadlineDesc = descs[0].trim();
        String deadlineDate = descs[1].trim();
        LocalDate formattedDeadlineDate = null;

        if (isValidDate(deadlineDate)) {
            formattedDeadlineDate = LocalDate.parse(deadlineDate);
        } else {
            throw new InvalidDateException();
        }

        Task deadline = new Deadline(deadlineDesc, formattedDeadlineDate);

        if (doneStatus.equalsIgnoreCase("Y")) {
            deadline.markAsDone();
        }
        tasks.add(deadline);
    }

    /**
     * Loads an event task into the task list.
     *
     * @param desc The details of the event task.
     * @param doneStatus An indicator which shows whether an event task has been completed or not.
     * @throws InvalidTaskInputException If an invalid task command is input.
     * @throws InvalidDateException If a date is input in a wrong format.
     */
    private void addEvent(String desc, String doneStatus)
            throws InvalidTaskInputException, InvalidDateException {
        String[] descs = desc.split(" /at |\\|");
        if (descs.length == 1) { // invalid Event input format
            throw new InvalidTaskInputException();
        }
        String eventDesc = descs[0].trim();
        String eventDate = descs[1].trim();
        LocalDate formattedEventDate = null;
        if (isValidDate(eventDate)) {
            formattedEventDate = LocalDate.parse(eventDate);
        } else {
            throw new InvalidDateException();
        }
        Task event = new Event(eventDesc, formattedEventDate);

        if (doneStatus.equalsIgnoreCase("Y")) {
            event.markAsDone();
        }

        tasks.add(event);
    }

    /**
     * Checks if an input date is written in a valid date format.
     *
     * @param inDate The input date.
     * @return true if the input date is written in a valid date format.
     */
    private static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    /**
     * Saves the task that the user inputs into the hard disk.
     *
     * @param task The task that user inputs which needs to be saved into the hard disk.
     * @throws IOException If an input or output exception occurred.
     */
    public void addToStorage(Task task) throws IOException {
        File file = new File(path);
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        String data = "";

        if (task instanceof Deadline) {
            data += "D | " + task.getStatusIcon() + " | " + task.getDescription()
                + " | " + ((Deadline) task).getDate();
        } else if (task instanceof Event) {
            data += "E | " + task.getStatusIcon() + " | " + task.getDescription()
                + " | " + ((Event) task).getDate();
        } else if (task instanceof Todo) {
            data += "T | " + task.getStatusIcon() + " | " + task.getDescription();
        }

        if (file.length() == 0) {
            bw.write(data);
        } else {
            bw.write("\n" + data);
        }

        bw.close();
        fw.close();
    }

    /**
     * Modifies the done status of a task inside the hard disk according to the index number given by the user.
     *
     * @param index The index number ot the task that is being modified.
     * @throws IOException If an input or output exception occurred.
     */
    public void changeToStorage(int index) throws IOException {
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String data = "";
        String line = null;
        int counter = 1;
        while ((line = br.readLine()) != null) {
            if (counter == index) {
                line = line.substring(0, 4) + "Y" + line.substring(5, line.length());
            }

            if (counter == 1) {
                data += line;
            } else {
                assert counter > 1 : counter;
                data += "\n" + line;
            }
            counter++;
        }

        FileOutputStream fileOutputStr = new FileOutputStream(path);
        fileOutputStr.write(data.getBytes());
        fileOutputStr.close();
    }

    /**
     * Deletes the task in the hard disk according to the index number given by the user.
     *
     * @param index The index number ot the task that is being modified.
     * @throws IOException If an input or output exception occurred.
     */
    public void deleteInStorage(int index) throws IOException {
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String data = "";
        String line = null;
        int counter = 1;
        boolean isFirstUndeletedLine = true;
        while ((line = br.readLine()) != null) {
            if (counter != index) {
                if (isFirstUndeletedLine) {
                    data += line;
                    isFirstUndeletedLine = false;
                } else {
                    assert !isFirstUndeletedLine;
                    data += "\n" + line;
                }
            }
            counter++;
        }

        FileOutputStream fileOutputStr = new FileOutputStream(path);
        fileOutputStr.write(data.getBytes());
        fileOutputStr.close();
    }
}