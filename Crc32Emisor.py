def is_valid_binary_string(data):
    return all(char in '01' for char in data)

def data_to_bytes(data):
    data = data.ljust((len(data) + 7) // 8 * 8, '0')
    return bytes(int(data[i:i+8], 2) for i in range(0, len(data), 8))

def crc32(data):
    polynomial = 0xEDB88320  # Polinomio estándar para CRC-32 reflejado
    crc = 0xFFFFFFFF
    
    data = data_to_bytes(data)
    
    for byte in data:
        crc ^= byte
        for _ in range(8):
            if crc & 1:
                crc = (crc >> 1) ^ polynomial
            else:
                crc >>= 1
    
    crc ^= 0xFFFFFFFF
    crc_binary = format(crc, '032b')
    return crc_binary

def main():
    while True:
        data = input("Ingrese una cadena de bits: ")
        if is_valid_binary_string(data):
            break
        else:
            print("Entrada no válida. Por favor, ingrese solo una cadena de bits (0s y 1s).")

    crc = crc32(data)
    result = data + crc

    print(f"Data: {data}")
    print(f"CRC-32: {crc}")
    print(f"Data con CRC: {result}")

if __name__ == "__main__":
    main()
