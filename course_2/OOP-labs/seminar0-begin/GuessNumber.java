import java.lang.Math;
import java.util.InputMismatchException;
import java.util.Scanner;

class Program {

    public static void main(String[] args) {
        GuessNumberGame.play();
    }
}

class GuessNumberGame {

    public static final int MAX_ATTEMPTS = 8;

    private static int getUserInput(Scanner scanner) {
        boolean isValid = false;
        int input = 0;
        do {
            try {
                input = scanner.nextInt();
                isValid = true;
            } catch (InputMismatchException e) {
                System.out.println(
                    "Invalid input. You can input only digits from 1 to 100"
                );
                System.out.println("Try again");
                scanner.next(); // skipping invalid token
                isValid = false;
            }
        } while (!isValid);

        return input;
    }

    public static void play() {
        int number = (int) (Math.random() * 101); // from 0 to 100.999999
        Scanner scanner = new Scanner(System.in);

        boolean isWin = false;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            int userInput = getUserInput(scanner);
            if (userInput > number) {
                System.out.println("Less");
            } else if (userInput < number) {
                System.out.println("More");
            } else {
                System.out.println(
                    "You win! It tooks " + attempt + " attempts"
                );
                isWin = true;
                break;
            }
        }

        if (!isWin) {
            System.out.println("You lose :(  The number was: " + number);
        }
    }
}
