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

public class OrderLine {
  // ----------------------------------------------------------------------------

  private final Order order;
  private final String productCode;
  private final int requestedQuantity;

  // ----------------------------------------------------------------------------

  public OrderLine(final Order order, final String productCode, final int requestedQuantity) {
    this.order = order;
    this.productCode = productCode;
    this.requestedQuantity = requestedQuantity;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "OrderLine[order.code=" + order.getCode() + ", productCode=" + productCode + ", requestedQuantity="
        + requestedQuantity + "]";
  }

  // ----------------------------------------------------------------------------

  public Order getOrder() {
    return order;
  }

  public String getProductCode() {
    return productCode;
  }

  public int getRequestedQuantity() {
    return requestedQuantity;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
