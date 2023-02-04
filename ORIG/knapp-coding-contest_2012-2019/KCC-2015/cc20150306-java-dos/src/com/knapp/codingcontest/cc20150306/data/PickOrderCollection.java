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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class PickOrderCollection implements Iterable<PickOrder> {
  private final Collection<PickOrder> orders = new ArrayList<PickOrder>();

  // ----------------------------------------------------------------------------

  PickOrderCollection() {
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "PickOrderCollection[" + orders + "]";
  }

  // ----------------------------------------------------------------------------

  /**
   * Get an iterator for all PickOrders
   */
  @Override
  public Iterator<PickOrder> iterator() {
    return Collections.unmodifiableCollection(orders).iterator();
  }

  // ----------------------------------------------------------------------------

  void add(final PickOrder order) {
    orders.add(order);
  }
}