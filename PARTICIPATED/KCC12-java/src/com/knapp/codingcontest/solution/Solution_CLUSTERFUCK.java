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

import com.knapp.codingcontest.data.InputData;
import com.knapp.codingcontest.data.Institute;
import com.knapp.codingcontest.data.Order;
import com.knapp.codingcontest.data.Product;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.InfoSnapshot;
import com.knapp.codingcontest.operations.InfoSnapshot.OperationType;
import com.knapp.codingcontest.operations.Warehouse;
import com.knapp.codingcontest.operations.WorkStation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the code YOU have to provide
 */
public class Solution_CLUSTERFUCK {
  public String getParticipantName() {
    return "David Koch";
  }

  public Institute getParticipantInstitution() {
    return Institute.HTL_Rennweg_Wien;
  }

  // ----------------------------------------------------------------------------

  protected final Warehouse warehouse;
  protected final WorkStation workStation;
  protected final InputData input;

  // ----------------------------------------------------------------------------

  public Solution_CLUSTERFUCK(final Warehouse warehouse, final InputData input) {
    this.warehouse = warehouse;
    workStation = warehouse.getWorkStation();
    this.input = input;

    // TODO: prepare data structures (but may also be done in run() method below)
  }

  // ----------------------------------------------------------------------------

  /**
   * The main entry-point.
   *
   */
  public void run() throws Exception {
    List<Order> allOrders = new ArrayList<>(input.getAllOrders());
    List<Order> singlePOrders = singleProductOrders(allOrders);
    Map<Product, Integer> map = productIntegerMap(singlePOrders);
    List<Product> singleProductsDesc = new ArrayList<>(map.keySet());

    for (Map.Entry<List<Product>, List<Order>> entry : productOrderOverlap(allOrders).entrySet()) {
      List<Product> products = new ArrayList<>(entry.getKey());
      List<Order> orders = new ArrayList<>(entry.getValue());

      while (!products.isEmpty()) {
        for (int i = 0; i < 3; i++) {
          try {
            workStation.assignProduct(products.getFirst());
          } catch (Exception e) {
            break;
          }
        }

        List<Product> toRemoveP = new ArrayList<>();
        List<Order> toRemoveO = new ArrayList<>();
        for (Product p : workStation.getAssignedProducts()) {
          for (Order o : orders) {
            try {
              workStation.startOrder(o);
            } catch (Exception ignored) {}
            try {
              workStation.pickOrder(o, p);
            } catch (Exception e) {
              toRemoveO.add(o);
            }
          }
          workStation.removeProduct(p);
          toRemoveP.add(p);
        }
        orders.removeAll(toRemoveO);
        products.removeAll(toRemoveP);
      }
    }
    System.out.println(workStation.getActiveOrders());

    for (Product p : singleProductsDesc) {
      workStation.assignProduct(p);

      List<Order> toRemove = new ArrayList<>();
      for (Order o : singlePOrders) {
        if (o.getAllProducts().get(0) == p) {
          workStation.startOrder(o);
          workStation.pickOrder(o, p);
          allOrders.remove(o);
          toRemove.add(o);
        }
      }
      singlePOrders.removeAll(toRemove);

      workStation.removeProduct(p);
    }

    while (!allOrders.isEmpty()) {
      Order current = allOrders.removeFirst();
      workStation.startOrder(current);
      List<Product> ordered = new ArrayList<>(current.getAllProducts());

      while (!ordered.isEmpty()) {
        List<Product> currentProds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
          try {
            currentProds.add(ordered.removeFirst());
          } catch (Exception e) {
            break;
          }
        }

        for (Product p : currentProds) {
          workStation.assignProduct(p);
          workStation.pickOrder(current, p);
          workStation.removeProduct(p);
        }
      }
    }
  }

  public Map<Product, Integer> productIntegerMap(List<Order> orders) {
    Map<Product, Integer> ret = new HashMap<>();
    for (Order o : orders) {
      for (Product p : o.getAllProducts()) {
        if (ret.containsKey(p)) {
          int temp = ret.get(p);
          temp++;
          ret.replace(p, temp);
        } else {
          ret.put(p, 1);
        }
      }
    }

    ret = sortMapByValueDescending(ret);
    return ret;
  }

  public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValueDescending(Map<K, V> map) {
    return map.entrySet()
            .stream()
            .sorted(Map.Entry.<K, V>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  public List<Order> singleProductOrders(List<Order> orders) {
    List<Order> ret = new ArrayList<>();

    for (Order o : orders) {
      if (o.getAllProducts().size() == 1) {
        ret.add(o);
      }
    }

    return ret;
  }

  public Map<List<Product>, List<Order>> productOrderOverlap(List<Order> orders) {
    Map<List<Product>, List<Order>> ret = new HashMap<>();

    for (Order o : orders) {
      List<Product> products = o.getAllProducts();
      if (ret.containsKey(products)) {
        List<Order> temp = ret.get(products);
        temp.add(o);
        ret.replace(products, temp);
      } else {
        List<Order> temp = new ArrayList<>();
        temp.add(o);
        ret.put(products, temp);
      }
    }

    return ret;
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

    final Order order = orders.iterator().next();
    final Product product = order.getOpenProducts().get(0);

    final WorkStation workStation = warehouse.getWorkStation();

    // ----- main interaction methods -----

    workStation.startOrder(order);
    workStation.assignProduct(product);
    workStation.removeProduct(product);
    workStation.pickOrder(order, product);

    // ----- information -----

    final List<Product> aps = order.getAllProducts();
    final List<Product> ops = order.getOpenProducts();
    final boolean ofin = order.isFinished();

    final int wsos = workStation.getOrderSlots();
    final int wsps = workStation.getProductSlots();

    final Set<Order> waaos = workStation.getActiveOrders();
    final Set<Product> wsaps = workStation.getAssignedProducts();

    // ----- additional information -----

    final CostFactors costFactors = input.getCostFactors();
    final double cf_pa = costFactors.getProductAssignmentCost();
    final double cf_up = costFactors.getUnfinishedProductPenalty();

    final InfoSnapshot info = warehouse.getInfoSnapshot();

    final int up = info.getUnfinishedProductCount();
    final int oso = info.getOperationCount(OperationType.StartOrder);
    final int oap = info.getOperationCount(OperationType.AssignProduct);
    final int orp = info.getOperationCount(OperationType.RemoveProduct);
    final int opo = info.getOperationCount(OperationType.PickOrder);

    final double c_uo = info.getUnfinishedOrdersCost();
    final double c_pa = info.getProductAssignmentCost();
    final double c_t = info.getTotalCost();
  }

  // ----------------------------------------------------------------------------
}
