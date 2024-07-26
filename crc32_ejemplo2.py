import binascii

def bitstring_to_bytes(bitstring):
    """
    Convierte una cadena de bits en una secuencia de bytes.
    
    :param bitstring: Cadena de bits (ejemplo: "1101101")
    :return: Secuencia de bytes correspondiente.
    """
    bitstring = bitstring.ljust((len(bitstring) + 7) // 8 * 8, '0')
    return bytes(int(bitstring[i:i+8], 2) for i in range(0, len(bitstring), 8))

def crc32_manual(bitstring):
    """
    Calcula el CRC-32 para una cadena de bits sin usar librerías externas.
    
    :param bitstring: Cadena de bits (ejemplo: "1101101")
    :return: Valor CRC-32 calculado en formato binario.
    """
    polynomial = 0xEDB88320  # Polinomio estándar para CRC-32 reflejado
    crc = 0xFFFFFFFF
    
    data = bitstring_to_bytes(bitstring)
    
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

def crc32_binascii(bitstring):
    """
    Calcula el CRC-32 usando la librería binascii para comparar.
    
    :param bitstring: Cadena de bits (ejemplo: "1101101")
    :return: Valor CRC-32 calculado en formato binario.
    """
    data = bitstring_to_bytes(bitstring)
    crc_value = binascii.crc32(data) & 0xFFFFFFFF
    crc_binary = format(crc_value, '032b')
    return crc_binary

def main():
    # Ejemplo de cadena de bits
    bitstring = "0110101"
    
    # Calcular el CRC-32 usando implementación manual
    crc_binary_manual = crc32_manual(bitstring)
    
    # Calcular el CRC-32 usando binascii para comparar
    crc_binary_binascii = crc32_binascii(bitstring)
    
    print(f"Cadena de bits: {bitstring}")
    print(f"CRC-32 en bits (manual): {crc_binary_manual}")
    print(f"CRC-32 en bits (binascii): {crc_binary_binascii}")

if __name__ == "__main__":
    main()

# 01111111011001010110011111001011
# 01111111011001010110011111001011