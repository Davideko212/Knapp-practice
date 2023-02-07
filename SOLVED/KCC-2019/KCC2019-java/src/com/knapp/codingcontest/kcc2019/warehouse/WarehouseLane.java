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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.knapp.codingcontest.kcc2019.data.Order;
import com.knapp.codingcontest.kcc2019.data.OrderLine;
import com.knapp.codingcontest.kcc2019.data.WarehouseCharacteristics;

public abstract class WarehouseLane {
  // ----------------------------------------------------------------------------

  public enum State {
    Available, Realeasing, ;
  }

  public static class PickLine {
    public final OrderLine orderLine;
    public final int pickQuantity;

    PickLine(final OrderLine orderLine, final int pickQuantity) {
      this.orderLine = orderLine;
      this.pickQuantity = pickQuantity;
    }

    @Override
    public String toString() {
      return "PickLine[orderLine=" + orderLine + ", pickQuantity=" + pickQuantity + "]";
    }
  }

  // ----------------------------------------------------------------------------

  private final int index;
  private final int laneSize;
  private final int releaseLaneFixedTicks;
  private final int releaseLaneItemsTicks;

  protected State state = State.Available;
  protected final LinkedList<PickLine> pickLines = new LinkedList<PickLine>();

  // ----------------------------------------------------------------------------

  protected WarehouseLane(final int index, final WarehouseCharacteristics warehouseCharacteristics) {
    this.index = index;
    laneSize = warehouseCharacteristics.getLaneSize();
    releaseLaneFixedTicks = warehouseCharacteristics.getReleaseLaneFixedTicks();
    releaseLaneItemsTicks = warehouseCharacteristics.getReleaseLaneItemsTicks();
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "WarehouseLane[index=" + index + ", laneSize=" + laneSize + ", state=" + state + ", pickLines=" + pickLines + "]";
  }

  // ----------------------------------------------------------------------------

  public int getIndex() {
    return index;
  }

  public int getSize() {
    return laneSize;
  }

  public State getState() {
    return state;
  }

  // ----------------------------------------------------------------------------

  public int getCurrentQuantity() {
    int currentQuantity = 0;
    for (final PickLine pickLine : pickLines) {
      currentQuantity += pickLine.pickQuantity;
    }
    return currentQuantity;
  }

  public List<PickLine> getCurrentPickLines() {
    return Collections.unmodifiableList(pickLines);
  }

  public int getCurrentSortingTicks() {
    int requiredReleaseTicks = releaseLaneFixedTicks;
    final int currentQuantity = getCurrentQuantity();
    requiredReleaseTicks += ((((releaseLaneItemsTicks * currentQuantity) + laneSize) - 1) / laneSize);
    return requiredReleaseTicks;
  }

  // ----------------------------------------------------------------------------

  void precheckPick(final OrderLine orderLine, final int pickQuantity) throws LaneNotAvailableException, LaneOverflowException,
      OrderLineExceededException {
    if (state != State.Available) {
      throw new LaneNotAvailableException(this);
    }
    if ((getCurrentQuantity() + pickQuantity) > laneSize) {
      throw new LaneOverflowException(this, pickQuantity);
    }
    if ((sumQuantity(orderLine) + pickQuantity) > orderLine.getRequestedQuantity()) {
      throw new OrderLineExceededException(this, orderLine, pickQuantity);
    }
  }

  void precheckRelease(final boolean checkOrders) throws ReleaseInProgressException, IncompleteOrderException {
    if (state == State.Realeasing) {
      throw new ReleaseInProgressException(this);
    }
    if (checkOrders) {
      ckeckOrders();
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private int sumQuantity(final OrderLine orderLine) {
    int sumQuantity = 0;
    for (final PickLine pickLine : pickLines) {
      if (orderLine.getOrder().getCode().equals(pickLine.orderLine.getOrder().getCode())) {
        if (orderLine.getProductCode().equals(pickLine.orderLine.getProductCode())) {
          sumQuantity += pickLine.pickQuantity;
        }
      }
    }
    return sumQuantity;
  }

  private void ckeckOrders() throws IncompleteOrderException {
    final Set<String> checkedOrderCodes = new HashSet<String>();
    for (final PickLine pickLine : pickLines) {
      final Order order = pickLine.orderLine.getOrder();
      if (checkedOrderCodes.contains(order.getCode())) {
        continue;
      }
      checkOrder(order);
      checkedOrderCodes.add(pickLine.orderLine.getOrder().getCode());
    }
  }

  private void checkOrder(final Order order) throws IncompleteOrderException {
    for (final OrderLine orderLine : order.getOrderLines()) {
      if (sumQuantity(orderLine) < orderLine.getRequestedQuantity()) {
        throw new IncompleteOrderException(this, order, orderLine);
      }
    }
  }

  void addItems(final OrderLine orderLine, final int pickQuantity) {
    pickLines.add(new PickLine(orderLine, pickQuantity));
  }

  protected abstract void doRelease();

  // ----------------------------------------------------------------------------
}
