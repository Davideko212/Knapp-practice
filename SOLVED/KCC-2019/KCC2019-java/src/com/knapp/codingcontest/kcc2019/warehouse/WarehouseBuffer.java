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

package com.knapp.codingcontest.kcc2019.warehouse;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.knapp.codingcontest.kcc2019.data.OrderLine;

public class WarehouseBuffer {
  // ----------------------------------------------------------------------------

  private final int bufferSize;
  private final Map<String, Integer> productItemCount = new TreeMap<String, Integer>();

  // ----------------------------------------------------------------------------

  WarehouseBuffer(final int bufferSize) {
    this.bufferSize = bufferSize;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("WarehouseBuffer[bufferSize=").append(bufferSize).append(", productItemCount=[");
    for (final Map.Entry<String, Integer> ic : productItemCount.entrySet()) {
      sb.append(ic.getKey()).append("#").append(ic.getValue()).append(", ");
    }
    sb.append("]]\n");
    return sb.toString();
  }

  // ----------------------------------------------------------------------------

  public int getSize() {
    return bufferSize;
  }

  public int getCurrentQuantity() {
    int currentQuantity = 0;
    for (final Integer ic : productItemCount.values()) {
      currentQuantity += ic.intValue();
    }
    return currentQuantity;
  }

  public Map<String, Integer> getCurrentProducts() {
    return Collections.unmodifiableMap(new TreeMap<String, Integer>(productItemCount));
  }

  // ----------------------------------------------------------------------------

  void precheckPick(final int pickQuantity) throws BufferOverflowException {
    if ((getCurrentQuantity() + pickQuantity) > bufferSize) {
      throw new BufferOverflowException(this);
    }
  }

  void precheckMove(final OrderLine orderLine, final int pickQuantity) throws MissingProductQuantityException {
    if (currentQuantity(orderLine) < pickQuantity) {
      throw new MissingProductQuantityException(this, orderLine, pickQuantity);
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private int currentQuantity(final OrderLine orderLine) {
    final Integer ic = productItemCount.get(orderLine.getProductCode());
    return ic != null ? ic.intValue() : 0;
  }

  void addItems(final String productCode, final int pickQuantity) {
    final Integer ic = productItemCount.get(productCode);
    if (ic == null) {
      productItemCount.put(productCode, Integer.valueOf(pickQuantity));
    } else {
      productItemCount.put(productCode, Integer.valueOf(ic.intValue() + pickQuantity));
    }
  }

  void removeItems(final String productCode, final int moveQuantity) {
    final Integer ic = productItemCount.remove(productCode);
    final int newValue = ic.intValue() - moveQuantity;
    if (newValue > 0) {
      productItemCount.put(productCode, Integer.valueOf(newValue));
    }
  }
}
