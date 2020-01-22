import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Duke {
    private static List<Task> tasks = new ArrayList<>();
    public static void main(String[] args) {
        greet();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String input = sc.nextLine();
            String[] inputs = input.split(" ", 2);
            String command = inputs[0];
            if (command.equals("bye")) {
                sayBye();
                break;
            } else if (command.equals("todo")) {
                String desc = inputs[1];
                addTodo(desc);
            } else if (command.equals("deadline")) {
                String desc = inputs[1];
                addDeadline(desc);
            } else if (command.equals("event")) {
                String desc = inputs[1];
                addEvent(desc);
            } else if (command.equals("list")) {
                printList();
            } else if (command.equals("done")) {
                int index = Integer.parseInt(inputs[1]);
                markTaskAsDone(index);
            } else {
                sayInvalidInput();
            }
        }
    }

    private static void greet() {
        System.out.println("Hi there, I'm Dodo!\nHow may I help you today?");
    }

    private static void sayBye() {
        System.out.println("Stop procrastinating. See you!");
    }

    /*private static void addToList(Task task) {
        tasks.add(task);
        System.out.println("added: " + task.getDescription());
    }*/
    private static void printAddToList() {
        System.out.println("Gotcha. Added this to your list:");
    }

    private static void printNumTask() {
        String taskWord;
        if (tasks.size() == 1) {
            taskWord = "task";
        } else {
            taskWord = "tasks";
        }
        System.out.printf("Now you got %d %s in your list!\n", tasks.size(), taskWord);
    }

    private static void addEvent(String desc) {
        String[] descs = desc.split(" /at ");
        String eventDesc = descs[0];
        String eventTime = descs[1];
        Task event = new Event(eventDesc, eventTime);
        tasks.add(event);
        printAddToList();
        System.out.println(event.toString());
        printNumTask();
    }

    private static void addDeadline(String desc) {
        String[] descs = desc.split(" /by ");
        String deadlineDesc = descs[0];
        String deadlineTime = descs[1];
        Task deadline = new Deadline(deadlineDesc, deadlineTime);
        tasks.add(deadline);
        printAddToList();
        System.out.println(deadline.toString());
        printNumTask();
    }

    private static void addTodo(String desc) {
        Task todo = new Todo(desc);
        tasks.add(todo);
        printAddToList();
        System.out.println(todo.toString());
        printNumTask();
    }

    private static void printList() {
        System.out.println("Stop procrastinating. Do it now!");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, tasks.get(i).toString());
        }
    }

    private static void markTaskAsDone(int index) {
        Task task = tasks.get(index - 1);
        task.markAsDone();
        System.out.println("Good job! One off your chest!");
        System.out.println(task.toString());
    }

    private static void sayInvalidInput() {
        System.out.println("Apologies, I'm too dumb to understand that!");
    }
}
