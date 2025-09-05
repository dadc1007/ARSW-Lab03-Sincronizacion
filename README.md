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

### 📊 Consumo general de CPU

- El programa presentó un consumo promedio de **~13% de CPU** durante la ejecución.
- Este uso corresponde casi en su totalidad al **hilo consumidor**, ya que el productor pasa la mayor parte del tiempo dormido.

📷 _Evidencia (VisualVM)_

<p align="center">
  <img src="assets/img/img1.png" alt="Uso con un hilo" width="400"/>
</p>

### 🧵 Hilos en ejecución

- `Thread-0`: ejecuta `edu.eci.arst.concprg.prodcons.Producer.run()`
- `Thread-1`: ejecuta `edu.eci.arst.concprg.prodcons.Consumer.run()`

📷 _Evidencia (VisualVM)_

<p align="center">
  <img src="assets/img/img2.png" alt="Uso con un hilo" width="400"/>
</p>

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

- El consumo total de CPU fue de **13%**, atribuible casi por completo al hilo **`Consumer`**.
- El alto consumo de CPU se debe al **`Consumer`**, que permanece en un bucle infinito verificando si la cola tiene elementos disponibles.
- Esta forma de ejecución produce un comportamiento de **busy waiting**, en el cual el hilo se mantiene activo aunque no tenga trabajo que realizar.
- La clase responsable del consumo de CPU es: **`edu.eci.arst.concprg.prodcons.Consumer`**.
- El problema radica en la ausencia de mecanismos de sincronización (`wait()` y `notify()`), que permitirían que el consumidor quedara bloqueado hasta que existieran elementos que procesar.

## 2. Ajuste con `wait()` y `notify()`

Se realizaron ajustes en la cola compartida (`Queue`) incorporando los métodos **wait()** y **notifyAll()** para mejorar la eficiencia en el uso de CPU.

Con esta modificación, cuando el consumidor intenta obtener un elemento y la cola está vacía:

- El hilo entra en estado de **espera bloqueante (`WAITING`)** mediante `wait()`.
- Se elimina el ciclo de consulta continua, evitando el **busy waiting**.

Cuando el productor inserta un nuevo elemento en la cola, se llama a `notifyAll()`, lo que **despierta al consumidor** para que procese el dato.

### 📊 Consumo general de CPU

- El consumo promedio de CPU se redujo drásticamente en comparación con el punto 1, pasando de ~13% a **~0,3%**.
- El consumidor ya **no mantiene ocupado el procesador** innecesariamente: solo se activa cuando hay datos para consumir.
- Esto confirma que la solución ahora aprovecha la CPU de manera **más eficiente**.

📷 _Evidencia (VisualVM)_

<p align="center">
  <img src="assets/img/img3.png" alt="Consumo tras ajuste" width="400"/>
</p>

### 🧵 Hilos en ejecución

- `Thread-0`: ejecuta `edu.eci.arst.concprg.prodcons.Producer.run()`
- `Thread-1`: ejecuta `edu.eci.arst.concprg.prodcons.Consumer.run()`

📷 _Evidencia (VisualVM)_

<p align="center">
  <img src="assets/img/img4.png" alt="Sampler tras ajuste" width="400"/>
</p>

### 🔍 Análisis del Productor (`Thread-0`)

El productor mantiene su comportamiento original:

- Genera un nuevo dato cada segundo (**`Thread.sleep(1000)`**).
- Su consumo de CPU es **prácticamente nulo** ya que pasa la mayor parte del tiempo dormido.

### 🔍 Análisis del Consumidor (`Thread-1`)

El consumidor está ejecutando los metodos:

```java
edu.eci.arst.concprg.prodcons.Queue.get()
java.lang.Object.wait()
```

Esto confirma que:

- El consumidor ya **no ejecuta un bucle activo** revisando la cola.
- Permanece en espera hasta recibir notificación del productor.
- Su consumo de CPU es **cercano a 0%** mientras está bloqueado.

### ✅ Conclusión

- El **busy waiting** fue eliminado por completo.
- La sincronización con **wait() y notifyAll()** asegura que los hilos solo consuman CPU cuando realmente hay trabajo que realizar.
- El consumo de CPU disminuyó de manera significativa (**~13% → ~0,3%**).
- El sistema ahora logra un **uso mucho más eficiente de los recursos**.

## 3. Ajuste con límite de stock en la cola

En este punto, se configuró la **cola (Queue)** con un **límite máximo de elementos**.  
De hecho, nuestra cola puede verse como una **implementación manual de `LinkedBlockingQueue`**, ya que reproduce su comportamiento básico de bloqueo y sincronización entre productores y consumidores.

De esta manera:

- El **productor** genera números muy rápido, pero si la cola llega al límite de stock, queda bloqueado en la instrucción `wait()` dentro de `put()`.
- El **consumidor** procesa más lento (**1 segundo por elemento**).
- Cada vez que el consumidor libera un espacio con `get()`, se ejecuta `notifyAll()`, despertando al productor para que pueda continuar.

### 📊 Consumo general de CPU

- El consumo promedio de CPU sigue siendo **muy bajo (≈0,3%)**.
- Al igual que en el **punto 2**, los hilos entran en estado de espera (**WAITING**) cuando no pueden avanzar.
- La diferencia principal respecto a **LinkedBlockingQueue** radica en el mecanismo de notificación.  
  En nuestra implementación manual, la cola utiliza `notifyAll()`, lo que provoca que **todos los hilos bloqueados se despierten**, aunque finalmente **solo uno pueda continuar**. Esto genera **ligeros picos de CPU** por los cambios de contexto innecesarios.
- En contraste, **LinkedBlockingQueue** implementa esta misma lógica de forma más eficiente, ya que utiliza notificaciones más finas y despierta únicamente al **hilo necesario**.

📷 _Evidencia (VisualVM)_

<p align="center">
  <img src="assets/img/img5.png" alt="Consumo tras ajuste" width="400"/>
</p>

### 🧵 Hilos en ejecución

- **Thread-0**: ejecuta `edu.eci.arst.concprg.prodcons.Producer.run()`
- **Thread-1**: ejecuta `edu.eci.arst.concprg.prodcons.Consumer.run()`

📷 _Evidencia (VisualVM)_

<p align="center">  
  <img src="assets/img/img6.png" alt="Sampler con límite de stock" width="400"/>  
</p>

### 🔍 Análisis del Productor (Thread-0)

- Produce números de forma **muy rápida**.
- Cuando la cola está llena (`items.size() == limit`), queda bloqueado en la llamada a `wait()` dentro de `put()`.
- Solo se despierta cuando el consumidor libera espacio.

### 🔍 Análisis del Consumidor (Thread-1)

- Consume elementos a un ritmo **más lento** (`Thread.sleep(1000)`).
- Mientras tanto, mantiene bloqueado al productor si la cola está llena.
- El uso de CPU es **bajo** porque no ejecuta bucles activos, sino que espera de forma bloqueante.

### ✅ Conclusión

- El **límite de stock** garantiza que la cola no crezca indefinidamente.
- No se presentan errores ni alto consumo de CPU incluso con un stock pequeño.
- La sincronización con **wait() y notifyAll()** cumple el objetivo, aunque el consumo es **ligeramente mayor** que con `LinkedBlockingQueue`.
- Nuestra implementación puede entenderse como una versión **manual y didáctica** de `LinkedBlockingQueue`, mientras que esta última resulta más eficiente en entornos reales al usar **locks más finos** y **notificaciones precisas**.

## 📂 Parte III – Sincronización y Dead-Locks (Highlander Simulator)

### 1. Revisión del programa

El juego **Highlander Simulator** simula la pelea entre N inmortales:

- Cada inmortal ataca constantemente a otro, restándole M puntos de vida y ganando los mismos puntos.
- La suma total de puntos de vida de todos los inmortales debería mantenerse constante en el tiempo (**invariante**).
- El juego rara vez termina con un único ganador; lo normal es que queden dos peleando indefinidamente.

### 2. Invariante del sistema

Si cada inmortal empieza con 100 puntos de vida y hay **N jugadores**, entonces el valor total debería ser:

Vida_Total = N × 100

Al usar la opción **Pause and Check**:

- Observamos que este valor no siempre se mantenía, lo que evidenció una **condición de carrera** en el acceso a la lista de inmortales.

### 3. Corrección con pausa y reanudación

Implementamos la opción **Pause** deteniendo a todos los hilos antes de calcular la sumatoria de puntos de vida.

- Usamos un monitor compartido y una bandera `isPaused`.
- Implementamos también la opción **Resume** para continuar la simulación.

✅ Ahora el invariante se cumple correctamente cuando pausamos y verificamos.

### 4. Regiones críticas y estrategia de bloqueo

Identificamos como regiones críticas:

- La actualización simultánea de la vida de dos inmortales durante una pelea.

Para evitar condiciones de carrera:

- Usamos **bloques sincronizados anidados** al actualizar los valores de vida.
- Esto garantizó consistencia en los combates.

### 5. Deadlocks y corrección

En pruebas con muchos inmortales, algunos hilos quedaron bloqueados.

- Usamos **jps** y **jstack** para analizar, confirmando un **deadlock** por adquisición circular de locks.
- La estrategia de corrección fue **ordenar el locking siempre en el mismo orden (por ID del inmortal)** para evitar bloqueos circulares.

✅ Con esta mejora, la simulación no se detuvo más.

### 6. Eliminación de inmortales muertos

Problema:

- Los inmortales vivos intentaban pelear con muertos → combates inútiles.
- Si eliminábamos directamente de la lista con `synchronized`, la simulación se volvía lenta.

Solución:

- Usamos **colecciones concurrentes** (`CopyOnWriteArrayList` / `ConcurrentLinkedQueue`).
- Estas permiten eliminar a los inmortales muertos sin necesidad de sincronización explícita.

✅ La simulación escala correctamente incluso con 1000 o 10000 inmortales.

### 7. Manejo de errores encontrados

- Con índices aleatorios (`Random.nextInt(size)`), a veces ocurrían errores `IllegalArgumentException: bound must be positive` cuando la lista quedaba vacía.
- Se corrigió validando el tamaño de la lista antes de elegir un oponente.

### 8. Opción STOP

Finalmente, implementamos el botón **STOP** para terminar con el programa de manera definitiva.

---

## 📌 Conclusiones generales

- El uso de **wait/notify** permitió eliminar el **busy waiting**, reduciendo el consumo de CPU de ~13% a ~0.3%.
- En simulaciones de muchos hilos, la sincronización con **locks explícitos** puede llevar a **deadlocks**; la clave es **ordenar la adquisición de locks**.
- Las **colecciones concurrentes** son una herramienta más eficiente que `synchronized` para compartir estructuras de datos entre hilos sin volver el programa secuencial.
- El invariante del sistema (suma de puntos de vida) se mantiene correctamente solo cuando se evita la condición de carrera con una pausa global.
- La escalabilidad del sistema depende directamente de las técnicas de concurrencia empleadas.

---
