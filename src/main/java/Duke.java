import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Duke {
    private static List<Task> tasks;

    public Duke() {
        greet();
        tasks = new ArrayList<>();
    }

    public void runDuke() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            try {
                String input = sc.nextLine();
                String[] inputs = input.split(" ", 2);
                String command = inputs[0];
                if (command.equalsIgnoreCase("bye")) {
                    sayBye();
                    break;
                } else if (command.equalsIgnoreCase("todo")) {
                    if (inputs.length == 1) {
                        throw new EmptyDescriptionException();
                    }
                    String desc = inputs[1];
                    addTodo(desc);
                } else if (command.equalsIgnoreCase("deadline")) {
                    if (inputs.length == 1) {
                        throw new EmptyDescriptionException();
                    }
                    String desc = inputs[1];
                    addDeadline(desc);
                } else if (command.equalsIgnoreCase("event")) {
                    if (inputs.length == 1) {
                        throw new EmptyDescriptionException();
                    }
                    String desc = inputs[1];
                    addEvent(desc);
                } else if (command.equalsIgnoreCase("list")) {
                    printList();
                } else if (command.equalsIgnoreCase("done")) {
                    if (!isNumeric(inputs[1])) {
                        throw new InvalidTaskInputException();
                    }
                    int index = Integer.parseInt(inputs[1]);
                    if (index < 1 || index > tasks.size()) {
                        throw new TaskIndexOutOfBoundsException();
                    }
                    markTaskAsDone(index);
                } else if (command.equalsIgnoreCase("delete")) {
                    int index = Integer.parseInt(inputs[1]);
                    if (index < 1 || index > tasks.size()) {
                        throw new TaskIndexOutOfBoundsException();
                    }
                    deleteTask(index);
                } else {
                    throw new InvalidCommandException();
                }
            } catch (DukeException e) {
                System.out.println(e.toString());
            }
        }
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int intNum = Integer.parseInt(strNum);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static void greet() {
        System.out.println("Hi there, I'm Dodo!\nHow may I help you today?");
    }

    private static void sayBye() {
        System.out.println("Stop procrastinating. See you!");
    }

    private static void printAddToList() {
        System.out.println("Gotcha. Added this to your list:");
    }

    private static void printNumTask() {
        String taskWord;
        if (tasks.size() <= 1) {
            taskWord = "task";
        } else {
            taskWord = "tasks";
        }
        System.out.printf("Now you got %d %s in your list!\n", tasks.size(), taskWord);
    }

    private static void addTodo(String desc) {
        Task todo = new Todo(desc);
        tasks.add(todo);
        printAddToList();
        System.out.println(todo.toString());
        printNumTask();
    }

    private static void addDeadline(String desc) throws InvalidTaskInputException, InvalidDateException {
        String[] descs = desc.split(" /by ");
        if (descs.length == 1) { // invalid Deadline input format
            throw new InvalidTaskInputException();
        }
        String deadlineDesc = descs[0];
        String deadlineTime = descs[1];
        LocalDate formattedDeadlineTime = null;
        if (isValidDate(deadlineTime)) {
            formattedDeadlineTime = LocalDate.parse(deadlineTime);
        } else {
            throw new InvalidDateException();
        }

        Task deadline = new Deadline(deadlineDesc, formattedDeadlineTime);
        tasks.add(deadline);
        printAddToList();
        System.out.println(deadline.toString());
        printNumTask();
    }

    private static void addEvent(String desc) throws InvalidTaskInputException, InvalidDateException {
        String[] descs = desc.split(" /at ");
        if (descs.length == 1) { // invalid Event input format
            throw new InvalidTaskInputException();
        }
        String eventDesc = descs[0];
        String eventTime = descs[1];
        LocalDate formattedEventTime = null;
        if (isValidDate(eventTime)) {
            formattedEventTime = LocalDate.parse(eventTime);
        } else {
            throw new InvalidDateException();
        }

        Task event = new Event(eventDesc, formattedEventTime);
        tasks.add(event);
        printAddToList();
        System.out.println(event.toString());
        printNumTask();
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    private static void printList() {
        if (tasks.size() == 0) {
            System.out.println("You currently don't have any task. Start listing now!");
        } else {
            System.out.println("Stop procrastinating. Do it now!");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, tasks.get(i).toString());
            }
        }
    }

    private static void markTaskAsDone(int index) {
        Task task = tasks.get(index - 1);
        task.markAsDone();
        System.out.println("Good job! One off your chest!");
        System.out.println(task.toString());
    }

    private static void deleteTask(int index) {
        Task task = tasks.get(index - 1);
        tasks.remove(index - 1);
        printRemoveTask();
        System.out.println(task.toString());
        printNumTask();
    }

    private static void printRemoveTask() {
        System.out.println("Okay, I have removed this task for you:");
    }

    public static void main(String[] args) {
        Duke duke = new Duke();
        duke.runDuke();
    }
}
