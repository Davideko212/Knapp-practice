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

package com.knapp.codingcontest.kcc2019.data;

import java.util.Properties;

public class WarehouseCharacteristics {
  // ----------------------------------------------------------------------------

  private final int numberOfLanes;
  private final int laneSize;
  private final int bufferSize;

  private final int releaseLaneFixedTicks;
  private final int releaseLaneItemsTicks;
  private final int maxTickCount;

  private final long costPerTick;
  private final long costFetchProduct;
  private final long costRouteSequenceError;
  private final long costUnfinishedOrdersPenalty;
  private final long costPerUnfinishedItem;

  // ----------------------------------------------------------------------------

  WarehouseCharacteristics(final Properties warehouseCharacteristics) {
    numberOfLanes = Integer.parseInt((String) warehouseCharacteristics.get("numberOfLanes"));
    laneSize = Integer.parseInt((String) warehouseCharacteristics.get("laneSize"));
    bufferSize = Integer.parseInt((String) warehouseCharacteristics.get("bufferSize"));

    releaseLaneFixedTicks = Integer.parseInt((String) warehouseCharacteristics.get("releaseLaneFixedTicks"));
    releaseLaneItemsTicks = Integer.parseInt((String) warehouseCharacteristics.get("releaseLaneItemsTicks"));
    maxTickCount = Integer.parseInt((String) warehouseCharacteristics.get("maxTickCount"));

    costPerTick = Long.parseLong((String) warehouseCharacteristics.get("costPerTick"));
    costFetchProduct = Long.parseLong((String) warehouseCharacteristics.get("costFetchProduct"));
    costRouteSequenceError = Long.parseLong((String) warehouseCharacteristics.get("costRouteSequenceError"));
    costUnfinishedOrdersPenalty = Long.parseLong((String) warehouseCharacteristics.get("costUnfinishedOrdersPenalty"));
    costPerUnfinishedItem = Long.parseLong((String) warehouseCharacteristics.get("costPerUnfinishedItem"));
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("WarehouseCharacteristics[\n");
    sb.append("    numberOfLanes               = ").append(numberOfLanes).append("\n");
    sb.append("    laneSize                    = ").append(laneSize).append("\n");
    sb.append("    bufferSize                  = ").append(bufferSize).append("\n");
    sb.append("\n");
    sb.append("    releaseLaneFixedTicks       = ").append(releaseLaneFixedTicks).append("\n");
    sb.append("    releaseLaneItemsTicks       = ").append(releaseLaneItemsTicks).append("\n");
    sb.append("    maxTickCount                = ").append(maxTickCount).append("\n");
    sb.append("\n");
    sb.append("    costPerTick                 = ").append(costPerTick).append("\n");
    sb.append("    costFetchProduct            = ").append(costFetchProduct).append("\n");
    sb.append("    costRouteSequenceError      = ").append(costRouteSequenceError).append("\n");
    sb.append("    costUnfinishedOrdersPenalty = ").append(costUnfinishedOrdersPenalty).append("\n");
    sb.append("    costPerUnfinishedItem       = ").append(costPerUnfinishedItem).append("\n");
    sb.append("]");
    return sb.toString();
  }

  // ----------------------------------------------------------------------------

  public int getNumberOfLanes() {
    return numberOfLanes;
  }

  public int getLaneSize() {
    return laneSize;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public int getReleaseLaneFixedTicks() {
    return releaseLaneFixedTicks;
  }

  public int getReleaseLaneItemsTicks() {
    return releaseLaneItemsTicks;
  }

  public int getMaxTickCount() {
    return maxTickCount;
  }

  public long getCostPerTick() {
    return costPerTick;
  }

  public long getCostFetchProduct() {
    return costFetchProduct;
  }

  public long getCostRouteSequenceError() {
    return costRouteSequenceError;
  }

  public long getCostUnfinishedOrdersPenalty() {
    return costUnfinishedOrdersPenalty;
  }

  public long getCostPerUnfinishedItem() {
    return costPerUnfinishedItem;
  }

  // ----------------------------------------------------------------------------
}
