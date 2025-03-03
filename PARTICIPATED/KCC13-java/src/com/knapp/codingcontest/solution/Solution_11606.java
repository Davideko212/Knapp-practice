/* -*- java -*-
# =========================================================================== #
#                                                                             #
#                         Copyright (C) KNAPP AG                              #
#                                                                             #
#       The copyright to the computer program(s) herein is the property       #
#       of Knapp.  The program(s) may be used   and/or copied only with       #
#       the  written permission of  Knapp  or in  accordance  with  the       #
#       terms and conditions stipulated in the agreement/contract under       #
#       which the program(s) have been supplied.                              #
#                                                                             #
# =========================================================================== #
*/

package com.knapp.codingcontest.solution;

import com.knapp.codingcontest.core.InputDataInternal;
import com.knapp.codingcontest.core.WarehouseInternal;
import com.knapp.codingcontest.data.*;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.InfoSnapshot;
import com.knapp.codingcontest.operations.InfoSnapshot.OperationType;
import com.knapp.codingcontest.operations.Warehouse;

import java.util.*;

/**
 * This is the code YOU have to provide
 */
public class Solution_11606 {
  public String getParticipantName() {
    return "David Koch";
  }

  public Institute getParticipantInstitution() {
    return Institute.HTL_Rennweg_Wien;
  }

  // ----------------------------------------------------------------------------

  protected final Warehouse warehouse;
  protected final InputData input;

  // ----------------------------------------------------------------------------

  public Solution_11606(final WarehouseInternal iwarehouse, final InputDataInternal iinput) {
    // TODO: prepare data structures (but may also be done in run() method below)
    warehouse = iwarehouse;
    input = iinput;
    if (getParticipantName() == null) {
      throw new IllegalArgumentException("let getParticipantName() return your name");
    }
    if (getParticipantInstitution() == null) {
      throw new IllegalArgumentException("let getParticipantInstitution() return yout institution");
    }
  }

  // ----------------------------------------------------------------------------

  /**
   * The main entry-point.
   *
   */
  public void run() throws Exception {
    ArrayList<Order> orders = new ArrayList<>(input.getAllOrders());
    Map<Bin, String> bins = new HashMap<>();
    for (Bin bin : warehouse.getAllBins()) {
      bins.put(bin, "");
    }
    Map<String, List<Shelf>> product_shelf_map = warehouse.getProductShelves();

    for (Order order : orders) {
      List<String> open_products = new ArrayList<>(order.getOpenProducts());
      List<Bin> order_bins = new ArrayList<>(warehouse.getBinsForOrder(order));

      while (!open_products.isEmpty()) {
        String currentProduct = open_products.removeFirst();
        double lowest_cost = 9999999;
        Shelf lowest_shelf = product_shelf_map.get(currentProduct).get(0);

        for (Shelf shelf : product_shelf_map.get(currentProduct)) {
          double cost = warehouse.calcCost(warehouse.getCurrentPosition(), shelf.getPosition());
          if (cost < lowest_cost) {
            lowest_cost = cost;
            lowest_shelf = shelf;
          }
        }

        warehouse.pickProduct(lowest_shelf, currentProduct);
        //if (order_bins.isEmpty()) {
          double lowest_cost_bin = 999999;
          Bin lowest_bin = bins.keySet().iterator().next();

          for (Bin bin : bins.keySet()) {
            if (!order_bins.contains(bin) && bins.get(bin).isEmpty()) {
              double cost = warehouse.calcCost(warehouse.getCurrentPosition(), bin.getPosition());
              if (cost < lowest_cost_bin) {
                lowest_cost_bin = cost;
                lowest_bin = bin;
              }
            }
          }
          
          order_bins.add(lowest_bin);
          warehouse.assignOrder(order, lowest_bin);
          warehouse.putProduct(lowest_bin);
        //}
      }
      warehouse.finishOrder(order);
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  /**
   * Just for documentation purposes.
   *
   * Method may be removed without any side-effects
   *   divided into these sections
   *
   *     <li><em>input methods</em>
   *
   *     <li><em>main interaction methods</em>
   *         - these methods are the ones that make (explicit) changes to the warehouse
   *
   *     <li><em>information</em>
   *         - information you might need for your solution
   *
   *     <li><em>additional information</em>
   *         - various other infos: statistics, information about (current) costs, ...
   *
   */
  @SuppressWarnings("unused")
  private void apis() throws Exception {
    // ----- input -----

    final Collection<Order> orders = input.getAllOrders();
    final List<Bin> bins = warehouse.getAllBins();
    final List<Shelf> shelves = warehouse.getAllShelves();

    //
    final Order order = orders.iterator().next();
    final String product = order.getOpenProducts().get(0);
    final Bin bin = bins.get(0);

    final Map<String, List<Shelf>> productShelves = warehouse.getProductShelves();
    final Shelf shelf = productShelves.get(product).iterator().next();

    // ----- main interaction methods -----

    warehouse.assignOrder(order, bin);
    warehouse.pickProduct(shelf, product);
    warehouse.putProduct(bin);
    warehouse.finishOrder(order);

    // ----- information -----

    final List<String> aps = order.getAllProducts();
    final List<String> ops = order.getOpenProducts();

    final boolean ofin = warehouse.isOrderFinished(order);

    final Position currentPos = warehouse.getCurrentPosition();
    final double costa = warehouse.calcCost(currentPos, shelf.getPosition());
    final double costb = warehouse.calcCost(shelf.getPosition(), bin.getPosition());

    final Order bo = warehouse.getOrderAssignedToBin(bin);
    final List<Bin> obins = warehouse.getBinsForOrder(bo);

    // ----- additional information -----

    final CostFactors costFactors = input.getCostFactors();

    final double cf_up = costFactors.getUnfinishedProductPenalty();
    final double cf_d = costFactors.getDistanceCost();
    final double cf_sc = costFactors.getSideChangeCost();

    final InfoSnapshot info = warehouse.getInfoSnapshot();

    final int up = info.getUnfinishedProductCount();
    final int oao = info.getOperationCount(OperationType.AssignOrder);
    final int opip = info.getOperationCount(OperationType.PickProduct);
    final int opup = info.getOperationCount(OperationType.PutProduct);
    final int oo = info.getOperationCount(OperationType.FinishOrder);

    final double d = info.getDistance();
    final double sc = info.getCountSideChange();

    final double c_uo = info.getUnfinishedOrdersCost();
    final double c_d = info.getDistanceCost();
    final double c_sc = info.getSideChangeCost();
    final double c_t = info.getTotalCost();
  }

  // ----------------------------------------------------------------------------
}
