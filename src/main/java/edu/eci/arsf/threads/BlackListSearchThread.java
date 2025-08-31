package edu.eci.arsf.threads;

import edu.eci.arsf.blacklistvalidator.Controller;
import edu.eci.arsf.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;

public class BlackListSearchThread extends Thread {
  private int startIndex;
  private int endIndex;
  private String ipadress;
  private HostBlacklistsDataSourceFacade skds;
  LinkedList<Integer> blackListOccurrences;
  private int checkedCount;
  private Controller controller;

  public BlackListSearchThread(
      int startIndex,
      int endIndex,
      String ipadress,
      HostBlacklistsDataSourceFacade skds,
      Controller controller) {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.ipadress = ipadress;
    this.skds = skds;
    this.blackListOccurrences = new LinkedList<>();
    this.controller = controller;
  }

  @Override
  public void run() {
    for (int i = startIndex; i < endIndex && controller.isRunning(); i++) {
      checkedCount++;

      if (skds.isInBlackListServer(i, ipadress)) {
        controller.increment();
        blackListOccurrences.add(i);
        System.out.println("IP found in list: " + i);
      }
    }
  }

  public LinkedList<Integer> getBlackListOccurrences() {
    return blackListOccurrences;
  }

  public int getCheckedCount() {
    return checkedCount;
  }
}
