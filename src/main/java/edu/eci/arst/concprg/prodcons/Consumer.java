/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

/**
 * @author hcadavid
 */
public class Consumer extends Thread {
  private Queue queue;

  public Consumer(Queue queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    try {
      while (true) {
        int elem = queue.get();
        System.out.println("Consumer consumes " + elem);
      }
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
