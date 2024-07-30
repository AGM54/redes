def string_to_bin(s):
    return ''.join(format(ord(char), '08b') for char in s)

def binary_string_to_bytes(bin_str):
    byte_length = (len(bin_str) + 7) // 8
    result = bytearray(byte_length)
    for i in range(len(bin_str)):
        if bin_str[i] == '1':
            result[i // 8] |= 1 << (7 - (i % 8))
    return result

def calculate_crc32(data):
    crc = 0xFFFFFFFF
    for byte in data:
        crc ^= byte
        for _ in range(8):
            if crc & 1:
                crc = (crc >> 1) ^ 0xEDB88320
            else:
                crc >>= 1
    return crc ^ 0xFFFFFFFF

def main_emisor():
    mensaje_str = input("Ingrese un mensaje de texto: ")
    bin_message = string_to_bin(mensaje_str)
    message_bytes = binary_string_to_bytes(bin_message)

    crc = calculate_crc32(message_bytes)
    crc_bin = format(crc, '032b')
    
    message_with_crc = bin_message + crc_bin

    print("Mensaje original: ", mensaje_str)
    print("Mensaje en binario: ", bin_message)
    print("CRC-32: ", crc_bin)
    print("Mensaje en binario con CRC: ", message_with_crc)

if __name__ == "__main__":
    main_emisor()
