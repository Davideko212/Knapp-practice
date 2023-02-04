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
 * A specific zone within a warehouse that provides specific storage conditions
 */
public class Zone {
  private final String name;
  private final boolean cool;
  private final boolean frozen;
  private final boolean valuable;
  private final int costPerShelf;
  private final int costPerLocation;

  private final Map<String, Shelf> shelfs = new HashMap<String, Shelf>();

  // ----------------------------------------------------------------------------

  protected Zone(final String name, final boolean cool, final boolean frozen, final boolean valuable, final int costPerShelf,
      final int costPerLocation) {
    this.name = name;
    this.cool = cool;
    this.frozen = frozen;
    this.valuable = valuable;
    this.costPerShelf = costPerShelf;
    this.costPerLocation = costPerLocation;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Zone[" + name + ":, cool=" + cool + ", frozen=" + frozen + ", valuable=" + valuable + ", costPerShelf="
        + costPerShelf + ", costPerLocation=" + costPerLocation + ",\n  shelfs=" + shelfs.values() + "]\n";
  }

  // ----------------------------------------------------------------------------

  /**
   * @return Uniqhe name of the zone
   */
  public String getName() {
    return name;
  }

  /**
   * @return Does this zone provide cool storage?
   */
  public boolean providesCool() {
    return cool;
  }

  /**
   * @return Does this zone provide frozen storage
   */
  public boolean providesFrozen() {
    return frozen;
  }

  /**
   * @return Does this zone provide secure storage for valuable items
   */
  public boolean providesValuable() {
    return valuable;
  }

  /**
   * @return The cost for using a shelf to pack an order (once per order)
   */
  public int getCostPerShelf() {
    return costPerShelf;
  }

  /**
   * @return The cost for retrieving a product from a location of a shelf
   */
  public int getCostPerLocation() {
    return costPerLocation;
  }

  /**
   * @return all shelfs in this zone
   */
  public Collection<Shelf> getShelfs() {
    return Collections.unmodifiableCollection(shelfs.values());
  }

  /**
   * Get a shelf by it's name
   *
   * @param shelf_ name of the shelf to return
   *
   * @return the shelf with the geiven name; null if it was not found
   */
  public Shelf findShelf(final String shelf_) {
    return shelfs.get(shelf_);
  }

  /**
   * Gets the next free location in this zone.
   * ==> Hint: This is a straight forward implementation and may not yield the best result!!
   */
  public Location findNextFreeLocation() {
    for (final Shelf shelf : getShelfs()) {
      final Location location = shelf.findNextFreeLocation();
      if (location != null) {
        return location;
      }
    }
    return null;
  }

  // ----------------------------------------------------------------------------

  void addLocation(final String shelf_, final String location_) {
    if (!shelfs.containsKey(shelf_)) {
      shelfs.put(shelf_, new Shelf(shelf_));
    }
    shelfs.get(shelf_).add(new Location(location_));
  }
}