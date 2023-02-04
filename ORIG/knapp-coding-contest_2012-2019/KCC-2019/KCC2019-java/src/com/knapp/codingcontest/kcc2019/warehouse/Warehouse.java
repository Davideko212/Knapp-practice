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

import java.util.List;

import com.knapp.codingcontest.kcc2019.core.WarehouseOperations;
import com.knapp.codingcontest.kcc2019.data.InputData;
import com.knapp.codingcontest.kcc2019.data.OrderLine;
import com.knapp.codingcontest.kcc2019.data.WarehouseCharacteristics;

public abstract class Warehouse {
  // ----------------------------------------------------------------------------

  protected String fetchedProductCode;
  protected final WarehouseBuffer buffer;

  // ----------------------------------------------------------------------------

  protected Warehouse(final InputData input) {
    final WarehouseCharacteristics characteristics = input.getWarehouseCharacteristics();
    buffer = new WarehouseBuffer(characteristics.getBufferSize());
  }

  // ----------------------------------------------------------------------------

  public abstract WarehouseStatistics getStatistics();

  public abstract List<? extends WarehouseLane> getLanes();

  public WarehouseBuffer getBuffer() {
    return buffer;
  }

  public abstract WarehouseLane getReleasingLane();

  // ----------------------------------------------------------------------------

  /**
   * Instruct the warehouse to fetch a specific product for picking to lane or picking to buffer.
   *
   *   Allowed max once per tick
   *
   * @param productCode code of the product to fetch
   */
  public void fetchProduct(final String productCode) throws OnlyOneFetchPerTickAllowedException {
    checkFetchProduct(productCode);
    doFetchProduct(productCode);
  }

  /**
   * Pick the fetched product to a lane for a specific orderline.
   *
   * @param lane target lane
   * @param orderLine the orderline to pick for
   * @param pickQuantity the quantity to pick. May be smaller than requested quantity (i.e. when remaining quantity is in buffer)
   */
  public void pickToLane(final WarehouseLane lane, final OrderLine orderLine, final int pickQuantity)
      throws InvalidQuantityException, MissingPrecedingFetchException, ProductMismatchException, LaneNotAvailableException,
      LaneOverflowException, OrderLineExceededException {
    checkPickToLane(lane, orderLine, pickQuantity);
    doPickToLane(lane, orderLine, pickQuantity);
  }

  /**
   * Pick a specific quantit of the fetched product to the buffer.
   *
   * @param pickQuantity quantity to pick
   */
  public void pickToBuffer(final int pickQuantity) throws InvalidQuantityException, MissingPrecedingFetchException,
      BufferOverflowException {
    checkPickToBuffer(pickQuantity);
    doPickToBuffer(pickQuantity);
  }

  /**
   * Move a specific quantity from the buffer to a lane for an orderline.
   *
   * @param lane target lane
   * @param orderLine orderline for which the items are moved
   * @param pickQuantity quantity to move
   */
  public void moveFromBuffer(final WarehouseLane lane, final OrderLine orderLine, final int pickQuantity)
      throws InvalidQuantityException, MissingProductQuantityException, LaneNotAvailableException, LaneOverflowException,
      OrderLineExceededException {
    checkMoveFromBuffer(lane, orderLine, pickQuantity);
    doMoveFromBuffer(lane, orderLine, pickQuantity);
  }

  /**
   * Release a lane
   *
   * @param lane the lane to release
   *
   * @throws ReleaseInProgressException
   * @throws IncompleteOrderException
   */
  public void releaseLane(final WarehouseLane lane) throws ReleaseInProgressException, IncompleteOrderException {
    checkReleaseLane(lane);
    doReleaseLane(lane);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
  // INTERNAL HOUSEKEEPING OPERATIONS

  protected abstract void add(WarehouseOperations.WarehouseOperation warehouseOperation);

  // ............................................................................

  private void checkFetchProduct(final String productCode) throws OnlyOneFetchPerTickAllowedException {
    if (fetchedProductCode != null) {
      throw new OnlyOneFetchPerTickAllowedException(fetchedProductCode, productCode);
    }
  }

  private void doFetchProduct(final String productCode) {
    fetchedProductCode = productCode;
    add(new WarehouseOperations.FetchProduct(productCode));
  }

  // ............................................................................

  private void checkPickToLane(final WarehouseLane lane, final OrderLine orderLine, final int pickQuantity)
      throws InvalidQuantityException, MissingPrecedingFetchException, ProductMismatchException, LaneNotAvailableException,
      LaneOverflowException, OrderLineExceededException {
    if (pickQuantity <= 0) {
      throw new InvalidQuantityException(pickQuantity);
    }
    if (fetchedProductCode == null) {
      throw new MissingPrecedingFetchException();
    }
    if (!fetchedProductCode.equals(orderLine.getProductCode())) {
      throw new ProductMismatchException(fetchedProductCode, orderLine);
    }
    lane.precheckPick(orderLine, pickQuantity);
  }

  private void doPickToLane(final WarehouseLane lane, final OrderLine orderLine, final int pickQuantity) {
    lane.addItems(orderLine, pickQuantity);
    add(new WarehouseOperations.PickToLane(lane.getIndex(), orderLine.getOrder().getCode(), pickQuantity));
  }

  // ............................................................................

  private void checkPickToBuffer(final int pickQuantity) throws InvalidQuantityException, MissingPrecedingFetchException,
      BufferOverflowException {
    if (pickQuantity <= 0) {
      throw new InvalidQuantityException(pickQuantity);
    }
    if (fetchedProductCode == null) {
      throw new MissingPrecedingFetchException();
    }
    getBuffer().precheckPick(pickQuantity);
  }

  private void doPickToBuffer(final int pickQuantity) {
    buffer.addItems(fetchedProductCode, pickQuantity);
    add(new WarehouseOperations.PickToBuffer(pickQuantity));
  }

  // ............................................................................

  private void checkMoveFromBuffer(final WarehouseLane lane, final OrderLine orderLine, final int pickQuantity)
      throws InvalidQuantityException, MissingProductQuantityException, LaneNotAvailableException, LaneOverflowException,
      OrderLineExceededException {
    if (pickQuantity <= 0) {
      throw new InvalidQuantityException(pickQuantity);
    }
    getBuffer().precheckMove(orderLine, pickQuantity);
    lane.precheckPick(orderLine, pickQuantity);
  }

  private void doMoveFromBuffer(final WarehouseLane lane, final OrderLine orderLine, final int pickQuantity) {
    buffer.removeItems(orderLine.getProductCode(), pickQuantity);
    lane.addItems(orderLine, pickQuantity);
    add(new WarehouseOperations.MoveFromBuffer(lane.getIndex(), orderLine.getOrder().getCode(), orderLine.getProductCode(),
        pickQuantity));
  }

  // ............................................................................

  private void checkReleaseLane(final WarehouseLane lane) throws ReleaseInProgressException, IncompleteOrderException {
    for (final WarehouseLane _lane : getLanes()) {
      _lane.precheckRelease(_lane.getIndex() == lane.getIndex());
    }
  }

  protected void doReleaseLane(final WarehouseLane lane) {
    lane.doRelease();
    add(new WarehouseOperations.ReleaseLane(lane.getIndex()));
  }

  // ----------------------------------------------------------------------------
}
