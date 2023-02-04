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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.knapp.codingcontest.cc20170310.data.Container;
import com.knapp.codingcontest.cc20170310.data.ContainerType;

public class WarehouseInfos implements Serializable {
  private static final long serialVersionUID = 1L;

  public final int[] totalContainers_;
  public final int[] totalSlots_;
  public final transient List<Container> containersAtWorkStation;
  //
  public final int numberOfGets;
  public final int numberOfPuts;
  public final int numberOfMoves;
  //
  public final int[] emptyContainers_;
  public final int[] emptySlots_;
  public final Throwable throwable;

  // ----------------------------------------------------------------------------

  public WarehouseInfos(final Warehouse warehouse) {
    this(warehouse, null);
  }

  public WarehouseInfos(final Warehouse warehouse, final Throwable throwable) {
    totalContainers_ = countTotalContainers(warehouse);
    totalSlots_ = countTotalSlots(warehouse);
    containersAtWorkStation = warehouse.getContainersAtWorkStation();
    numberOfGets = warehouse.currentNumberOfGets;
    numberOfPuts = warehouse.currentNumberOfPuts;
    numberOfMoves = warehouse.currentNumberOfMoves;
    emptyContainers_ = countEmptyContainers(warehouse);
    emptySlots_ = countEmptySlots(warehouse);
    this.throwable = throwable;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "WarehouseInfos[totalContainers=" + Arrays.toString(totalContainers_) + ", totalSlots="
        + Arrays.toString(totalSlots_) //
        + ", containersAtWorkStation=" + containersAtWorkStation //
        + ", numberOfGets=" + numberOfGets + ", numberOfPuts=" + numberOfPuts //
        + ", numberOfMoves=" + numberOfMoves //
        + ", emptyContainers=" + Arrays.toString(emptyContainers_) + ", emptySlots=" + Arrays.toString(emptySlots_) //
        + "]";
  }

  // ----------------------------------------------------------------------------

  private int[] countTotalContainers(final Warehouse warehouse) {
    final int[] totalContainers = new int[ContainerType.values().length + 1];
    for (final Container container : warehouse.input.getContainers()) {
      totalContainers[0]++;
      totalContainers[container.getType().ordinal() + 1]++;
    }
    return totalContainers;
  }

  private int[] countTotalSlots(final Warehouse warehouse) {
    final int[] totalSlots = new int[ContainerType.values().length + 1];
    for (final Container container : warehouse.input.getContainers()) {
      totalSlots[0] += container.getType().getNumberOfSlots();
      totalSlots[container.getType().ordinal() + 1] += container.getType().getNumberOfSlots();
    }
    return totalSlots;
  }

  private int[] countEmptyContainers(final Warehouse warehouse) {
    final int[] emptyContainers = new int[ContainerType.values().length + 1];
    for (final Container container : warehouse.input.getContainers()) {
      if (container.isEmpty()) {
        emptyContainers[0]++;
        emptyContainers[container.getType().ordinal() + 1]++;
      }
    }
    return emptyContainers;
  }

  private int[] countEmptySlots(final Warehouse warehouse) {
    final int[] emptySlots = new int[ContainerType.values().length + 1];
    for (final Container container : warehouse.input.getContainers()) {
      for (int s = 0; s < container.getType().getNumberOfSlots(); s++) {
        if (container.getSlots().get(s).isEmpty()) {
          emptySlots[0]++;
          emptySlots[container.getType().ordinal() + 1]++;
        }
      }
    }
    return emptySlots;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
