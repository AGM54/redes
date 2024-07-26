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

def manipular_mensaje(mensaje_codificado):
    mensaje_manipulado = list(mensaje_codificado)
    mensaje_manipulado[0] = '1' if mensaje_manipulado[0] == '0' else '0'
    mensaje_manipulado[1] = '1' if mensaje_manipulado[1] == '0' else '0'
    return ''.join(mensaje_manipulado)

if __name__ == "__main__":
    n = int(input("Ingrese el número total de bits (n): "))
    k = int(input("Ingrese el número de bits de datos (k): "))
    
    mensaje = input(f"Ingrese un mensaje binario de {k} bits: ")
    if len(mensaje) != k:
        print(f"El mensaje debe tener exactamente {k} bits.")
        exit()

    mensaje_codificado = codificar_hamming(mensaje, n, k)
    print(f"Mensaje codificado: {mensaje_codificado}")

    mensaje_manipulado = manipular_mensaje(mensaje_codificado)
    #print(f"Mensaje manipulado: {mensaje_manipulado}")
