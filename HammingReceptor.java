import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HammingReceptor {

    // Método para verificar si la cadena es binaria válida
    private static boolean isValidBinaryString(String data) {
        for (char c : data.toCharArray()) {
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }

    // Método para calcular la paridad
    private static int calcularParidad(int[] bits, int[] posiciones) {
        int paridad = 0;
        for (int posicion : posiciones) {
            paridad ^= bits[posicion - 1];
        }
        return paridad;
    }

    // Método para detectar y corregir errores en el mensaje codificado
    private static String detectarYCorregirError(String encodedBits, int n, int k) {
        int r = n - k;
        int posicionError = 0;

        int[] bits = new int[n];
        for (int i = 0; i < n; i++) {
            bits[i] = Character.getNumericValue(encodedBits.charAt(i));
        }

        for (int i = 0; i < r; i++) {
            int posicionParidad = (int) Math.pow(2, i);
            List<Integer> paridadPos = new ArrayList<>();
            for (int j = 1; j <= n; j++) {
                if ((j & posicionParidad) != 0) {
                    paridadPos.add(j);
                }
            }
            int paridad = calcularParidad(bits, paridadPos.stream().mapToInt(Integer::intValue).toArray());
            if (paridad != 0) {
                posicionError += posicionParidad;
            }
        }

        if (posicionError != 0) {
            posicionError -= 1;
            bits[posicionError] = bits[posicionError] == 0 ? 1 : 0;
            System.out.println("Error detectado en la posición: " + (posicionError + 1));
            StringBuilder correctedBits = new StringBuilder();
            for (int bit : bits) {
                correctedBits.append(bit);
            }
            System.out.println("Bits corregidos: " + correctedBits.toString());
        } else {
            System.out.println("No se detectó ningún error.");
        }

        StringBuilder resultado = new StringBuilder();
        for (int bit : bits) {
            resultado.append(bit);
        }

        return resultado.toString();
    }

    // Método para convertir de Hamming a ASCII
    private static String hammingToAscii(String encodedBits, int n, int k) {
        String correctedBits = detectarYCorregirError(encodedBits, n, k);

        StringBuilder dataBits = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if ((i + 1 & (i + 1 - 1)) == 0) {  // omitir bits de paridad (potencias de 2)
                continue;
            }
            dataBits.append(correctedBits.charAt(i));
        }

        return dataBits.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String encodedMessage = "";

        while (true) {
            System.out.println("Ingrese el mensaje codificado: ");
            encodedMessage = scanner.nextLine();
            if (isValidBinaryString(encodedMessage)) {
                System.out.println("Ingrese el valor de n (número total de bits): ");
                int n = scanner.nextInt();
                scanner.nextLine(); // Consumir la nueva línea
                if (encodedMessage.length() == n) {
                    break;
                } else {
                    System.out.println("El número total de bits no coincide con la longitud de la cadena ingresada.");
                }
            } else {
                System.out.println("Entrada no válida. Por favor, ingrese solo una cadena de bits (0s y 1s).");
            }
        }

        System.out.println("Ingrese el valor de k (número de bits de datos): ");
        int k = scanner.nextInt();

        String correctedMessage = hammingToAscii(encodedMessage, encodedMessage.length(), k);
        System.out.println("Mensaje original corregido: " + correctedMessage);

        scanner.close();
    }
}
