package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

  private ImmortalUpdateReportCallback updateCallback = null;
  private int health;
  private int defaultDamageValue;
  private final List<Immortal> immortalsPopulation;
  private final String name;
  private final Random r = new Random(System.currentTimeMillis());
  ImmortalsController immortalsController;

  public Immortal(
      String name,
      List<Immortal> immortalsPopulation,
      int health,
      int defaultDamageValue,
      ImmortalUpdateReportCallback ucb) {
    super(name);
    this.updateCallback = ucb;
    this.name = name;
    this.immortalsPopulation = immortalsPopulation;
    this.health = health;
    this.defaultDamageValue = defaultDamageValue;
  }

  public void run() {
    while (health > 0) {
      if (immortalsController.isPaused()) {
        synchronized (immortalsController.getLock()) {
          try {
            while (immortalsController.isPaused()) {
              immortalsController.getLock().wait();
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      }

      // No hay nadie más con quien pelear
      if (immortalsPopulation.size() <= 1) {
        break;
      }

      Immortal[] snapshot = immortalsPopulation.toArray(new Immortal[0]);
      if (snapshot.length <= 1) {
        break;
      }

      Immortal im;
      int myIndex = -1;
      for (int i = 0; i < snapshot.length; i++) {
        if (snapshot[i] == this) {
          myIndex = i;
          break;
        }
      }

      int nextFighterIndex = r.nextInt(snapshot.length);
      if (nextFighterIndex == myIndex) {
        nextFighterIndex = (nextFighterIndex + 1) % snapshot.length;
      }

      im = snapshot[nextFighterIndex];
      this.fight(im);

      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void fight(Immortal i2) {
    Immortal first, second;
    if (this.name.compareTo(i2.name) < 0) {
      first = this;
      second = i2;
    } else {
      first = i2;
      second = this;
    }

    synchronized (first) {
      synchronized (second) {
        if (i2.getHealth() > 0) {
          i2.changeHealth(i2.getHealth() - defaultDamageValue);
          this.health += defaultDamageValue;
          updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

          if (i2.getHealth() <= 0) {
            immortalsPopulation.remove(i2);
          }

        } else {
          updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }
      }
    }
  }

  public void changeHealth(int v) {
    health = v;
  }

  public int getHealth() {
    return health;
  }

  public void setImmortalsController(ImmortalsController immortalsController) {
    this.immortalsController = immortalsController;
  }

  @Override
  public String toString() {

    return name + "[" + health + "]";
  }
}
