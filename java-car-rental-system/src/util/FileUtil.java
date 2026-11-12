package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class FileUtil {
    private FileUtil() {}

    public static String[] readAllLines(String path) {
        Path p = Path.of(path);
        if (!Files.exists(p)) return new String[0];
        String[] lines = new String[0];
        try (BufferedReader br = new BufferedReader(new FileReader(p.toFile(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines = append(lines, line);
            }
        } catch (IOException e) {
            return new String[0];
        }
        return lines;
    }

    public static void writeAllLines(String path, String[] lines) {
        if (lines == null) lines = new String[0];
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8, false))) {
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] == null) continue;
                bw.write(lines[i]);
                if (i < lines.length - 1) bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[Warning] Could not write file: " + path + " => " + e.getMessage());
        }
    }

    public static void appendLine(String path, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("[Warning] Could not append file: " + path + " => " + e.getMessage());
        }
    }

    private static String[] append(String[] array, String value) {
        String[] out = Arrays.copyOf(array, array.length + 1);
        out[array.length] = value;
        return out;
    }
}
