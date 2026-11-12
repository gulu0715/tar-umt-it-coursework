package util;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.Predicate;

public final class InputUtil {
    public static final Scanner SC = new Scanner(System.in);
    private InputUtil() {}

    public static String readNonEmptyString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public static String readOptionalString(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static Double readOptionalDouble(Scanner sc, String prompt) {
        while (true) {
            String input = readOptionalString(sc, prompt);
            if (input.isEmpty()) return null;
            try {
                return Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Invalid number. Please enter again or press Enter to keep current value.");
            }
        }
    }

    public static Integer readOptionalInt(Scanner sc, String prompt) {
        while (true) {
            String input = readOptionalString(sc, prompt);
            if (input.isEmpty()) return null;
            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Invalid number. Please enter again or press Enter to keep current value.");
            }
        }
    }

    public static Double readOptionalPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            Double value = readOptionalDouble(sc, prompt);
            if (value == null) return null;
            if (value > 0) return value;
            System.out.println("Please enter a number greater than 0, or press Enter to keep current value.");
        }
    }

    public static Integer readOptionalNonNegativeInt(Scanner sc, String prompt) {
        while (true) {
            Integer value = readOptionalInt(sc, prompt);
            if (value == null) return null;
            if (value >= 0) return value;
            System.out.println("Please enter a number that is 0 or greater, or press Enter to keep current value.");
        }
    }

    public static Integer readOptionalPositiveInt(Scanner sc, String prompt) {
        while (true) {
            Integer value = readOptionalInt(sc, prompt);
            if (value == null) return null;
            if (value > 0) return value;
            System.out.println("Please enter a number greater than 0, or press Enter to keep current value.");
        }
    }

    public static String readValidatedString(Scanner sc, String prompt, Predicate<String> validator, String errorMessage) {
        while (true) {
            String input = readNonEmptyString(sc, prompt);
            if (validator == null || validator.test(input)) return input;
            System.out.println(errorMessage);
        }
    }

    public static String readOptionalValidatedString(Scanner sc, String prompt, Predicate<String> validator, String errorMessage) {
        while (true) {
            String input = readOptionalString(sc, prompt);
            if (input.isEmpty()) return input;
            if (validator == null || validator.test(input)) return input;
            System.out.println(errorMessage);
        }
    }

    public static String readBackableValidatedString(Scanner sc, String prompt, Predicate<String> validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.equals("0")) return null;
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Enter 0 to go back.");
                continue;
            }
            if (validator == null || validator.test(input)) return input;
            System.out.println(errorMessage);
        }
    }

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Invalid number. Please enter again.");
            }
        }
    }

    public static int readIntInRange(Scanner sc, String prompt, int min, int max) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value >= min && value <= max) return value;
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    public static int readNonNegativeInt(Scanner sc, String prompt) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value >= 0) return value;
            System.out.println("Please enter a number that is 0 or greater.");
        }
    }

    public static int readPositiveInt(Scanner sc, String prompt) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value > 0) return value;
            System.out.println("Please enter a number greater than 0.");
        }
    }

    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Invalid number. Please enter again.");
            }
        }
    }

    public static double readPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value > 0) return value;
            System.out.println("Please enter a number greater than 0.");
        }
    }

    public static double readNonNegativeDouble(Scanner sc, String prompt) {
        while (true) {
            double value = readDouble(sc, prompt);
            if (value >= 0) return value;
            System.out.println("Please enter a number that is 0 or greater.");
        }
    }

    public static boolean readYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) return true;
            if (input.equalsIgnoreCase("N")) return false;
            System.out.println("Please enter Y or N.");
        }
    }

    public static LocalDate readDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("Invalid date. Use yyyy-mm-dd.");
            }
        }
    }

    public static LocalDate readDateOnOrAfter(Scanner sc, String prompt, LocalDate minDate) {
        while (true) {
            LocalDate date = readDate(sc, prompt);
            if (minDate == null || !date.isBefore(minDate)) return date;
            System.out.println("Date cannot be before " + minDate + ".");
        }
    }
}
