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

public class InvalidQuantityException extends Exception {
  private static final long serialVersionUID = 1L;

  // ----------------------------------------------------------------------------

  public final transient int pickQuantity;

  // ----------------------------------------------------------------------------

  public InvalidQuantityException(final int pickQuantity) {
    super("InvalidQuantity: " + pickQuantity);
    this.pickQuantity = pickQuantity;
  }

  // ----------------------------------------------------------------------------

}
