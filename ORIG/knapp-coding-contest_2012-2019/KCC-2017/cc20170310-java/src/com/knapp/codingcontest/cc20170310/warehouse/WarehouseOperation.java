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

package com.knapp.codingcontest.cc20170310.warehouse;

import com.knapp.codingcontest.cc20170310.data.Container;
import com.knapp.codingcontest.cc20170310.data.ContainerSlot;

public abstract class WarehouseOperation {
  private final String toResultString;

  // ----------------------------------------------------------------------------

  protected WarehouseOperation(final Object... args) {
    final StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName()).append(";");
    for (final Object arg : args) {
      sb.append(arg).append(";");
    }
    toResultString = sb.toString();
  }

  // ----------------------------------------------------------------------------

  public final String toResultString() {
    return toResultString;
  }

  @Override
  public String toString() {
    return toResultString();
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static final class GetContainer extends WarehouseOperation {
    public GetContainer(final Container container) {
      super(container.getCode());
    }
  }

  public static final class PutContainer extends WarehouseOperation {
    public PutContainer(final Container container) {
      super(container.getCode());
    }
  }

  public static final class MoveItems extends WarehouseOperation {
    public final ContainerSlot source;
    public final ContainerSlot destination;
    public final int quantity;

    public MoveItems(final ContainerSlot source, final ContainerSlot destination, final int quantity) {
      super(source.getContainer().getCode(), source.getIndex(), destination.getContainer().getCode(), destination.getIndex(),
          quantity);
      this.source = source;
      this.destination = destination;
      this.quantity = quantity;
    }

    @Override
    public String toString() {
      return "MoveItems[source=" + source.getContainer().getCode() + "/" + source.getIndex() + ", destination="
          + destination.getContainer().getCode() + "/" + destination.getIndex() + ", quantity=" + quantity + "]";
    }
  }
}
