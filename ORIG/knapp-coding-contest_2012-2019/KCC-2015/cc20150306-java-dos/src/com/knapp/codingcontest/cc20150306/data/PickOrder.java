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
import java.util.Collections;
import java.util.List;

/**
 * A PickOrder (Kommissionierauftrag) which should be packed in the warehouse
 * with the current assignment of products
 */
public class PickOrder {
  private final String orderId;
  private final List<PickOrderLine> lines = new ArrayList<PickOrderLine>();

  // ----------------------------------------------------------------------------

  protected PickOrder(final String orderId) {
    this.orderId = orderId;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return the unique Id for this order
   */
  public String getOrderId() {
    return orderId;
  }

  /**
   * @return the PickOrderLines in this order
   */
  public List<PickOrderLine> getLines() {
    return Collections.unmodifiableList(lines);
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "PickOrder[" + orderId + ":" + lines + "]\n";
  }

  // ----------------------------------------------------------------------------

  protected void addLine(final PickOrderLine line) {
    lines.add(line);
  }
}
