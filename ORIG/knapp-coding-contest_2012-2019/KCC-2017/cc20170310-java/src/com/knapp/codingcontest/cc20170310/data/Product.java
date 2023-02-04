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

package com.knapp.codingcontest.cc20170310.data;

import java.util.EnumMap;
import java.util.Map;

public class Product {
  // ----------------------------------------------------------------------------

  private final String code;
  private final Map<ContainerType, Integer> maxQuantities;

  // ----------------------------------------------------------------------------

  protected Product(final String code) {
    this.code = code;
    maxQuantities = new EnumMap<ContainerType, Integer>(ContainerType.class);
    for (final ContainerType t : ContainerType.values()) {
      maxQuantities.put(t, Integer.valueOf(0));
    }
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Product[code=" + code + ", maxQuantities=" + maxQuantities + "]";
  }

  // ----------------------------------------------------------------------------

  public final String getCode() {
    return code;
  }

  public final int getMaxQuantity(final ContainerType type) {
    return maxQuantities.get(type).intValue();
  }

  // ----------------------------------------------------------------------------

  protected void setMaxQuantity(final ContainerType type, final int maxQuantity) {
    maxQuantities.put(type, Integer.valueOf(maxQuantity));
  }

  // ----------------------------------------------------------------------------
  // ............................................................................
}
