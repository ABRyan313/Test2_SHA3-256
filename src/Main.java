import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private static final String EMAIL = "abir4044@diu.edu.bd";

    public static void main(String[] args) {
        String archivePath = "E:\\JAVA\\Test2_SHA3-256\\task2.zip";
        String outputDir = "E:\\JAVA\\Test2_SHA3-256\\unzipped";

        try {
            List <String> hashes = unzipAndProcessFiles(archivePath, outputDir);
            Collections.sort(hashes);
            StringBuilder concatenatedHashes = new StringBuilder();
            for (String hash : hashes) {

                concatenatedHashes.append(hash);
            }
            String finalString = concatenatedHashes.toString() + EMAIL.toLowerCase();
            byte[] finalHash = calculateSHA3_256(finalString.getBytes());
            System.out.println(bytesToHex(finalHash));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> unzipAndProcessFiles(String archivePath, String outputDir) throws IOException {
        Path path = Paths.get(archivePath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IOException("Invalid file path: " + archivePath);
        }

        Path outputDirPath = Paths.get(outputDir);
        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }

        List<String> hashes = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                Path outputPath = outputDirPath.resolve(entry.getName());
                File outputFile = outputPath.toFile();
                outputFile.getParentFile().mkdirs();
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[256];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
                byte[] hash = calculateSHA3_256(outputFile);
                hashes.add(bytesToHex(hash));
                zis.closeEntry();
            }
        }

        return hashes;
    }

    public static byte[] calculateSHA3_256(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
            byte[] buffer = new byte[256];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digestSHA3.update(buffer, 0, bytesRead);
            }
            return digestSHA3.digest();
        }
    }

    public static byte[] calculateSHA3_256(byte[] input) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return digestSHA3.digest(input);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
