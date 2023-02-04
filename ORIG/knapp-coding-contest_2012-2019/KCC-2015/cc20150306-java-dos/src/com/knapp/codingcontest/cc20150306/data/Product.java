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
 * A product that is handled in a warehouse
 */
public class Product {
  private final String code;
  private final boolean cool;
  private final boolean frozen;
  private final boolean valuable;

  // ----------------------------------------------------------------------------

  protected Product(final String code, final boolean cool, final boolean frozen, final boolean valuable) {
    this.code = code;
    this.cool = cool;
    this.frozen = frozen;
    this.valuable = valuable;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Product[" + code + ": cool=" + cool + ", frozen=" + frozen + ", valuable=" + valuable + "]\n";
  }

  // ----------------------------------------------------------------------------

  /**
   * @return Unique code of this product
   */
  public String getCode() {
    return code;
  }

  /**
   * @return Does this product require cool storage
   */
  public boolean needsCool() {
    return cool;
  }

  /**
   * @return Does this product require frozen storage
   */
  public boolean needsFrozen() {
    return frozen;
  }

  /**
   * @return Is this product valuable and therefore needs secure storage
   */
  public boolean needsValuable() {
    return valuable;
  }

  // ----------------------------------------------------------------------------
}
