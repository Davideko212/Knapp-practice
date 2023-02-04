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

package com.knapp.codingcontest.cc20170310;

import java.io.PrintStream;

import com.knapp.codingcontest.cc20170310.data.ContainerType;
import com.knapp.codingcontest.cc20170310.data.InputData;
import com.knapp.codingcontest.cc20170310.solution.OptimizeStorage;
import com.knapp.codingcontest.cc20170310.util.PrepareUpload;
import com.knapp.codingcontest.cc20170310.warehouse.Warehouse;
import com.knapp.codingcontest.cc20170310.warehouse.WarehouseInfos;

/**
 * ----------------------------------------------------------------------------
 * you may change any code you like
 *   => but changing the output may lead to invalid results!
 * ----------------------------------------------------------------------------
 */
public class Main {
  public static void main(final String... args) throws Exception {
    System.out.println("KNAPP Coding Contest 2017: STARTING...");

    System.out.println("# ... LOADING DATA ...");
    Warehouse warehouse = null;
    WarehouseInfos initialWarehouseInfos = null;

    final InputData input = new InputData();
    input.readData();
    warehouse = new Warehouse(input);

    System.out.println("# statistics before optimization");
    initialWarehouseInfos = warehouse.buildWarehouseInfos();
    Main.printWarehouseInfos(System.out, initialWarehouseInfos);

    System.out.println("# ... OPTIMIZE STORAGE ...");
    final OptimizeStorage optimizeStorage = new OptimizeStorage(warehouse);
    optimizeStorage.optimizeStorage();

    System.out.println("# ... WRITING OUTPUT/RESULT ...");
    System.out.println("# statistics after optimization");
    final WarehouseInfos warehouseInfos = warehouse.buildWarehouseInfos();
    //Main.printWarehouseInfos(System.out, warehouseInfos);
    Main.printWarehouseInfosDiff(System.out, initialWarehouseInfos, warehouseInfos);

    PrepareUpload.createZipFile(warehouse);
    System.out
        .println(">>> Created " + PrepareUpload.FILENAME_WAREHOUSE_OPERATIONS + " & " + PrepareUpload.FILENAME_UPLOAD_ZIP);

    System.out.println("KNAPP Coding Contest 2017: FINISHED");
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static final void printWarehouseInfos(final PrintStream out, final WarehouseInfos warehouseInfos) {
    out.println("# =============================================================================");
    out.println("# == WAREHOUSE INFOS ==========================================================");
    out.println("#");
    out.println(String.format("#    total containers / slots"));
    for (final ContainerType ct : ContainerType.values()) {
      out.println(String.format("#        %15s       = %5d / %5d", ct, warehouseInfos.totalContainers_[ct.ordinal() + 1],
          warehouseInfos.totalSlots_[ct.ordinal() + 1]));
    }
    out.println(String.format("#        %15s       = %5d / %5d", "[TOTAL]", //
        warehouseInfos.totalContainers_[0], warehouseInfos.totalSlots_[0]));
    out.println("#");
    out.println(String.format("#    containersAtWorkStation   = %s", warehouseInfos.containersAtWorkStation));
    out.println(String.format("#    numberOf gets / puts      = %5d / %5d", warehouseInfos.numberOfGets,
        warehouseInfos.numberOfPuts));
    out.println(String.format("#    numberOfMoves             = %5d", warehouseInfos.numberOfMoves));
    out.println("#");
    out.println(String.format("#    empty containers / slots"));
    for (final ContainerType ct : ContainerType.values()) {
      out.println(String.format("#        %15s       = %5d / %5d", ct, //
          warehouseInfos.emptyContainers_[ct.ordinal() + 1], warehouseInfos.emptySlots_[ct.ordinal() + 1]));
    }
    out.println(String.format("#        %15s       = %5d / %5d  ( %.1f%% / %.1f%% )", "[TOTAL]", //
        warehouseInfos.emptyContainers_[0], warehouseInfos.emptySlots_[0], //
        ((double) warehouseInfos.emptyContainers_[0] / warehouseInfos.totalContainers_[0]) * 100, //
        ((double) warehouseInfos.emptySlots_[0] / warehouseInfos.totalSlots_[0]) * 100));
    out.println("#");
    out.println("# =============================================================================");
  }

  // ............................................................................

  public static final void printWarehouseInfosDiff(final PrintStream out, final WarehouseInfos warehouseInfos1,
      final WarehouseInfos warehouseInfos2) {
    out.println("# =============================================================================");
    out.println("# -- DIFFS --------------------------------------------------------------------");
    out.println("#");
    out.println(String.format("# %-16s | %-14s | %-14s |", "Empty", "Containers", "Slots"));
    out.println(String.format("# %16s | %14s | %14s |", "", "", ""));
    for (final ContainerType ct : ContainerType.values()) {
      out.println(String.format("#       %10s | %5d %8s | %5d %8s |", ct, //
          warehouseInfos2.emptyContainers_[ct.ordinal() + 1], //
          Main.fs(warehouseInfos2.emptyContainers_[ct.ordinal() + 1] - warehouseInfos1.emptyContainers_[ct.ordinal() + 1]), //
          warehouseInfos2.emptySlots_[ct.ordinal() + 1], //
          Main.fs(warehouseInfos2.emptySlots_[ct.ordinal() + 1] - warehouseInfos1.emptySlots_[ct.ordinal() + 1])));
    }
    out.println(String.format("# %16s | %14s | %14s |", "", "", ""));
    out.println(String.format("#       %10s | %5d %8s | %5d %8s |", "[TOTAL]", //
        warehouseInfos2.emptyContainers_[0], //
        Main.fs(warehouseInfos2.emptyContainers_[0] - warehouseInfos1.emptyContainers_[0]), //
        warehouseInfos2.emptySlots_[0], //
        Main.fs(warehouseInfos2.emptySlots_[0] - warehouseInfos1.emptySlots_[0])));
    out.println("#");
    out.println("# ==> SCORE: " + Main.getScore(warehouseInfos1, warehouseInfos2) + "   (exclusive upload-time)");
    out.println("#");
    out.println("# =============================================================================");
  }

  private static int getScore(final WarehouseInfos wi1, final WarehouseInfos wi2) {
    int score = 0;
    for (final ContainerType type : ContainerType.values()) {
      score += ((wi2.emptySlots_[type.ordinal() + 1] - wi1.emptySlots_[type.ordinal() + 1]) * (4 / type.getNumberOfSlots()));
    }
    score += 2 * (wi2.emptyContainers_[0] - wi1.emptyContainers_[0]);
    return score;
  }

  // ----------------------------------------------------------------------------

  private static String fs(final int value) {
    if (value <= 0) {
      return "(" + value + ")";
    }
    return "(+" + value + ")";
  }

  // ............................................................................
}
