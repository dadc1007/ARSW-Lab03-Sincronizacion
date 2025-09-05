package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImmortalsController {
  private final CopyOnWriteArrayList<Immortal> immortals;
  private final Object lock = new Object();
  private boolean isPaused;

  public ImmortalsController(List<Immortal> immortals) {
    this.immortals = new CopyOnWriteArrayList<>(immortals);
  }

  public void pauseAll() {
    isPaused = true;
  }

  public void resumeAll() {
    synchronized (lock) {
      isPaused = false;
      lock.notifyAll();
    }
  }

  public int getTotalHealth() {
    int health = 0;

    for (Immortal immortal : immortals) {
      health += immortal.getHealth();
    }

    return health;
  }

  public CopyOnWriteArrayList<Immortal> getImmortals() {
    return immortals;
  }

  public boolean isPaused() {
    return isPaused;
  }

  public Object getLock() {
    return lock;
  }
}
