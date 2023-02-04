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
 * represents a location within a shelf that can hold one single product
 */
public class Location {
  private final String code;
  private Product product;

  // ----------------------------------------------------------------------------

  protected Location(final String code) {
    this.code = code;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Location[" + code + ": " + product + "]";
  }

  // ----------------------------------------------------------------------------

  /**
   * @return the unique code of the location
   */
  public String getCode() {
    return code;
  }

  // ----------------------------------------------------------------------------

  /**
   * Assign a new product to this location.
   * if a product was previously assigned, it is returned
   *
   * @return a previously assigned product or null if none was assgigned
   */
  public Product assignProduct(final Product product) {
    final Product product_ = this.product;
    this.product = product;
    return product_;
  }

  /**
   * Unassign a location
   *
   * @return a previously assigned product or null if none was assgigned
   */
  public Product clearAssignedProduct() {
    final Product product_ = product;
    product = null;
    return product_;
  }

  /**
   * Queries wether a product is currently assigned to this location
   *
   * @return true -a product is assigned; false otherwise
   */
  public boolean isProductAssigned() {
    return product != null;
  }

  /**
   * @return currently assigned product or null of none is assigned
   */
  public Product getProduct() {
    return product;
  }

  // ----------------------------------------------------------------------------
}
