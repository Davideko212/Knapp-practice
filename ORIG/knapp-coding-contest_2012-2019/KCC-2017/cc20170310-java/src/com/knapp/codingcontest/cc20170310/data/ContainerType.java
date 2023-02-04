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

public enum ContainerType {
  Full(1), //
  Half(2), //
  Quarter(4), //
  ;

  private final int slots;

  private ContainerType(final int slots) {
    this.slots = slots;
  }

  public int getNumberOfSlots() {
    return slots;
  }
}
