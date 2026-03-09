// Solving quadratic (and linear too) equations
import java.util.Scanner;

class Solver {

    public static void main(String[] args) {
        // solve
        Scanner scanner = new Scanner(System.in);
        double a = scanner.nextDouble();
        double b = scanner.nextDouble();
        double c = scanner.nextDouble();

        SolveEquation(a, b, c);
    }

    // solves: bx + c = 0
    private static void SolveLinear(double b, double c) {
        if (b == 0) {
            if (c == 0) {
                System.out.println("All real numbers");
            } else {
                System.out.println("--- 1 solution ---");
                System.out.println("x = 0");
            }
        } else {
            System.out.println("--- 1 solution ---");
            System.out.println("x = " + (-c / b));
        }
    }

    // solves: ax^2 + bx + c = 0
    private static void SolveEquation(double a, double b, double c) {
        if (a == 0) {
            SolveLinear(b, c);
            return;
        }
        double discriminant = b * b - 4 * a * c;
        if (discriminant > 0) {
            System.out.println("--- 2 solutions ---");
            double x1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            double x2 = (-b - Math.sqrt(discriminant)) / (2 * a);
            System.out.println("x1 = " + x1 + ", x2 = " + x2);
        } else if (discriminant == 0) {
            System.out.println("--- 1 solution ---");
            double x = -b / (2 * a);
            System.out.println("x = " + x);
        } else {
            System.out.println("No solutions in real numbers");
        }
    }
}
