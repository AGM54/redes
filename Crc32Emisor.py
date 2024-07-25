POLYNOMY = '100000100110000010001110110110111'

def is_valid_binary_string(data):
    return all(char in '01' for char in data)

def crc32(data, poly = POLYNOMY):
    # Convierte los datos a binario y los extiende con 32 ceros
    data = data + '0' * 32
    data = list(data)
    poly = list(poly)

    # Realiza la división binaria
    for i in range(len(data) - 32):
        if data[i] == '1':
            for j in range(len(poly)):
                data[i + j] = str(int(data[i + j]) ^ int(poly[j]))

    # El residuo es el CRC
    crc = ''.join(data[-32:])
    return crc

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
