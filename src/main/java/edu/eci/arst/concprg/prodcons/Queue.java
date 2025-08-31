package edu.eci.arst.concprg.prodcons;

import java.util.LinkedList;

public class Queue {
  private LinkedList<Integer> items = new LinkedList<>();

  public synchronized void put(int value) {
    items.add(value);
    notifyAll();
  }

  public synchronized int get() throws InterruptedException {
    while (items.isEmpty()) {
      wait();
    }

    return items.removeFirst();
  }
}
