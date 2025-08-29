# 🧵 Laboratorio 3 - Programación concurrente, condiciones de carrera y sincronización de hilos.

**Escuela Colombiana de Ingeniería Julio Garavito**  
**Curso:** Arquitectura de Software (ARSW)

---

## 👥 Integrantes del grupo

- Vicente Garzón Ríos
- Daniel Alejandro Díaz Camelo

---

## 📌 Descripción

.

---

## 📂 Parte I - Control de hilos con wait/notify. Productor/consumidor.

## 1. Análisis de Consumo de CPU con VisualVM

Al ejecutar el programa y analizarlo con **VisualVM**, se observa el comportamiento de los hilos que forman parte del sistema productor-consumidor:

### 🧵 Hilos en ejecución

- `Thread-0`: ejecuta `edu.eci.arst.concprg.prodcons.Producer.run()`
- `Thread-1`: ejecuta `edu.eci.arst.concprg.prodcons.Consumer.run()`

---

### 🔍 Análisis del Productor (`Thread-0`)

El hilo está en estado de espera, específicamente en la llamada:
  ```java
  java.lang.Thread.sleep(native)
  ```

Esto indica que el productor se encuentra dormido, por lo tanto:
- No realiza trabajo activo.
- Su `Total Time (CPU)` es de **0 ms**, lo que confirma que **no esta utilizando el CPU**.


### 🔍 Análisis del Consumidor (`Thread-1`)

El consumidor esta ejecutando el metodo:
  ```java
  edu.eci.arst.concprg.prodcons.Consumer.run()
  ```

En este caso, se observa lo siguiente:
- `Total time`: 76,106 ms.
- `Total time (CPU)`: 76,106 ms.

Esto implica que el hilo ha estado activo **todo el tiempo muestreado**, y **ha utilizado la CPU continuamente.** 




## 1. 
Al ejecutar el programa y utilizar visualvm para revisar el consumo de CPU, podemos ver que se estan utilizando dos hilos:
Thread-0: ejecuta edu.eci.arst.concprg.prodcons.Producer.run()
Thread-1: ejecuta edu.eci.arst.concprg.prodcons.Consumer.run()
Ambos hilos tienen un tiempo total de CPU de 76,106 ms, lo que indica que han estado activos durante todo ese tiempo. Dentro de Thread-0, se puede observar que se esta llamando a java.lang.Thread.sleep(native), lo que indica que el productor esta esperando/durmiendo (no esta consumiendo CPU), ademas de que en total time (CPU) muestra 0 ms, por lo que confirmamos que no esta usando el CPU.
Por otro lado Thread-1 esta ejecutando el metodo Consumer.run(), en donde total time (CPU) muestra 76,106 ms, al igual que en total time (es decir el 100% de tiempo de CPU), lo que quiere decir que se esta usando usando intensivamente el CPU