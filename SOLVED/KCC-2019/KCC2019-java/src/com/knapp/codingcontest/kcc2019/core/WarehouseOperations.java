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

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.knapp.codingcontest.kcc2019.data.InputData;
import com.knapp.codingcontest.kcc2019.data.WarehouseCharacteristics;
import com.knapp.codingcontest.kcc2019.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseStatistics;

public final class WarehouseOperations implements WarehouseStatistics {
  // ----------------------------------------------------------------------------

  private final WarehouseInternal warehouse;
  private final InputData input;
  private final WarehouseCharacteristics characteristics;
  private final List<WarehouseOperation> warehouseOperations = new LinkedList<WarehouseOperation>();
  private final Map<WarehouseStatistics.Operation, long[]> currentOperationsCost = new EnumMap<WarehouseStatistics.Operation, long[]>(
      WarehouseStatistics.Operation.class);
  private int routeSequenceErrors = 0;

  // ----------------------------------------------------------------------------

  WarehouseOperations(final WarehouseInternal warehouse, final InputData input) {
    this.warehouse = warehouse;
    this.input = input;
    characteristics = input.getWarehouseCharacteristics();
    for (final WarehouseStatistics.Operation op : WarehouseStatistics.Operation.values()) {
      currentOperationsCost.put(op, new long[] { 0, 0 });
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  @Override
  public Map<Operation, long[]> getCurrentOperationsDetails() {
    final Map<Operation, long[]> _currentOperationsCost = new EnumMap<WarehouseStatistics.Operation, long[]>(
        WarehouseStatistics.Operation.class);
    for (final Map.Entry<Operation, long[]> e : currentOperationsCost.entrySet()) {
      final long[] v = e.getValue();
      _currentOperationsCost.put(e.getKey(), new long[] { v[0], v[1] });
    }
    return _currentOperationsCost;
  }

  // { #orders, #lines, #quantity }
  @Override
  public int[] getCurrentUnfinishedOrders() {
    return warehouse.getShipping().getCurrentUnfinishedOrders();
  }

  @Override
  public int getCurrentRouteSequenceErrors() {
    return routeSequenceErrors;
  }

  // ............................................................................

  @Override
  public long getCurrentOperationsCost() {
    long currentOperationsCost = 0;
    for (final long[] oc : this.currentOperationsCost.values()) {
      currentOperationsCost += oc[1];
    }
    return currentOperationsCost;
  }

  @Override
  public long getCurrentUnfinishedOrdersCost() {
    final int[] unfinishedOrders = getCurrentUnfinishedOrders();
    if (unfinishedOrders[0] > 0) {
      return characteristics.getCostUnfinishedOrdersPenalty()
          + (unfinishedOrders[2] * characteristics.getCostPerUnfinishedItem());
    }
    return 0;
  }

  @Override
  public long getCurrentRouteSequenceErrorCost() {
    return routeSequenceErrors * characteristics.getCostRouteSequenceError();
  }

  @Override
  public long getCurrentTotalCost() {
    return getCurrentOperationsCost() + getCurrentUnfinishedOrdersCost() + getCurrentRouteSequenceErrorCost();
  }

  public long _getCurrentTotalCost() {
    return _getCurrentOperationsCost() + getCurrentUnfinishedOrdersCost() + getCurrentRouteSequenceErrorCost();
  }

  private long _getCurrentOperationsCost() {
    long currentOperationsCost = 0;
    for (final Map.Entry<Operation, long[]> e : this.currentOperationsCost.entrySet()) {
      final long[] oc = e.getValue();
      switch (e.getKey()) {
        case StartTick:
          if (getCurrentUnfinishedOrders()[0] > 0) {
            currentOperationsCost += (characteristics.getMaxTickCount() * characteristics.getCostPerTick());
          } else {
            currentOperationsCost += oc[1];
          }
          break;

        case FetchProduct:
        case PickToLane:
        case PickToBuffer:
        case MoveFromBuffer:
        case ReleaseLane:
          currentOperationsCost += oc[1];
          break;
      }
    }
    return currentOperationsCost;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
  // INTERNAL HOUSEKEEPING OPERATIONS

  void incRouteSequenceErrors() {
    routeSequenceErrors++;
  }

  public Iterable<WarehouseOperations.WarehouseOperation> result() {
    return Collections.unmodifiableList(warehouseOperations);
  }

  // ----------------------------------------------------------------------------

  void add(final WarehouseOperation warehouseOperation) {
    warehouseOperations.add(warehouseOperation);
    final long[] oc = currentOperationsCost.get(warehouseOperation.operation());
    oc[0]++;
    oc[1] += warehouseOperation.calcCost(warehouse, characteristics);
  }

  // ----------------------------------------------------------------------------

  void startTick(final int currentTick) {
    warehouse.setFetchedProductCode(null);
    add(new WarehouseOperations.StartTick(currentTick));
  }

  void finishTick(final int currentTick) {
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static abstract class WarehouseOperation {
    private final String toResultString;

    protected WarehouseOperation(final Object... args) {
      final StringBuilder sb = new StringBuilder();
      sb.append(getClass().getSimpleName()).append(";");
      for (final Object arg : args) {
        sb.append(arg).append(";");
      }
      toResultString = sb.toString();
    }

    public abstract Operation operation();

    protected abstract long calcCost(Warehouse warehouse, WarehouseCharacteristics characteristics);

    public final String toResultString() {
      return toResultString;
    }

    @Override
    public String toString() {
      return toResultString();
    }
  }

  // ----------------------------------------------------------------------------

  public static class StartTick extends WarehouseOperation {
    public final int currentTick;

    public StartTick(final int currentTick) {
      super(currentTick);
      this.currentTick = currentTick;
    }

    @Override
    public Operation operation() {
      return Operation.StartTick;
    }

    @Override
    protected long calcCost(final Warehouse warehouse, final WarehouseCharacteristics characteristics) {
      return characteristics.getCostPerTick();
    }

  }

  public static class FetchProduct extends WarehouseOperation {
    public final String productCode;

    public FetchProduct(final String productCode) {
      super(productCode);
      this.productCode = productCode;
    }

    @Override
    public Operation operation() {
      return Operation.FetchProduct;
    }

    @Override
    protected long calcCost(final Warehouse warehouse, final WarehouseCharacteristics characteristics) {
      return characteristics.getCostFetchProduct();
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public static class PickToLane extends WarehouseOperation {
    public final int laneIndex;
    public final String orderCode;
    public final int pickQuantity;

    public PickToLane(final int laneIndex, final String orderCode, final int pickQuantity) {
      super(laneIndex, orderCode, pickQuantity);
      this.laneIndex = laneIndex;
      this.orderCode = orderCode;
      this.pickQuantity = pickQuantity;
    }

    @Override
    public Operation operation() {
      return Operation.PickToLane;
    }

    @Override
    protected long calcCost(final Warehouse warehouse, final WarehouseCharacteristics characteristics) {
      return 0;
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public static class PickToBuffer extends WarehouseOperation {
    public final int pickQuantity;

    public PickToBuffer(final int pickQuantity) {
      super(pickQuantity);
      this.pickQuantity = pickQuantity;
    }

    @Override
    public Operation operation() {
      return Operation.PickToBuffer;
    }

    @Override
    protected long calcCost(final Warehouse warehouse, final WarehouseCharacteristics characteristics) {
      return 0;
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public static class MoveFromBuffer extends WarehouseOperation {
    public final int laneIndex;
    public final String orderCode;
    public final String productCode;
    public final int pickQuantity;

    public MoveFromBuffer(final int laneIndex, final String orderCode, final String productCode, final int pickQuantity) {
      super(laneIndex, orderCode, productCode, pickQuantity);
      this.laneIndex = laneIndex;
      this.orderCode = orderCode;
      this.productCode = productCode;
      this.pickQuantity = pickQuantity;
    }

    @Override
    public Operation operation() {
      return Operation.MoveFromBuffer;
    }

    @Override
    protected long calcCost(final Warehouse warehouse, final WarehouseCharacteristics characteristics) {
      return 0;
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  public static class ReleaseLane extends WarehouseOperation {
    public final int laneIndex;

    public ReleaseLane(final int laneIndex) {
      super(laneIndex);
      this.laneIndex = laneIndex;
    }

    @Override
    public Operation operation() {
      return Operation.ReleaseLane;
    }

    @Override
    protected long calcCost(final Warehouse warehouse, final WarehouseCharacteristics characteristics) {
      return 0;
    }
  }

  // ----------------------------------------------------------------------------
}
