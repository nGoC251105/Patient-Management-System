// PTIT INT1332 - NHAP VA XU LY MENU CONSOLE.
// Scanner doc tung dong, Integer.parseInt/Double.parseDouble chuyen du lieu.
// try/catch chong loi khi nhap chu thay vi so; do/while hoac while de nhap lai.
// <E> la generic method, cho phep dung menu voi nhieu kieu du lieu.
import java.util.ArrayList;
import java.util.Scanner;

/** Shared Console input reader: avoids competing Scanners on System.in. */
public final class Menu {
    private static final Scanner INPUT = new Scanner(System.in, "UTF-8");
    private Menu() { }

    /** Returns a whole input line (with a helpful EOF message). */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        if (!INPUT.hasNextLine()) throw new IllegalStateException("End of input.");
        return INPUT.nextLine().trim();
    }

    /** Reads an int; invalid text and values outside [min,max] are retried. */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String raw = readLine(prompt);
            try {
                int number = Integer.parseInt(raw);
                if (number >= min && number <= max) return number;
            } catch (NumberFormatException ignored) {
                // The same feedback applies to nonnumeric or out-of-range data.
            }
            System.out.println("Invalid integer. Enter " + min + " to " + max + ".");
        }
    }

    /** Reads a positive finite monetary value. */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            try {
                double number = Double.parseDouble(readLine(prompt));
                if (Double.isFinite(number) && number > 0) return number;
            } catch (NumberFormatException ignored) { }
            System.out.println("Invalid service fee. Enter a number greater than 0.");
        }
    }

    /** Reads a required text field without comma/newline, matching file format. */
    public static String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty() && value.indexOf(',') < 0) return value;
            System.out.println("Value must not be empty or contain a comma.");
        }
    }

    /** Menu indexes correspond to the supplied ArrayList indexes, starting at 0. */
    public static <E> int int_getChoice(ArrayList<E> options) {
        if (options == null || options.isEmpty()) throw new IllegalArgumentException("No menu options.");
        for (int i = 0; i < options.size(); i++)
            System.out.println(i + ". " + options.get(i));
        return readInt("Your choice: ", 0, options.size() - 1);
    }

    /** Selects a reference to an existing element, not arbitrary free-text ID. */
    public static <E> E ref_getChoice(ArrayList<E> options) {
        if (options == null || options.isEmpty()) throw new IllegalArgumentException("No options to select.");
        for (int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i));
        return options.get(readInt("Choose 1-" + options.size() + ": ", 1, options.size()) - 1);
    }
}
