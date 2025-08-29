# 🧵 Laboratorio 3 - Programación concurrente, condiciones de carrera y sincronización de hilos.

**Escuela Colombiana de Ingeniería Julio Garavito**  
**Curso:** Arquitectura de Software (ARSW)

---

## 👥 Integrantes del grupo

- Vicente Garzón Ríos
- Daniel Alejandro Díaz Camelo

---

## 📌 Descripción

Este laboratorio aborda conceptos de **programación concurrente** en Java, enfocándose en la sincronización de hilos, el manejo del problema **productor–consumidor**, y la prevención de **deadlocks** mediante mecanismos de coordinación y suspensión segura.


---

## 📂 Parte I - Control de hilos con wait/notify. Productor/consumidor.

## 1. Análisis de Consumo de CPU con VisualVM

Al ejecutar el programa y analizarlo con **VisualVM**, se observa el comportamiento de los hilos que forman parte del sistema productor-consumidor:

### 🧵 Hilos en ejecución

- `Thread-0`: ejecuta `edu.eci.arst.concprg.prodcons.Producer.run()`
- `Thread-1`: ejecuta `edu.eci.arst.concprg.prodcons.Consumer.run()`

📷 _Evidencia (VisualVM)_

<p align="center">
  <img src="assets/img/img1.png" alt="Uso con un hilo" width="400"/>
</p>

---

### 🔍 Análisis del Productor (`Thread-0`)

El hilo está en estado de espera, específicamente en la llamada:
  ```java
  java.lang.Thread.sleep(native)
  ```

Esto indica que el productor se encuentra dormido, por lo tanto:
- No realiza trabajo activo.
- Su `Total Time (CPU)` es de **0 ms**, lo que confirma que **no esta consumiendo CPU**.


### 🔍 Análisis del Consumidor (`Thread-1`)

El consumidor está ejecutando el metodo:
  ```java
  edu.eci.arst.concprg.prodcons.Consumer.run()
  ```

Se observa lo siguiente:
- `Total time`: 76,106 ms.
- `Total time (CPU)`: 76,106 ms.

Esto implica que el hilo ha estado activo **todo el tiempo muestreado**, y que **ha utilizado la CPU continuamente.**

### ✅ Conclusión

- El alto consumo de CPU se debe al **`Consumer`**, que permanece en un bucle infinito verificando si la cola tiene elementos disponibles.
- Esta forma de ejecución produce un comportamiento de **busy waiting**, en el cual el hilo se mantiene activo aunque no tenga trabajo que realizar.
- La clase responsable del consumo de CPU es: **`edu.eci.arst.concprg.prodcons.Consumer`**.
- El problema radica en la ausencia de mecanismos de sincronización (`wait()` y `notify()`), que permitirían que el consumidor quedara bloqueado hasta que existieran elementos que procesar.