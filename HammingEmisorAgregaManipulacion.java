import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HammingEmisor {

 
    private static int calcularParidad(int[] bits, int[] posiciones) {
        int paridad = 0;
        for (int posicion : posiciones) {
            paridad ^= bits[posicion - 1];
        }
        return paridad;
    }

    private static String codificarHamming(String datos, int n, int k) {
        int r = n - k;
        int[] bits = new int[n];
        List<Integer> dataBits = new ArrayList<>();
     
        for (int i = 1, j = 0; i <= n; i++) {
            if ((i & (i - 1)) == 0) { 

            } else {
                dataBits.add(i - 1);
            }
        }
        

        for (int i = 0; i < datos.length(); i++) {
            bits[dataBits.get(i)] = Character.getNumericValue(datos.charAt(i));
        }
      
        for (int i = 0; i < r; i++) {
            int posicionParidad = (int) Math.pow(2, i);
            List<Integer> paridadPos = new ArrayList<>();
            for (int j = 1; j <= n; j++) {
                if (((j >> i) & 1) == 1) {
                    paridadPos.add(j);
                }
            }
            bits[posicionParidad - 1] = calcularParidad(bits, paridadPos.stream().mapToInt(Integer::intValue).toArray());
        }
        
      
        StringBuilder codificado = new StringBuilder();
        for (int bit : bits) {
            codificado.append(bit);
        }
        
        return codificado.toString();
    }

   
    private static String manipularMensaje(String mensajeCodificado) {
        StringBuilder mensajeManipulado = new StringBuilder(mensajeCodificado);
        mensajeManipulado.setCharAt(0, mensajeManipulado.charAt(0) == '0' ? '1' : '0');
        mensajeManipulado.setCharAt(1, mensajeManipulado.charAt(1) == '0' ? '1' : '0');
        return mensajeManipulado.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Ingrese el número total de bits (n):");
        int n = scanner.nextInt();
        System.out.println("Ingrese el número de bits de datos (k):");
        int k = scanner.nextInt();
        scanner.nextLine(); 
        
        System.out.println("Ingrese un mensaje binario de " + k + " bits:");
        String mensaje = scanner.nextLine();
        if (mensaje.length() != k) {
            System.out.println("El mensaje debe tener exactamente " + k + " bits.");
            return;
        }
        
        String mensajeCodificado = codificarHamming(mensaje, n, k);
        System.out.println("Mensaje codificado: " + mensajeCodificado);
        
        String mensajeManipulado = manipularMensaje(mensajeCodificado);
        System.out.println("Mensaje manipulado: " + mensajeManipulado);
        
        scanner.close();
    }
}
