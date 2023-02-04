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

import java.util.LinkedList;

import com.knapp.codingcontest.kcc2019.data.WarehouseCharacteristics;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseLane;

public class WarehouseLaneInternal extends WarehouseLane {
  // ----------------------------------------------------------------------------

  private final WarehouseInternal warehouse;

  // ----------------------------------------------------------------------------

  WarehouseLaneInternal(final WarehouseInternal warehouse, final int index,
      final WarehouseCharacteristics warehouseCharacteristics) {
    super(index, warehouseCharacteristics);
    this.warehouse = warehouse;
  }

  // ----------------------------------------------------------------------------

  void setState(final State state) {
    this.state = state;
  }

  @Override
  protected void doRelease() {
    warehouse.getSorter().releaseLane(warehouse.currentTick, this);
  }

  LinkedList<PickLine> consumePickLines() {
    final LinkedList<PickLine> _pickLines = new LinkedList<PickLine>(pickLines);
    pickLines.clear();
    return _pickLines;
  }

  // ----------------------------------------------------------------------------
}
