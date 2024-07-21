def detectar_corregir(codigo_hamming):
    n = len(codigo_hamming)
    r = 0

    while (2**r) < n:
        r += 1

    posicion_error = 0

    for i in range(r):
        posicion_paridad = 2**i
        paridad = 0

        for j in range(1, n+1):
            if j & posicion_paridad:
                paridad ^= int(codigo_hamming[j-1])
        posicion_error += paridad * posicion_paridad

    if posicion_error:
        posicion_error -= 1
        codigo_corregido = list(codigo_hamming)
        codigo_corregido[posicion_error] = '1' if codigo_corregido[posicion_error] == '0' else '0'
        codigo_hamming = ''.join(codigo_corregido)

    return codigo_hamming, posicion_error + 1 if posicion_error else None


# Simulación de error introducido
codigo_recibido = input('Ingrese el código recibido: ')
print(f'Código recibido con error: {codigo_recibido}')

codigo_corregido, posicion_error = detectar_corregir(codigo_recibido)
print(f'Código corregido: {codigo_corregido}')
print(f'Posición del error: {posicion_error}' if posicion_error else 'No se detectó ningún error.')