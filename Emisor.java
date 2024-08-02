import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Emisor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Solicitar el mensaje al usuario
        System.out.print("Ingrese un mensaje de texto: ");
        String mensajeStr = scanner.nextLine();
        
        // Solicitar el algoritmo al usuario
        System.out.print("Seleccione el algoritmo de integridad (1: CRC-32, 2: Hamming): ");
        int algoritmo = scanner.nextInt();
        
        // Solicitar la probabilidad de error
        System.out.print("Ingrese la probabilidad de error (por ejemplo, 0.01 para 1%): ");
        double errorProbability = scanner.nextDouble();

        // Solicitar el número de pruebas
        System.out.print("Ingrese el número de pruebas: ");
        int numPruebas = scanner.nextInt();
        
        for (int i = 0; i < numPruebas; i++) {
            String binMessage = stringToBin(mensajeStr);
            String messageWithIntegrity = "";

            if (algoritmo == 1) {
                messageWithIntegrity = applyCRC32(binMessage); // 1
            } else if (algoritmo == 2) {
                messageWithIntegrity = applyHamming(binMessage); // 2
            } else {
                System.out.println("Algoritmo no soportado.");
                return;
            }

            // Aplicar ruido
            String messageWithNoise = applyNoise(messageWithIntegrity, errorProbability);

            try {
                // Enviar el mensaje al receptor
                Socket socket = new Socket("localhost", 12345);
                OutputStream outputStream = socket.getOutputStream();

                String jsonString = String.format("{\"Algorithm\": \"%s\", \"Tests\": %d, \"Message\": \"%s\"}", algoritmo, numPruebas, messageWithNoise);
            
                outputStream.write(jsonString.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        scanner.close();
    }

    public static String stringToBin(String s) {
        StringBuilder result = new StringBuilder();
        for (char c : s.toCharArray()) {
            result.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return result.toString();
    }

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

    public static int calculateCRC32(byte[] data) {
        int crc = 0xFFFFFFFF;
        for (byte b : data) {
            crc ^= b;
            for (int i = 0; i < 8; i++) {
                if ((crc & 1) != 0) {
                    crc = (crc >> 1) ^ 0xEDB88320;
                } else {
                    crc >>= 1;
                }
            }
        }
        return crc ^ 0xFFFFFFFF;
    }

    public static String applyCRC32(String binMessage) {
        byte[] messageBytes = binaryStringToBytes(binMessage);
        int crc = calculateCRC32(messageBytes);
        String crcBin = String.format("%32s", Integer.toBinaryString(crc)).replace(' ', '0');
        return binMessage + crcBin;
    }

    public static int calculateParity(int[] bits, int[] positions) {
        int parity = 0;
        for (int position : positions) {
            parity ^= bits[position - 1];
        }
        return parity;
    }

    public static String applyHamming(String binMessage) {
        int k = binMessage.length();
        int r = 1;
        while ((k + r + 1) > (1 << r)) {
            r += 1;
        }
        int n = k + r;
        int[] bits = new int[n];
        List<Integer> dataBits = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            if ((i & (i - 1)) == 0) {
                // Skip parity positions
            } else {
                dataBits.add(i - 1);
            }
        }

        for (int i = 0; i < binMessage.length(); i++) {
            bits[dataBits.get(i)] = Character.getNumericValue(binMessage.charAt(i));
        }

        for (int i = 0; i < r; i++) {
            int positionParity = 1 << i;
            List<Integer> parityPos = new ArrayList<>();
            for (int j = 1; j <= n; j++) {
                if (((j >> i) & 1) == 1) {
                    parityPos.add(j);
                }
            }
            bits[positionParity - 1] = calculateParity(bits, parityPos.stream().mapToInt(Integer::intValue).toArray());
        }

        StringBuilder codedMessage = new StringBuilder();
        for (int bit : bits) {
            codedMessage.append(bit);
        }
        return codedMessage.toString();
    }

    public static String applyNoise(String message, double errorProbability) {
        StringBuilder noisyMessage = new StringBuilder(message);
        Random rand = new Random();
        for (int i = 0; i < message.length(); i++) {
            if (rand.nextDouble() < errorProbability) {
                noisyMessage.setCharAt(i, message.charAt(i) == '0' ? '1' : '0');
            }
        }
        return noisyMessage.toString();
    }
}
