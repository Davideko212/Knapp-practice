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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.knapp.codingcontest.kcc2019.data.InputData;
import com.knapp.codingcontest.kcc2019.data.WarehouseCharacteristics;
import com.knapp.codingcontest.kcc2019.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseLane;

public class WarehouseInternal extends Warehouse {
  // ----------------------------------------------------------------------------

  private final InputData input;

  // ----------------------------------------------------------------------------

  private final List<WarehouseLaneInternal> lanes;
  final WarehouseSorter sorter;
  final WarehouseShipping shipping;

  private final WarehouseOperations warehouseOperations;

  // ----------------------------------------------------------------------------

  public WarehouseInternal(final InputData input) {
    super(input);
    this.input = input;
    final WarehouseCharacteristics characteristics = input.getWarehouseCharacteristics();
    lanes = new ArrayList<WarehouseLaneInternal>(characteristics.getNumberOfLanes());
    for (int i = 0; i < characteristics.getNumberOfLanes(); i++) {
      lanes.add(new WarehouseLaneInternal(this, i, characteristics));
    }
    sorter = new WarehouseSorter(this, input);
    shipping = new WarehouseShipping(this, input);
    warehouseOperations = new WarehouseOperations(this, input);
  }

  // ----------------------------------------------------------------------------

  @Override
  public List<? extends WarehouseLane> getLanes() {
    return Collections.unmodifiableList(lanes);
  }

  // ----------------------------------------------------------------------------

  public WarehouseSorter getSorter() {
    return sorter;
  }

  public WarehouseShipping getShipping() {
    return shipping;
  }

  // ----------------------------------------------------------------------------

  @Override
  public WarehouseOperations getStatistics() {
    return warehouseOperations;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
  // INTERNAL HOUSEKEEPING OPERATIONS

  @Override
  protected void add(final WarehouseOperations.WarehouseOperation warehouseOperation) {
    warehouseOperations.add(warehouseOperation);
  }

  int currentTick;

  public void startTick(final int currentTick) {
    warehouseOperations.startTick(currentTick);
    this.currentTick = currentTick;
  }

  public void finishTick(final int currentTick) {
    sorter.tick(currentTick);
    warehouseOperations.finishTick(currentTick);
  }

  void setFetchedProductCode(final String fetchedProductCode) {
    this.fetchedProductCode = fetchedProductCode;
  }

  @Override
  public WarehouseLane getReleasingLane() {
    return sorter.getReleasingLane();
  }

  // ----------------------------------------------------------------------------
}
