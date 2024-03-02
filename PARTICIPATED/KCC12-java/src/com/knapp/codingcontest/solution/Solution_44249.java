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
public class Solution_44249 {
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

  public Solution_44249(final Warehouse warehouse, final InputData input) {
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

    List<Order> newOrders = new ArrayList<>();
    newOrders.add(allOrders.removeFirst());
    while (!allOrders.isEmpty()) {
      Order current = newOrders.get(newOrders.size()-1);
      Order overlap = largestOverlap(current, allOrders);
      newOrders.add(overlap);
      allOrders.remove(overlap);
    }
    allOrders = newOrders;

    Order current = allOrders.removeFirst();
    Set<Product> needed = new HashSet<>(current.getAllProducts());
    workStation.startOrder(current);
    for (int i = 0; i < 3; i++) {
      try {
        workStation.assignProduct(current.getAllProducts().get(i));
      } catch (Exception e) {
        break;
      }
    }
    System.out.println("meow");
    int counter = 0;
    while (!allOrders.isEmpty()) {
      Set<Product> overlap = new HashSet<>(current.getOpenProducts());
      overlap.retainAll(workStation.getAssignedProducts());

      for (Product p : overlap) {
        /*try {
          for (int i = 0; i < Collections.frequency(current.getOpenProducts(), p); i++) {
            workStation.pickOrder(current, p);
            needed.remove(p);
          }
        } catch (Exception e) {
          System.out.println("fsdfsf");
          break;
        }*/
        /*for (int i = 0; i < Collections.frequency(current.getOpenProducts(), p); i++) {
          workStation.pickOrder(current, p);
        }*/
        for (int i = 0; i < Collections.frequency(current.getOpenProducts(), p); i++) {
          System.out.println(overlap);
          workStation.pickOrder(current, p);
        }
        needed.remove(p);
      }

      for (Product p : workStation.getAssignedProducts()) {
        for (int i = 0; i < 100; i++) {
          //System.out.println(overlap);
          try {
            workStation.pickOrder(current, p);
          } catch (Exception e) {
            break;
          }
        }
        needed.remove(p);
      }
      System.out.println(needed);
      System.out.println(workStation.getAssignedProducts());
      System.out.println(workStation.getActiveOrders());
      if (needed.isEmpty()) {
        System.out.println(allOrders.size());
        current = allOrders.removeFirst();
        workStation.startOrder(current);
        needed = new HashSet<>(current.getOpenProducts());
        continue;
      }

      for (Product p : new HashSet<>(workStation.getAssignedProducts())) {
        workStation.removeProduct(p);
      }

      List<Product> temp = new ArrayList<>(needed);
      for (int i = 0; i < 3; i++) {
        try {
          if (counter < 10) {
            workStation.assignProduct(current.getAllProducts().get(i));
          } else {
            workStation.assignProduct(temp.get(i));
          }
        } catch (Exception e) {
          break;
        }
      }
      counter++;
    }
  }

  public Order largestOverlap(Order order, List<Order> orders) {
    Set<Product> products = new HashSet<>(order.getAllProducts());

    if (!EXACToverlap(products, orders).isEmpty()) return EXACToverlap(products, orders).getFirst();

    float highscore = 0;
    Order holder = orders.get(0);
    for (Order o : orders) {
      Set<Product> oP = new HashSet<>(o.getAllProducts());
      oP.retainAll(products);
      float score = (float) products.size() / oP.size();
      if (score > highscore) {
        holder = o;
        highscore = score;
      }
    }

    return holder;
  }

  public List<Order> overlapWith4Prods(Set<Product> currentProds, List<Order> orders) {
    List<Order> ret = new ArrayList<>();
    for (Order o : orders) {
      if (o.getAllProducts().containsAll(currentProds)) {
        ret.add(o);
      }
    }
    return ret;
  }

  public List<Order> EXACToverlapWith4Prods(Set<Product> currentProds, List<Order> orders) {
    List<Order> ret = new ArrayList<>();
    for (Order o : orders) {
      if (o.getAllProducts().containsAll(currentProds) && (o.getAllProducts().size() == 4)) {
        ret.add(o);
      }
    }
    return ret;
  }

  public List<Order> EXACToverlap(Set<Product> currentProds, List<Order> orders) {
    List<Order> ret = new ArrayList<>();
    for (Order o : orders) {
      if (new HashSet<>(o.getAllProducts()).equals(currentProds)) {
        ret.add(o);
      }
    }
    return ret;
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
