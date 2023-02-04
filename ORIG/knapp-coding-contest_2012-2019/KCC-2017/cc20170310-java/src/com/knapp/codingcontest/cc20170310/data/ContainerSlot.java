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

public class ContainerSlot {
  private final Container container;
  private final int index;
  private Product product;
  private int quantity;

  // ----------------------------------------------------------------------------

  ContainerSlot(final Container container, final int index) {
    this.container = container;
    this.index = index;
  }

  // ----------------------------------------------------------------------------

  public Container getContainer() {
    return container;
  }

  public int getIndex() {
    return index;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    if (isEmpty()) {
      return "ContainerSlot[]";
    }
    return "ContainerSlot[product=" + product + ", quantity=" + quantity + "]";
  }

  // ----------------------------------------------------------------------------

  public boolean isEmpty() {
    return product == null;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
  // should only be called internal!!! otherwise you could tamper with the result
  //   (Warehouse.apply(moveItems), InputData.readContainers())

  public void _setProduct(final Product product) {
    this.product = product;
  }

  public void _setQuantity(final int quantity) {
    this.quantity = quantity;
    if (quantity == 0) {
      product = null;
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
