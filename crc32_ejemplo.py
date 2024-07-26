import binascii

def bitstring_to_bytes(bitstring):
    """
    Convierte una cadena de bits en una secuencia de bytes.
    
    :param bitstring: Cadena de bits (ejemplo: "1101101")
    :return: Secuencia de bytes correspondiente.
    """
    # Añadir ceros al inicio para que la longitud sea múltiplo de 8
    bitstring = bitstring.zfill(len(bitstring) + (8 - len(bitstring) % 8) % 8)
    return bytes(int(bitstring[i:i+8], 2) for i in range(0, len(bitstring), 8))

def crc32_from_bitstring(bitstring):
    """
    Calcula el CRC-32 para una cadena de bits.
    
    :param bitstring: Cadena de bits (ejemplo: "1101101")
    :return: Valor CRC-32 calculado en formato binario.
    """
    # Convertir la cadena de bits a bytes
    data = bitstring_to_bytes(bitstring)
    # Calcular el CRC-32
    crc_value = binascii.crc32(data) & 0xFFFFFFFF
    # Convertir el valor CRC-32 a una cadena binaria
    crc_binary = format(crc_value, '032b')
    return crc_binary

def main():
    # Ejemplo de cadena de bits
    bitstring = "1101101"
    
    # Calcular el CRC-32
    crc_binary = crc32_from_bitstring(bitstring)
    
    print(f"Cadena de bits: {bitstring}")
    print(f"CRC-32 en bits: {crc_binary}")

if __name__ == "__main__":
    main()
