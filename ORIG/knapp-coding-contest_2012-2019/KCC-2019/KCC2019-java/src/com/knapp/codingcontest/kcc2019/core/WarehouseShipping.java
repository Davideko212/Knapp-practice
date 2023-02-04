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

package com.knapp.codingcontest.kcc2019.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.knapp.codingcontest.kcc2019.data.InputData;
import com.knapp.codingcontest.kcc2019.data.Order;
import com.knapp.codingcontest.kcc2019.data.OrderLine;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseLane;

public class WarehouseShipping {
  // ----------------------------------------------------------------------------

  private final WarehouseInternal warehouse;
  private final Map<String, MyOrder> myOrders = new HashMap<String, MyOrder>();

  // ----------------------------------------------------------------------------

  WarehouseShipping(final WarehouseInternal warehouse, final InputData input) {
    this.warehouse = warehouse;
    for (final Order order : input.getOrders()) {
      myOrders.put(order.getCode(), new MyOrder(order));
    }
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "WarehouseShipping[myOrders=" + myOrders + "]";
  }

  // ----------------------------------------------------------------------------

  void processSortedLines(final int currentTick, final LinkedList<WarehouseLane.PickLine> pickLines) {
    for (final WarehouseLane.PickLine pickLine : pickLines) {
      processLine(currentTick, pickLine.orderLine.getOrder().getCode(), pickLine.orderLine.getProductCode(),
          pickLine.pickQuantity);
    }
  }

  public boolean areAllOrdersFinished() {
    for (final MyOrder order : myOrders.values()) {
      if (!order.isFinished()) {
        return false;
      }
    }
    return true;
  }

  // { #orders, #lines, #quantity }
  public int[] getCurrentUnfinishedOrders() {
    final int[] unfinishedOrders = { 0, 0, 0 };
    for (final MyOrder order : myOrders.values()) {
      if (!order.isFinished()) {
        unfinishedOrders[0]++;
        for (final MyOrderLine line : order.orderLines.values()) {
          unfinishedOrders[1]++;
          unfinishedOrders[2] += line.requestedQuantity;
        }
      }
    }
    return unfinishedOrders;
  }

  // ----------------------------------------------------------------------------

  private void processLine(final int currentTick, final String orderCode, final String productCode, final int pickQuantity) {
    final MyOrder order = myOrders.get(orderCode);
    order.processLine(productCode, pickQuantity);
    if (order.isFinished()) {
      if (!isCorrectSequence(order)) {
        warehouse.getStatistics().incRouteSequenceErrors();
      }
    }
  }

  private boolean isCorrectSequence(final MyOrder myOrder) {
    for (final MyOrder o : myOrders.values()) {
      if (!o.isFinished() && (o.routeDeparture < myOrder.routeDeparture)) {
        return false;
      }
    }
    return true;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private class MyOrder {
    private final String code;
    private final int routeDeparture;
    private final Map<String, MyOrderLine> orderLines = new HashMap<String, MyOrderLine>();

    private MyOrder(final Order order) {
      code = order.getCode();
      routeDeparture = order.getRouteDeparture();
      for (final OrderLine orderLine : order.getOrderLines()) {
        orderLines.put(orderLine.getProductCode(), new MyOrderLine(orderLine));
      }
    }

    @Override
    public String toString() {
      return "MyOrder[code=" + code + ", routeDeparture=" + routeDeparture + ", orderLines=" + orderLines + "]\n";
    }

    private void processLine(final String productCode, final int pickQuantity) {
      final MyOrderLine orderLine = orderLines.get(productCode);
      orderLine.process(pickQuantity);
    }

    private boolean isFinished() {
      for (final MyOrderLine orderLine : orderLines.values()) {
        if (!orderLine.isFinished()) {
          return false;
        }
      }
      return true;
    }
  }

  private static class MyOrderLine {
    private final String productCode;
    private final int requestedQuantity;
    private int pickedQuantity;

    private MyOrderLine(final OrderLine orderLine) {
      productCode = orderLine.getProductCode();
      requestedQuantity = orderLine.getRequestedQuantity();
      pickedQuantity = 0;
    }

    @Override
    public String toString() {
      return "MyOrderLine[productCode=" + productCode + ", requestedQuantity=" + requestedQuantity + ", pickedQuantity="
          + pickedQuantity + "]";
    }

    public boolean isFinished() {
      return pickedQuantity >= requestedQuantity;
    }

    public void process(final int pickQuantity) {
      pickedQuantity += pickQuantity;
    }
  }
}
