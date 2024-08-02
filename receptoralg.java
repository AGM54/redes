import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class receptoralg {

    public static byte[] binaryStringToBytes(String binStr) {
        int byteLength = (binStr.length() + 7) / 8;
        byte[] result = new byte[byteLength];
        for (int i = 0; i < binStr.length(); i++) {
            if (binStr.charAt(i) == '1') {
                result[i / 8] |= 1 << (7 - (i % 8));
            }
        }
        return result;
    }

    public static long calculateCRC32(byte[] data) {
        long crc = 0xFFFFFFFFL;
        for (byte b : data) {
            crc ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 1) != 0) {
                    crc = (crc >> 1) ^ 0xEDB88320L;
                } else {
                    crc >>= 1;
                }
            }
        }
        return crc ^ 0xFFFFFFFFL;
    }

    public static boolean checkCRC32(String messageWithCRC) {
        String messageBin = messageWithCRC.substring(0, messageWithCRC.length() - 32);
        String crcBin = messageWithCRC.substring(messageWithCRC.length() - 32);
        byte[] messageBytes = binaryStringToBytes(messageBin);
        long receivedCRC = Long.parseUnsignedLong(crcBin, 2);
        long calculatedCRC = calculateCRC32(messageBytes);
        return receivedCRC == calculatedCRC;
    }

    public static int calculateParity(int[] bits, int[] positions) {
        int parity = 0;
        for (int position : positions) {
            parity ^= bits[position - 1];
        }
        return parity;
    }

    public static String detectAndCorrectHamming(String encodedBits, int n, int k) {
        int r = n - k;
        int errorPosition = 0;

        int[] bits = new int[n];
        for (int i = 0; i < n; i++) {
            bits[i] = Character.getNumericValue(encodedBits.charAt(i));
        }

        for (int i = 0; i < r; i++) {
            int positionParity = (int) Math.pow(2, i);
            List<Integer> parityPos = new ArrayList<>();
            for (int j = 1; j <= n; j++) {
                if ((j & positionParity) != 0) {
                    parityPos.add(j);
                }
            }
            int parity = calculateParity(bits, parityPos.stream().mapToInt(Integer::intValue).toArray());
            if (parity != 0) {
                errorPosition += positionParity;
            }
        }

        if (errorPosition != 0) {
            errorPosition -= 1;
            if (errorPosition >= 0 && errorPosition < bits.length) {
                bits[errorPosition] = bits[errorPosition] == 0 ? 1 : 0;
                System.out.println("Error detectado y corregido en la posición: " + (errorPosition + 1));
                return "Error detectado y corregido en la posición: " + (errorPosition + 1);
            } else {
                System.out.println("Error detectado pero la posición está fuera del rango permitido.");
                return "Error detectado pero la posición está fuera del rango permitido.";
            }
        } else {
            System.out.println("No se detectó ningún error.");
            return "No se detectó ningún error.";
        }
    }

    public static String extractDataBits(String correctedBits, int n, int k) {
        StringBuilder dataBits = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if ((i + 1 & (i + 1)) != 0) {  // omitir bits de paridad (potencias de 2)
                dataBits.append(correctedBits.charAt(i));
            }
        }
        return dataBits.toString();
    }

    public static String getOriginalMessage(String messageWithParity) {
        // Calculate number of parity bits needed
        int m = messageWithParity.length();
        int r = (int) Math.ceil(Math.log(m) / Math.log(2));
        int k = m - r;

        // Remove parity bits to get original message
        StringBuilder originalMessage = new StringBuilder();
        for (int i = 0; i < m; i++) {
            if ((i + 1 & (i + 1) - 1) != 0) {
                originalMessage.append(messageWithParity.charAt(i));
            }
        }

        return originalMessage.toString();
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor escuchando en el puerto 12345");
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Conexión aceptada de " + clientSocket.getRemoteSocketAddress());
                    InputStream inputStream = clientSocket.getInputStream();
                    OutputStream outputStream = clientSocket.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead = inputStream.read(buffer);
                    String data = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    System.out.println("Datos recibidos: " + data);
                    char algorithm = data.charAt(0);
                    String message = data.substring(1);
                    String response = "";

                    if (algorithm == '3') {
                        String messageWithCRC = message;
                        String originalMessage = messageWithCRC.substring(0, messageWithCRC.length() - 32);
                        System.out.println("Mensaje en binario: " + originalMessage);
                        response = "Mensaje en binario: " + originalMessage + "\n";
                        if (checkCRC32(messageWithCRC)) {
                            System.out.println("CRC-32 verificado correctamente.");
                            response += "CRC-32 verificado correctamente.\n";
                        } else {
                            System.out.println("Error de CRC-32 detectado.");
                            response += "Error de CRC-32 detectado.\n";
                        }
                    } else if (algorithm == '2') {
                        String encodedBits = message;
                        int n = encodedBits.length();
                        int r = (int) (Math.log(n + 1) / Math.log(2));
                        int k = n - r;
                        String originalMessage = getOriginalMessage(encodedBits);
                        System.out.println("Mensaje original: " + originalMessage);
                        response = "Mensaje original: " + originalMessage + "\n";
                        System.out.println("Mensaje en binario recibido: " + encodedBits);
                        response += "Mensaje en binario recibido: " + encodedBits + "\n";
                        String correctedMessage = detectAndCorrectHamming(encodedBits, n, k);
                        String originalDataBits = extractDataBits(correctedMessage, n, k);
                        System.out.println("Mensaje corregido: " + correctedMessage);
                        response += "Mensaje corregido: " + correctedMessage + "\n";
                        System.out.println("Mensaje original corregido (binario): " + originalDataBits);
                        response += "Mensaje original corregido (binario): " + originalDataBits + "\n";
                    } else {
                        System.out.println("Algoritmo no soportado.");
                        response = "Algoritmo no soportado.\n";
                    }

                    outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
