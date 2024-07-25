def is_valid_binary_string(data):
    return all(char in '01' for char in data)

def calculate_parity(bits, positions):
    parity = 0
    for position in positions:
        parity ^= int(bits[position - 1])
    return parity

def detect_and_correct_error(encoded_bits, n, k):
    r = n - k
    posicion_error = 0

    for i in range(r):
        posicion_paridad = 2 ** i
        paridad = 0

        for j in range(1, n + 1):
            if j & posicion_paridad:
                paridad ^= int(encoded_bits[j - 1])
        posicion_error += paridad * posicion_paridad

    if posicion_error != 0:
        posicion_error -= 1
        codigo_corregido = list(encoded_bits)
        codigo_corregido[posicion_error] = '1' if codigo_corregido[posicion_error] == '0' else '0'
        encoded_bits = ''.join(codigo_corregido)
        print(f"Error detectado en la posición: {posicion_error + 1}")
        print(f"Bits corregidos: {encoded_bits}")
    else:
        print("No se detectó ningún error.")

    return encoded_bits

def hamming_to_ascii(encoded_bits, n, k):
    corrected_bits = detect_and_correct_error(encoded_bits, n, k)

    data_bits = ""
    for i in range(n):
        if not (i + 1) & (i):
            continue  
        data_bits += corrected_bits[i]
    
    return data_bits

def main():
    while True:
        encoded_message = input("Ingrese el mensaje codificado: ")
        if is_valid_binary_string(encoded_message):
            n = int(input("Ingrese el valor de n (número total de bits): "))
            if len(encoded_message) == n:
                break
            else:
                print("El número total de bits no coincide con la longitud de la cadena ingresada.")
        else:
            print("Entrada no válida. Por favor, ingrese solo una cadena de bits (0s y 1s).")
        
    k = int(input("Ingrese el valor de k (número de bits de datos): "))

    corrected_message = hamming_to_ascii(encoded_message, n, k)
    print("Mensaje original corregido:", corrected_message)

if __name__ == "__main__":
    main()