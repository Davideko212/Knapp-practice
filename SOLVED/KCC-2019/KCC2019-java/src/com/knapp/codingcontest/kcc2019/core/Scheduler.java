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

import com.knapp.codingcontest.kcc2019.data.InputData;
import com.knapp.codingcontest.kcc2019.solution.Solution;

public class Scheduler {
  // ----------------------------------------------------------------------------

  private final WarehouseInternal warehouse;
  private final InputData input;

  private int currentTick = 0;

  // ----------------------------------------------------------------------------

  public Scheduler(final WarehouseInternal warehouse, final InputData input) {
    this.warehouse = warehouse;
    this.input = input;
  }

  // ----------------------------------------------------------------------------

  public void run(final Solution solution) throws Exception {
    final int maxTickCount = input.getWarehouseCharacteristics().getMaxTickCount();
    while (currentTick < maxTickCount) {
      warehouse.startTick(currentTick);
      solution.tick(currentTick);
      warehouse.finishTick(currentTick);
      if (warehouse.getShipping().areAllOrdersFinished()) {
        break;
      }
      currentTick++;
    }
  }

  public int getCurrentTick() {
    return currentTick;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
