package edu.eci.arsf.blacklistvalidator;

public class Controller {
  private int occurrences = 0;
  private boolean running = true;

  public synchronized void stop() {
    running = false;
  }

  public synchronized void increment() {
    occurrences++;
    if (occurrences >= HostBlackListsValidator.BLACK_LIST_ALARM_COUNT) {
      stop();
    }
  }

  public synchronized boolean isRunning() {
    return running;
  }
}
