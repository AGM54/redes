import java.util.Scanner;

public class CRC32Receptor {

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el mensaje en binario con CRC-32: ");
        String binMessageWithCRC = scanner.nextLine();

        if (checkCRC32(binMessageWithCRC)) {
            String originalMessage = binMessageWithCRC.substring(0, binMessageWithCRC.length() - 32);
            System.out.println("No se detectaron errores: " + originalMessage);
        } else {
            System.out.println("Se detectaron errores: el mensaje se descarta.");
        }
    }
}
