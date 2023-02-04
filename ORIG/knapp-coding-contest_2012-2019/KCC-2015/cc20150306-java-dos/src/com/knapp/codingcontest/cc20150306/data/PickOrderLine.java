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

/**
 * Represents a product that must be packed for a PickOrder
 */
public class PickOrderLine {
  private final String productCode;

  // ----------------------------------------------------------------------------

  PickOrderLine(final String productCode) {
    this.productCode = productCode;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return The code of the product to pack
   */
  public String getProductCode() {
    return productCode;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "PickOrderLine[" + productCode + "]";
  }

  // ----------------------------------------------------------------------------
}
