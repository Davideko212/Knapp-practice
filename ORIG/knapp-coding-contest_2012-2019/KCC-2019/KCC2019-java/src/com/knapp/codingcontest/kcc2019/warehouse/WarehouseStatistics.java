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

import java.util.Map;

public interface WarehouseStatistics {
  public static enum Operation {
    StartTick, FetchProduct, PickToLane, PickToBuffer, MoveFromBuffer, ReleaseLane, ;
  }

  // ----------------------------------------------------------------------------

  // Operation, [#, cost]
  Map<Operation, long[]> getCurrentOperationsDetails();

  // { #orders, #lines, #quantity }
  int[] getCurrentUnfinishedOrders();

  // ............................................................................

  long getCurrentOperationsCost();

  long getCurrentUnfinishedOrdersCost();

  int getCurrentRouteSequenceErrors();

  long getCurrentRouteSequenceErrorCost();

  long getCurrentTotalCost();
}
