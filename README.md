# Proyecto de Comunicación de Datos con Detección y Corrección de Errores

Este proyecto implementa un sistema de comunicación de datos con algoritmos de detección y corrección de errores, utilizando un emisor en Python y un receptor en Java. Se han empleado los algoritmos CRC-32 y Hamming para asegurar la integridad de los datos transmitidos.

## Emisor

El emisor está implementado en Python y realiza las siguientes funciones:

1. **Conversión de Texto a Binario**: Convierte el mensaje de texto ingresado en una cadena binaria.
2. **Aplicación de Algoritmos de Integridad**:
   - **CRC-32**: Calcula y añade un código CRC-32 al final del mensaje binario para detección de errores.
   - **Hamming**: Aplica el código Hamming para corrección de errores.
3. **Introducción de Ruido**: Simula errores en la transmisión al alterar bits del mensaje según una probabilidad especificada.
4. **Envío del Mensaje**: Envía el mensaje con ruido al receptor utilizando sockets TCP.
5. **Registro de Resultados**: Guarda los resultados de las pruebas en un archivo CSV, incluyendo detalles como el mensaje original, mensaje con integridad, mensaje con ruido, y si se detectaron errores.

## Receptor

El receptor está implementado en Java y realiza las siguientes funciones:

1. **Recepción del Mensaje**: Recibe el mensaje enviado por el emisor utilizando sockets TCP.
2. **Identificación del Algoritmo**: Determina el algoritmo de integridad aplicado (CRC-32 o Hamming) basado en un prefijo en el mensaje.
3. **Verificación y Corrección de Errores**:
   - **CRC-32**: Verifica la integridad del mensaje utilizando el código CRC-32 y detecta errores.
   - **Hamming**: Detecta y corrige errores en el mensaje utilizando el código Hamming.
4. **Respuesta al Emisor**: Envía una respuesta al emisor indicando si se detectaron o corrigieron errores en el mensaje recibido.
5. **Registro de Resultados**: Opcionalmente, puede registrar los resultados de la verificación y corrección de errores.

## Ejecución del Proyecto

### Emisor

1. Asegúrese de tener Python instalado.
2. Ejecute el script del emisor en Python:
   ```sh
   python emisor.py
