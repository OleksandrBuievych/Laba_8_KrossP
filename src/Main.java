import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
    public static void main(String[] args) {
        // Вікно для вибору початкової директорії
        JFileChooser sourceChooser = new JFileChooser();
        sourceChooser.setDialogTitle("Виберіть початкову директорію");
        sourceChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int sourceResult = sourceChooser.showOpenDialog(null);
        if (sourceResult != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String sourceDir = sourceChooser.getSelectedFile().getAbsolutePath();

        // Вікно для вибору цільової директорії
        JFileChooser destChooser = new JFileChooser();
        destChooser.setDialogTitle("Виберіть цільову директорію");
        destChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int destResult = destChooser.showOpenDialog(null);
        if (destResult != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String destDir = destChooser.getSelectedFile().getAbsolutePath();

        // Вікно для вибору шляху для збереження текстового файлу
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Виберіть шлях для збереження текстового файлу");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        int fileResult = fileChooser.showSaveDialog(null);
        if (fileResult != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String logFile = fileChooser.getSelectedFile().getAbsolutePath();

        try {
            FileWriter fileWriter = new FileWriter(logFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Отримуємо поточну дату
            Date currentDate = new Date();

            // Віднімаємо 3 дні від поточної дати
            long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000;
            Date threeDaysAgo = new Date(currentDate.getTime() - threeDaysInMillis);

            // Форматуємо дату у потрібний формат для порівняння з датою модифікації файлу
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Проходимо по всіх файлах і директоріях в початковій директорії
            Files.walkFileTree(Paths.get(sourceDir), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Отримуємо дату модифікації файлу
                    Date lastModified = new Date(attrs.lastModifiedTime().toMillis());

                    // Порівнюємо дату модифікації з датою 3 днів тому
                    if (lastModified.after(threeDaysAgo)) {
                        // Копіюємо файл у цільову директорію
                        Path destFile = Paths.get(destDir, file.getFileName().toString());
                        Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);

                        // Виводимо назву файлу на екран
                        System.out.println(file.getFileName());

                        // Записуємо назву файлу у текстовий файл
                        printWriter.println(file.getFileName());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
