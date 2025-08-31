package edu.eci.arst.concprg.prodcons;

import java.util.LinkedList;

public class Queue {
  private LinkedList<Integer> items = new LinkedList<>();
  private int limit;

  public Queue(int limit) {
    this.limit = limit;
  }

  public synchronized void put(int value) throws InterruptedException {
    while (items.size() == limit) {
      wait(); // el productor espera si la cola esta llena
    }
    items.add(value);
    notifyAll(); // avisa al consumidor que hay un nuevo elemento por consumir
  }

  public synchronized int get() throws InterruptedException {
    while (items.isEmpty()) {
      wait(); // el consumidor espera si la cola esta vacia
    }

    int value = items.removeFirst();
    notifyAll(); // avisa al productor que ya hay espacio disponible
    return value;
  }
}
