import model.Island;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🏝️  СИМУЛЯЦИЯ ЭКОСИСТЕМЫ ОСТРОВА");
        System.out.println("=".repeat(60));

        Scanner scanner = new Scanner(System.in);
        System.out.print("Запустить симуляцию? (y/n): ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("y")) {
            Island island = new Island();

            Thread simulationThread = new Thread(() -> {
                island.startSimulation();

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    System.out.println("Симуляция прервана пользователем.");
                }

                island.stopSimulation();
            });

            simulationThread.start();

            System.out.println("\nСимуляция запущена. Нажмите Enter для остановки...");
            scanner.nextLine();

            simulationThread.interrupt();

            try {
                simulationThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("\n✅ Программа завершена.");
        } else {
            System.out.println("Симуляция отменена.");
        }

        scanner.close();
    }
}