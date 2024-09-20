import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Выбор прграммы для открытия
        for (int i = 0; i < 3; i++) {
            System.out.println("Введите программу, которую вы хотите запустить" +
                    "(1 - Блокнот, 2 - Калькулятор, " +
                    "3 - Paint): ");
            int programNumber = scanner.nextInt();

            switch (programNumber) {
                case 1:
                    openProgram("notepad.exe");
                    break;
                case 2:
                    openProgram("calc.exe");
                    break;
                case 3:
                    openProgram("mspaint.exe");
                    break;
                default:
                    System.out.println("Вы ввели неверный номер команды. Введите его заново");
                    i--;
            }
        }

        // Вывод списка всех активных процессов
        List<String> processes = getProcesses();

        // Закрытие программ
        for (int i = 0; i < 3; i++) {
            System.out.println("Введите программу, которую вы хотите закрыть" +
                    "(1 - Блокнот, 2 - Калькулятор, " +
                    "3 - Paint): ");
            int programNumber = scanner.nextInt();

            switch (programNumber) {
                case 1:
                    closeProgram("Notepad.exe", processes);
                    break;
                case 2:
                    closeProgram("CalculatorApp.exe", processes);
                    break;
                case 3:
                    closeProgram("mspaint.exe", processes);
                    break;
                default:
                    System.out.println("Вы ввели неверный номер команды. Введите его заново");
                    i--;
            }
        }
    }

    // Открытие программы по ее названию
    private static void openProgram(String program) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(program);
        pb.start();
        System.out.println("Программа запущена");
    }

    // Закртие программ с помощью поиска по названию прилжения и получению PID
    private static void closeProgram(String program, List<String> processes) throws IOException {
        for (String p: processes) {
            if (p.toUpperCase().contains(program.toUpperCase())) {
                String pid = p.split("\\|")[2];
                System.out.println(pid);
                ProcessBuilder pb = new ProcessBuilder("taskkill", "/PID", String.valueOf(pid), "/T", "/F");
                pb.start();
            }
        }
    }

    // Получение списка всех процессов
    private static List<String> getProcesses() throws Exception {
        // Список всех запущенных процессов
        List<String> processes = new ArrayList<>();

        // Запускаем команду на получение списка всех процессов
        Process process = Runtime.getRuntime().exec("tasklist");

        // Открываем поток для чтения результатов из командной стпроки
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

        System.out.println("-------------------------------------------------------------------");
        System.out.println("| Image Name | PID | Session Name | Session | Mem Usage |");

        // Номер выводимой строки
        int lineNumber = 0;

        // Пока чтения не закончилось обраьатываем каждую линию
        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber += 1;

            // Работаем только со строками, в которых есть информация о процессах
            if (lineNumber > 3) {
                List<String> information = List.of(line.split(" {2}"));
                String informationLine = "|";

                // Переменная для последующего разделения колонки PID и Console
                int dataNumber = 0;

                // Добавляем к выводимой строке инфомацию о процессе
                for (String s : information) {
                    if (!Objects.equals(s, "")) {
                        if (dataNumber == 1) {
                            String[] pidAndSessionName = s.split(" ");
                            int size = pidAndSessionName.length;
                            informationLine += pidAndSessionName[size - 2] + "\t|" + pidAndSessionName[size - 1] + "\t|";
                        }
                        else if (dataNumber > 2) informationLine += s.substring(0, s.length() - 2) + "\t|";
                        else informationLine += s + "\t|";
                        dataNumber += 1;
                    }
                }
                informationLine += "Кб|";

                // Вывод информационной строки на экран
                System.out.println(informationLine);

                // Добавление информации о процессе в список
                processes.add(informationLine);
            }
        }
        System.out.println("-------------------------------------------------------------------");
        reader.close();

        return processes;
    }
}