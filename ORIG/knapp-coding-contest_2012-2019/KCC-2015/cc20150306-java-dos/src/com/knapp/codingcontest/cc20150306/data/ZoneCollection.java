/* -*- java -*- ************************************************************************** *
 *
 *            Copyright (C) Knapp Logistik Automation GmbH
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.cc20150306.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds a colleciotion of all zones in the warehouse
 */
public class ZoneCollection implements Iterable<Zone> {
  private final Map<String, Zone> zones = new HashMap<String, Zone>();

  // ----------------------------------------------------------------------------

  ZoneCollection() {
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "ZoneCollection[" + zones.values() + "]";
  }

  // ----------------------------------------------------------------------------

  /**
   * Get a zone by it's name
   *
   * @param zone_ name of the zone to return
   *
   * @return the zone with the geiven name; null if it was not found
   */
  public Zone findZone(final String zone_) {
    return zones.get(zone_);
  }

  /**
   * An iterator for all zones so you can use
   * for (Zone zone: zoneCollection) {
   *   ...
   * }
   */
  @Override
  public Iterator<Zone> iterator() {
    return Collections.unmodifiableCollection(zones.values()).iterator();
  }

  // ----------------------------------------------------------------------------

  void add(final Zone zone) {
    zones.put(zone.getName(), zone);
  }
}
