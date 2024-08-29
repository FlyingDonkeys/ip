package src.main.java;

public class Parser {

    public Parser() {
    }

    protected static void parseInput(String input) throws DeadlineFormatException, EmptyTodoException, UnknownCommandException {
        if (input.equals("bye")) {
            System.out.println("Bye " + Ui.username + "\n");
            return;
        } else if (input.equals("list")) {
            String list = "Your current tasks are\n";
            for (int i = 1; i <= TaskList.getSize(); i++) {
                list += String.format("%d. %s\n", i, TaskList.getTasks().get(i - 1));
            }
            System.out.println(list);
        } else if (check_mark_command(input)) {
            String[] arr = input.split(" ", 2);
            Integer taskNum = Integer.valueOf(arr[1]);
            if (arr[0].equals("mark")) {
                // Mark task "taskNum" as done
                TaskList.markTaskAsDone(taskNum);
            } else if (arr[0].equals("unmark")) {
                // Mark task "taskNum" as not done
                TaskList.unmarkTaskAsDone(taskNum);
            }
        } else if (check_delete_command(input)) {
            String[] arr = input.split(" ", 2);
            Integer taskNum = Integer.valueOf(arr[1]);
            TaskList.removeTask(taskNum - 1);
        } else {
            // Want to parse and add task into task list
            parseTaskAddition(input);
        }

        // Want to update file of tasks after every new command (which could possibly change the tasks involved)
        updateFile();
    }

    public static boolean check_mark_command(String targetString) {
        String[] arr = targetString.split(" ");
        if ((arr[0].equals("mark") || arr[0].equals("unmark")) && arr.length == 2) {
            try {
                Integer taskNum = Integer.valueOf(arr[1]);
                if (taskNum <= tasklist.size() && taskNum >= 1) {
                    return true;
                } else {
                    System.out.println("You might have tried to mark or unmark a task that does not exist.\n" +
                            "If so, please delete this wrongly added task using the delete command.\n");
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean check_delete_command(String targetString) {
        String[] arr = targetString.split(" ");
        if (arr[0].equals("delete") && arr.length == 2) {
            try {
                Integer taskNum = Integer.valueOf(arr[1]);
                if (taskNum <= tasklist.size() && taskNum >= 1) {
                    return true;
                } else {
                    System.out.println("You might have tried to delete a task that does not exist.\n");
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void parseTaskAddition(String input) throws EmptyTodoException, UnknownCommandException, DeadlineFormatException {
        // Want to split the string according to spaces 1st
        String[] splitString = input.split(" ", 2);
        String name;
        if (splitString[0].equals("todo")) {
            if (splitString.length == 1) {
                throw new EmptyTodoException();
            }
            String taskName = splitString[1];
            tasklist.add(new Todo(taskName));
            name = taskName;
        } else if (splitString[0].equals("deadline")) {
            String[] components = splitString[1].split(" /by ", 2);
            if (components.length < 2) {
                throw new DeadlineFormatException();
            }
            tasklist.add(new Deadline(components[0], components[1]));
            name = components[0];
        } else if (splitString[0].equals("event")) {
            // Idea is that original string is in the form {event_name} /from {start} /to {end}
            // Hence first split will get event name and the {start} /to {end}
            // Second split will split the {start} /to {end} to get the actual start and end
            String[] component1 = splitString[1].split(" /from ", 2);
            String[] component2 = component1[1].split(" /to ", 2);
            tasklist.add(new Event(component1[0], component2[0], component2[1]));
            name = component1[0];
        } else {
            throw new UnknownCommandException(input);
        }
        System.out.println(String.format("Hey %s, I have added \"%s\" into your task list!\n" +
                "You now have %d tasks to complete!\n", username, name, tasklist.size()));
    }
}
