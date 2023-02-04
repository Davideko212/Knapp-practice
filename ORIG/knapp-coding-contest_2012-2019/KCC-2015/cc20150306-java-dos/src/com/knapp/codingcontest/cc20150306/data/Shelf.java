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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A shelf that holds a number of locations
 */
public class Shelf {

  private final String code;
  private final Map<String, Location> locations = new HashMap<String, Location>();

  // ----------------------------------------------------------------------------

  Shelf(final String code) {
    this.code = code;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return the unique code for this shelf
   */
  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return "Shelf[" + code + ": " + locations + "]\n";
  }

  // ----------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return code.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    final Shelf other = (Shelf) obj;
    return code.equals(other.code);
  }

  // ----------------------------------------------------------------------------

  /**
   * @return all locations of this shelf
   */
  public Collection<Location> getLocations() {
    return Collections.unmodifiableCollection(locations.values());
  }

  /**
   * Get the next free location in this shelf
   *
   * @return the next free location in this shelf; null if none could be found
   */
  public Location findNextFreeLocation() {
    for (final Location location : getLocations()) {
      if (location.getProduct() == null) {
        return location;
      }
    }
    return null;
  }

  // ----------------------------------------------------------------------------

  void add(final Location location) {
    locations.put(location.getCode(), location);
  }
}
