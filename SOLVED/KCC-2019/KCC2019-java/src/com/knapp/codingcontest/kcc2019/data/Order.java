/* -*- java -*- ************************************************************************** *
 *
 *                     Copyright (C) KNAPP AG
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.kcc2019.data;

import java.util.ArrayList;
import java.util.List;

public class Order {
  // ----------------------------------------------------------------------------

  private final String code;
  private final int routeDeparture;
  private final List<OrderLine> orderLines = new ArrayList<OrderLine>();

  // ----------------------------------------------------------------------------

  public Order(final String code, final int routeDeparture) {
    this.code = code;
    this.routeDeparture = routeDeparture;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Order#" + code + "[routeDeparture=" + routeDeparture + ", orderLines=" + orderLines + "]";
  }

  // ----------------------------------------------------------------------------

  public String getCode() {
    return code;
  }

  public int getRouteDeparture() {
    return routeDeparture;
  }

  public List<OrderLine> getOrderLines() {
    return orderLines;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
