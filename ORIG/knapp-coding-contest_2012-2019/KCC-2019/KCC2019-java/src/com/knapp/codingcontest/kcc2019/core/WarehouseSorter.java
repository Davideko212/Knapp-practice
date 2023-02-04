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
import java.util.Comparator;
import java.util.LinkedList;

import com.knapp.codingcontest.kcc2019.data.InputData;
import com.knapp.codingcontest.kcc2019.data.WarehouseCharacteristics;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseLane;

public class WarehouseSorter {
  // ----------------------------------------------------------------------------

  public enum State {
    Idle, Sorting, ;
  }

  // ----------------------------------------------------------------------------

  private final WarehouseInternal warehouse;
  private final WarehouseCharacteristics characteristics;

  private State state = State.Idle;
  private int tickReleaseFinished;
  private WarehouseLaneInternal releasingLane;

  // ----------------------------------------------------------------------------

  public WarehouseSorter(final WarehouseInternal warehouse, final InputData input) {
    this.warehouse = warehouse;
    characteristics = input.getWarehouseCharacteristics();
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "WarehouseSorter[state=" + state + ", tickReleaseFinished=" + tickReleaseFinished + ", releasingLane="
        + releasingLane + "]\n";
  }

  // ----------------------------------------------------------------------------

  public State getState() {
    return state;
  }

  public WarehouseLane getReleasingLane() {
    return releasingLane;
  }

  // ----------------------------------------------------------------------------

  void releaseLane(final int currentTick, final WarehouseLaneInternal releasingLane) {
    tickReleaseFinished = currentTick + releasingLane.getCurrentSortingTicks();
    this.releasingLane = releasingLane;
    this.releasingLane.setState(WarehouseLane.State.Realeasing);
    state = State.Sorting;
  }

  public void tick(final int currentTick) {
    switch (state) {
      case Idle:
        break;

      case Sorting:
        if (currentTick >= tickReleaseFinished) {
          final LinkedList<WarehouseLane.PickLine> pickLines = releasingLane.consumePickLines();
          Collections.sort(pickLines, WarehouseSorter.ORDER_LINE_COMPARATOR);
          warehouse.getShipping().processSortedLines(currentTick, pickLines);
          releasingLane.setState(WarehouseLane.State.Available);
          releasingLane = null;
          state = State.Idle;
        }
        break;
    }
  }

  // ----------------------------------------------------------------------------

  private static final Comparator<WarehouseLane.PickLine> ORDER_LINE_COMPARATOR = new Comparator<WarehouseLane.PickLine>() {
    @Override
    public int compare(final WarehouseLane.PickLine l1, final WarehouseLane.PickLine l2) {
      final int cmp = l1.orderLine.getOrder().getRouteDeparture() - l2.orderLine.getOrder().getRouteDeparture();
      if (cmp != 0) {
        return cmp;
      }
      return l1.orderLine.getOrder().getCode().compareTo(l2.orderLine.getOrder().getCode());
    }
  };
}
