/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsf.blacklistvalidator;

import edu.eci.arsf.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import edu.eci.arsf.threads.BlackListSearchThread;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hcadavid
 */
public class HostBlackListsValidator {

  public static final int BLACK_LIST_ALARM_COUNT = 5;

  /**
   * Check the given host's IP address in all the available black lists, and report it as NOT
   * Trustworthy when such IP was reported in at least BLACK_LIST_ALARM_COUNT lists, or as
   * Trustworthy in any other case. The search is not exhaustive: When the number of occurrences is
   * equal to BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as NOT Trustworthy,
   * and the list of the five blacklists returned.
   *
   * @param ipaddress suspicious host's IP address.
   * @return Blacklists numbers where the given host's IP address was found.
   */
  public List<Integer> checkHost(String ipaddress, int N, Controller controller) {
    HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    int numberServers = skds.getRegisteredServersCount();
    int segmentSize = numberServers / N;
    List<BlackListSearchThread> threads = new ArrayList<>();

    int startIndex = 0;
    for (int i = 0; i < N; i++) {
      int endIndex = (i == N - 1) ? numberServers : startIndex + segmentSize;

      BlackListSearchThread t = new BlackListSearchThread(startIndex, endIndex, ipaddress, skds, controller);
      threads.add(t);
      startIndex = endIndex;
    }

    for (BlackListSearchThread t : threads) {
      t.start();
    }

    for (BlackListSearchThread t : threads) {
      try {
        t.join();
      } catch (InterruptedException ex) {
        LOG.log(Level.WARNING, "Interrupted during blacklist search thread", ex);
      }
    }

    LinkedList<Integer> blackListOccurrences = new LinkedList<>();
    int checkedCount = 0;
    for (BlackListSearchThread t : threads) {
      blackListOccurrences.addAll(t.getBlackListOccurrences());
      checkedCount += t.getCheckedCount();
    }

    if (blackListOccurrences.size() >= BLACK_LIST_ALARM_COUNT) {
      skds.reportAsNotTrustworthy(ipaddress);
    } else {
      skds.reportAsTrustworthy(ipaddress);
    }

    LOG.log(
        Level.INFO,
        "Checked Black Lists:{0} of {1}",
        new Object[] {checkedCount, numberServers});

    return blackListOccurrences;
  }

  private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
}
