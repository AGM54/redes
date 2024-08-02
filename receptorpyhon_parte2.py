import socket
import matplotlib.pyplot as plt
import json

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

def calculate_parity(bits, positions):
    parity = 0
    for position in positions:
        parity ^= int(bits[position - 1])
    return parity

def detect_and_correct_hamming(encoded_bits, n, k):
    r = n - k
    error_position = 0
    for i in range(r):
        position_parity = 2 ** i
        parity = 0
        for j in range(1, n + 1):
            if j & position_parity:
                parity ^= int(encoded_bits[j - 1])
        error_position += parity * position_parity

    if error_position != 0:
        error_position -= 1
        corrected_bits = list(encoded_bits)
        corrected_bits[error_position] = '1' if corrected_bits[error_position] == '0' else '0'
        encoded_bits = ''.join(corrected_bits)
        print(f"Error detectado y corregido en la posición: {error_position + 1}")
    else:
        print("No se detectó ningún error.")

    data_bits = ""
    for i in range(n):
        if not (i + 1) & (i):
            continue
        data_bits += encoded_bits[i]
    return data_bits

def verify_crc(message_with_crc):
    message = message_with_crc[1:-32]
    crc_received = int(message_with_crc[-32:], 2)
    message_bytes = binary_string_to_bytes(message)
    crc_calculated = calculate_crc32(message_bytes)
    return crc_received == crc_calculated

def start_server(port):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('0.0.0.0', port))
    server_socket.listen(1)
    print("Servidor escuchando en el puerto", port)

    results = {"crc32": {"success": 0, "failure": 0}, "hamming": {"success": 0, "failure": 0}}

    pruebas_realizadas = 0

    client_socket, addr = server_socket.accept()
    print("Conexión aceptada de", addr)
    data = client_socket.recv(1024).decode()
    print("Datos recibidos:", data)

    # Parsear JSON recibido
    try:
        json_data = json.loads(data)
        algorithm = json_data['Algorithm']
        message_with_integrity = json_data['Message']
        num_pruebas = json_data['Tests']
    except json.JSONDecodeError as e:
        print(f"Error al decodificar JSON: {e}")
        client_socket.close()

    while pruebas_realizadas < num_pruebas:
        if algorithm == '1':
            if verify_crc(message_with_integrity):
                print("CRC-32 verificado correctamente.")
                results["crc32"]["success"] += 1
            else:
                print("Error de CRC-32 detectado.")
                results["crc32"]["failure"] += 1
        elif algorithm == '2':
            n = len(message_with_integrity)
            k = n - (len(bin(n).lstrip('0b')) - 1)
            corrected_message = detect_and_correct_hamming(message_with_integrity, n, k)
            print("Mensaje corregido:", corrected_message)
            if corrected_message:
                results["hamming"]["success"] += 1
            else:
                results["hamming"]["failure"] += 1
        else:
            print("Algoritmo no soportado.")

        client_socket.close()
        pruebas_realizadas += 1
    
    print("Resultados:", results)
    return results

if __name__ == "__main__":
    num_pruebas = 10000
    results = start_server(12345)

    # Generar gráficos de los resultados
    labels = ['CRC-32', 'Hamming']
    success = [results["crc32"]["success"], results["hamming"]["success"]]
    failure = [results["crc32"]["failure"], results["hamming"]["failure"]]

    x = range(len(labels))

    plt.bar(x, success, width=0.4, label='Éxitos', color='b', align='center')
    plt.bar(x, failure, width=0.4, label='Fracasos', color='r', align='edge')

    plt.xlabel('Algoritmo')
    plt.ylabel('Número de Pruebas')
    plt.title('Resultados de Pruebas de Detección y Corrección de Errores')
    plt.xticks(x, labels)
    plt.legend()

    # Guardar la gráfica como imagen
    plt.savefig('resultados_pruebas.png')

    # Mostrar la gráfica
    plt.show()
