import socket
import random

def string_to_bin(s):
    return ''.join(format(ord(char), '08b') for char in s)

def calcular_paridad(bits, posiciones):
    paridad = 0
    for posicion in posiciones:
        paridad ^= int(bits[posicion - 1])
    return paridad

def codificar_hamming(datos, n, k):
    r = n - k
    bits = [0] * n
    data_bits = []

    for i in range(1, n + 1):
        if (i & (i - 1)) == 0:  # es una potencia de 2 (posición de paridad)
            continue
        else:
            data_bits.append(i - 1)

    for i in range(len(datos)):
        bits[data_bits[i]] = int(datos[i])

    for i in range(r):
        posicion_paridad = 2 ** i
        paridad_pos = []
        for j in range(1, n + 1):
            if (j >> i) & 1 == 1:
                paridad_pos.append(j)
        bits[posicion_paridad - 1] = calcular_paridad(bits, paridad_pos)

    return ''.join(map(str, bits))

def aplicar_ruido(bits, probabilidad_error):
    bits_list = list(bits)
    for i in range(len(bits_list)):
        if random.random() < probabilidad_error:
            bits_list[i] = '1' if bits_list[i] == '0' else '0'
    return ''.join(bits_list)

def main_emisor():
    mensaje_str = input("Ingrese un mensaje de texto: ")
    probabilidad_error = float(input("Ingrese la probabilidad de error (por ejemplo, 0.01 para 1%): "))
    num_pruebas = int(input("Ingrese el número de pruebas a realizar: "))
    
    for i in range(num_pruebas):
        mensaje_bin = string_to_bin(mensaje_str)
        k = len(mensaje_bin)

        # Calcular r y n adecuadamente
        r = 1
        while (k + r + 1) > (1 << r):
            r += 1
        n = k + r

        mensaje_codificado = codificar_hamming(mensaje_bin, n, k)
        mensaje_con_ruido = aplicar_ruido(mensaje_codificado, probabilidad_error)

        # Enviar a través de sockets
        host = 'localhost'
        port = 65432

        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((host, port))
            s.sendall(mensaje_con_ruido.encode())
            print(f"Prueba {i+1}: Mensaje enviado con ruido: {mensaje_con_ruido}")

if __name__ == "__main__":
    main_emisor()
