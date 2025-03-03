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
import java.util.stream.Collectors;

/**
 * This is the code YOU have to provide
 */
public class Solution_3335 {
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

  public Solution_3335(final WarehouseInternal iwarehouse, final InputDataInternal iinput) {
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
    Map<String, Integer> occ = product_occurence(orders);
    String in_hand = "";

    orders = optimizeOrderList(orders);
    Collections.reverse(orders);

    while (!orders.isEmpty()) {
      Order order = orders.remove(0);

      List<String> open_products = new ArrayList<>(order.getOpenProducts());

      String currentProduct = "";
      Bin closestBin = null;
      while (!order.getOpenProducts().isEmpty()) {
        if (order.getOpenProducts().contains(in_hand)) {
          closestBin = findClosestBin(bins, warehouse.getBinsForOrder(order), warehouse.getCurrentPosition());
          warehouse.assignOrder(order, closestBin);
          warehouse.putProduct(closestBin);
        } else {
          currentProduct = findClosestProduct(order.getOpenProducts(), product_shelf_map, warehouse.getCurrentPosition());

          Shelf closestShelf = findClosestShelf(product_shelf_map.get(currentProduct), warehouse.getCurrentPosition());
          warehouse.pickProduct(closestShelf, currentProduct);
          in_hand = currentProduct;

          closestBin = findClosestBin(bins, warehouse.getBinsForOrder(order), warehouse.getCurrentPosition());
          warehouse.assignOrder(order, closestBin);
          warehouse.putProduct(closestBin);
        }
      }
      warehouse.finishOrder(order);
      if (orders.get(0).getAllProducts().contains(currentProduct)) {
        warehouse.assignOrder(orders.get(0), closestBin);
        warehouse.putProduct(closestBin);
      }

      // MFW NO LOOKAHEAD
      // :(
    }
  }

  private ArrayList<Order> optimizeOrderList(ArrayList<Order> orders) {
    if (orders.isEmpty()) {
      return orders;
    }

    ArrayList<Order> optimizedOrders = new ArrayList<>();
    optimizedOrders.add(orders.remove(0)); // Start with the first order

    while (!orders.isEmpty()) {
      Order lastOrder = optimizedOrders.get(optimizedOrders.size() - 1);
      Order nextOrder = findNextOrderWithMostSharedProducts(lastOrder, orders);

      optimizedOrders.add(nextOrder);
      orders.remove(nextOrder);
    }

    return optimizedOrders;
  }

  private Order findNextOrderWithMostSharedProducts(Order lastOrder, ArrayList<Order> orders) {
    Order bestOrder = null;
    int maxSharedProducts = -1;

    for (Order order : orders) {
      int sharedProducts = countSharedProducts(lastOrder, order);
      if (sharedProducts > maxSharedProducts) {
        maxSharedProducts = sharedProducts;
        bestOrder = order;
      }
    }

    return bestOrder;
  }

  private int countSharedProducts(Order order1, Order order2) {
    Set<String> products1 = new HashSet<>(order1.getOpenProducts());
    Set<String> products2 = new HashSet<>(order2.getOpenProducts());
    products1.retainAll(products2); // Intersection of the two sets
    return products1.size();
  }

  private String findClosestProduct(List<String> open_products, Map<String, List<Shelf>> product_shelf_map, Position currentPosition) {
    String closestProduct = null;
    double lowestCost = Double.MAX_VALUE;

    for (String product : open_products) {
      for (Shelf shelf : product_shelf_map.get(product)) {
        double cost = warehouse.calcCost(currentPosition, shelf.getPosition());
        if (cost < lowestCost) {
          lowestCost = cost;
          closestProduct = product;
        }
      }
    }
    return closestProduct;
  }

  private Shelf findClosestShelf(List<Shelf> shelves, Position currentPosition) {
    Shelf closestShelf = null;
    double lowestCost = Double.MAX_VALUE;

    for (Shelf shelf : shelves) {
      double cost = warehouse.calcCost(currentPosition, shelf.getPosition());
      if (cost < lowestCost) {
        lowestCost = cost;
        closestShelf = shelf;
      }
    }
    return closestShelf;
  }

  private Bin findClosestBin(Map<Bin, String> bins, List<Bin> order_bins, Position currentPosition) {
    Bin closestBin = null;
    double lowestCost = Double.MAX_VALUE;

    for (Bin bin : bins.keySet()) {
      if (!order_bins.contains(bin) && bins.get(bin).isEmpty()) {
        double cost = warehouse.calcCost(currentPosition, bin.getPosition());
        if (cost < lowestCost) {
          lowestCost = cost;
          closestBin = bin;
        }
      }
    }
    return closestBin;
  }

  public Map<String, Integer> product_occurence(List<Order> orders) {
    Map<String, Integer> ret = new HashMap<>();
    for (Order order : orders) {
      HashSet<String> products = new HashSet<>(order.getAllProducts());
      for (String product : products) {
        if (!ret.containsKey(product)) {
          ret.put(product, 1);
        } else {
          ret.put(product, ret.get(product) + 1);
        }
      }
    }
    return ret.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
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
