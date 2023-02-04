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

package com.knapp.codingcontest.kcc2019;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.knapp.codingcontest.kcc2019.core.PrepareUpload;
import com.knapp.codingcontest.kcc2019.core.Scheduler;
import com.knapp.codingcontest.kcc2019.core.WarehouseInternal;
import com.knapp.codingcontest.kcc2019.core.WarehouseOperations;
import com.knapp.codingcontest.kcc2019.data.InputData;
import com.knapp.codingcontest.kcc2019.data.Order;
import com.knapp.codingcontest.kcc2019.data.OrderLine;
import com.knapp.codingcontest.kcc2019.data.WarehouseCharacteristics;
import com.knapp.codingcontest.kcc2019.solution.Solution;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseStatistics;

/**
 * ----------------------------------------------------------------------------
 * you may change any code you like
 *   => but changing the output may lead to invalid results!
 * ----------------------------------------------------------------------------
 */
public class Main {
  public static void main(final String... args) throws Exception {
    System.out.println("##################################################");
    System.out.println("###   KNAPP Coding Contest 2019: STARTING...   ###");
    System.out.println("##################################################");

    System.out.println("");
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... LOADING INPUT ...");
    final InputData input = new InputData();
    input.readData();
    final WarehouseCharacteristics characteristics = input.getWarehouseCharacteristics();
    System.out.println("");
    System.out.println(Main.buildInputStatistics(input));
    System.out.println("");
    System.out.println(characteristics);

    System.out.println("");
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... RUN YOUR SOLUTION ...");
    final long start = System.currentTimeMillis();
    final WarehouseInternal warehouse = new WarehouseInternal(input);
    final Scheduler scheduler = new Scheduler(warehouse, input);
    final Solution solution = new Solution(warehouse, input);
    scheduler.run(solution);
    final long end = System.currentTimeMillis();
    System.out.println("");
    System.out.println("# ... DONE ... (" + (end - start) + "ms)");

    System.out.println("");
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... RESULT/COSTS FOR YOUR SOLUTION ...");
    System.out.println("#     " + solution.getParticipantName() + " / " + solution.getParticipantInstitution());

    System.out.println("");
    final WarehouseOperations warehouseStatistics = warehouse.getStatistics();
    final int[] unfinishedOrders = warehouseStatistics.getCurrentUnfinishedOrders();

    System.out.println(String.format("--> operation cost (details)  : %10d   %s", //
        warehouseStatistics.getCurrentOperationsCost(), //
        Main.format(characteristics, warehouseStatistics.getCurrentOperationsDetails(), (unfinishedOrders[0] > 0))));
    System.out.println(String.format("--> route sequence error cost : %10d   (# %d)", //
        warehouseStatistics.getCurrentRouteSequenceErrorCost(), //
        warehouseStatistics.getCurrentRouteSequenceErrors()));
    System.out.println(String.format("--> unfinished order cost     : %10d   (#%4d/%5d/%6d)       %s", //
        warehouseStatistics.getCurrentUnfinishedOrdersCost(), //
        unfinishedOrders[0], unfinishedOrders[1], unfinishedOrders[2], //
        (warehouseStatistics.getCurrentUnfinishedOrdersCost() == 0 ? "" : //
            String.format("(= %d + (%d * %d))", characteristics.getCostUnfinishedOrdersPenalty(), //
                unfinishedOrders[2], characteristics.getCostPerUnfinishedItem()))));
    System.out.println(String.format("                                ------------"));
    if ((unfinishedOrders[0] > 0) && (warehouseStatistics.getCurrentTotalCost() != warehouseStatistics._getCurrentTotalCost())) {
      System.out.println(String.format("==> TOTAL COST                : %10d => actual cost: %d (due to unfinished orders)", //
          warehouseStatistics.getCurrentTotalCost(), warehouseStatistics._getCurrentTotalCost()));
    } else {
      System.out.println(String.format("==> TOTAL COST                : %10d", //
          warehouseStatistics.getCurrentTotalCost()));
    }
    System.out.println(String.format("                                ============"));

    System.out.println("");
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... WRITING OUTPUT/RESULT ...");
    PrepareUpload.createZipFile(warehouse, solution);
    System.out.println("");
    System.out
        .println(">>> Created " + PrepareUpload.FILENAME_WAREHOUSE_OPERATIONS + " & " + PrepareUpload.FILENAME_UPLOAD_ZIP);

    System.out.println("");
    System.out.println("##################################################");
    System.out.println("###   KNAPP Coding Contest 2019: FINISHED      ###");
    System.out.println("##################################################");
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private static String buildInputStatistics(final InputData input) {
    final StringBuilder sb = new StringBuilder();
    final int[] counts = Main.countOLI(input.getOrders());
    sb.append("InputStatistics[\n");
    sb.append("    #orders                 = ").append(counts[0]).append("\n");
    sb.append("    #order-lines            = ").append(counts[1]).append("\n");
    sb.append("    #items                  = ").append(counts[2]).append("\n");
    sb.append("   (#products               = ").append(counts[3]).append(")\n");
    sb.append("\n");
    sb.append("    routes (#order/line/item):\n");
    for (final Map.Entry<Integer, int[]> roc : Main.countROC(input.getOrders())) {
      final int[] v = roc.getValue();
      sb.append(String.format("        %4d                = %4d / %5d / %6d", roc.getKey(), v[0], v[1], v[2])).append("\n");
    }
    sb.append("]");
    return sb.toString();
  }

  private static int[] countOLI(final List<Order> orders) {
    final int[] counts = { 0, 0, 0, 0 };
    final Set<String> p = new HashSet<String>();
    for (final Order o : orders) {
      counts[0]++;
      for (final OrderLine l : o.getOrderLines()) {
        counts[1]++;
        counts[2] += l.getRequestedQuantity();
        p.add(l.getProductCode());
      }
    }
    counts[3] = p.size();
    return counts;
  }

  private static Set<Map.Entry<Integer, int[]>> countROC(final List<Order> orders) {
    final TreeMap<Integer, int[]> roc = new TreeMap<Integer, int[]>();
    for (final Order o : orders) {
      final Integer routeDeparture = Integer.valueOf(o.getRouteDeparture());
      int[] oc = roc.get(routeDeparture);
      if (oc == null) {
        oc = new int[] { 0, 0, 0 };
        roc.put(routeDeparture, oc);
      }
      oc[0]++;
      for (final OrderLine l : o.getOrderLines()) {
        oc[1]++;
        oc[2] += l.getRequestedQuantity();
      }
    }
    return roc.entrySet();
  }

  // ............................................................................

  private static String format(final WarehouseCharacteristics characteristics,
      final Map<WarehouseStatistics.Operation, long[]> operationsDetails, final boolean haveUnfinishedOrders) {
    final StringBuilder sb = new StringBuilder();
    sb.append("(");
    boolean first = true;
    for (final Map.Entry<WarehouseStatistics.Operation, long[]> entry : operationsDetails.entrySet()) {
      if (!first) {
        sb.append("\n                                              ");
      }
      first = false;
      final long[] oc = entry.getValue();
      if ((entry.getKey() == WarehouseStatistics.Operation.StartTick) && haveUnfinishedOrders
          && (oc[0] != characteristics.getMaxTickCount())) {
        sb.append(String.format("%-16s = %6d (# %d) => actual cost: %d (due to unfinished orders)", entry.getKey(), oc[1],
            oc[0], (characteristics.getMaxTickCount() * characteristics.getCostPerTick())));
      } else {
        sb.append(String.format("%-16s = %6d (# %d)", entry.getKey(), oc[1], oc[0]));
      }
    }
    sb.append(")");
    return sb.toString();
  }

  // ============================================================================
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // ----------------------------------------------------------------------------
  // ............................................................................
  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
}
