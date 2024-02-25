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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.knapp.codingcontest.data.*;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.InfoSnapshot;
import com.knapp.codingcontest.operations.Operations;

/**
 * This is the code YOU have to provide
 */
public class Solution {
    public String getParticipantName() {
        return "David Koch"; // TODO: return your name
    }

    public Institute getParticipantInstitution() {
        return Institute.HTL_Rennweg_Wien;
    }

    // ----------------------------------------------------------------------------

    protected final InputData input;
    protected final Operations operations;

    // ----------------------------------------------------------------------------

    public Solution(final InputData input, final Operations operations) {
        this.input = input;
        this.operations = operations;

        // TODO: prepare data structures (but may also be done in run() method below)
    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point.
     * <p>
     * calculation for shipments costs:
     * total_cost = sum{products per warehouse/customer} ((cost_base + (sum(size_products) * cost_size)) * distanz_warehouse_to_customer)
     * <p>
     * some hints:
     * - one shipments is: all products for one customer from one warehouse (will be handled and calculated automatically/internally)
     * - there are finite amounts of product stocks in the warehouses (stock will be adjusted automatically by using op.ship() method)
     * - not all warehouses have all products on stock - or stock might run out (may be checked via wh.hasStock())
     * <p>
     * optimization is possible along two factors:
     * - minimize warehouse/customer pairs (#shipments) - reduce cost_base impact on total costs
     * - minimize distances - shipments from closer warehouses are cheaper
     * <p>
     * some ideas for finding a better solution:
     * sometimes it might be beneficial to split an order to have most delivered from close warehouse and only some from farther
     * instead of trying to deliver everything from just one warehouse that is far away
     */
    public void run() throws Exception {
        List<OrderLine> orlist = input.getOrderLines();
        List<Warehouse> warehouses = input.getWarehouses();
        Map<Customer, List<OrderLine>> customerORs = new HashMap<>(); // map of customers and a list of all their orderlines
        Map<Customer, List<Warehouse>> customerWHs = new HashMap<>(); // map of customers and a list of their closest warehouses (desc)

        for (OrderLine or : orlist) {
            Customer c = or.getCustomer();
            if (customerORs.containsKey(c)) {
                List<OrderLine> update = customerORs.get(c);
                update.add(or);
                customerORs.replace(c, update);
            } else {
                List<OrderLine> newL = new ArrayList<>();
                newL.add(or);
                customerORs.put(c, newL);
            }
        }

        for (Customer c : customerORs.keySet()) {
            Map<Warehouse, Double> temp = new LinkedHashMap<>();

            for (Warehouse wh : warehouses) {
                double dist = c.getPosition().calculateDistance(wh.getPosition());
                temp.put(wh, dist);
            }
            temp = sortByValue(temp);

            List<Warehouse> whl = new ArrayList<>();
            for (Map.Entry<Warehouse, Double> entry : temp.entrySet()) {
                whl.add(entry.getKey());
            }
            customerWHs.put(c, whl);
        }

        // ACTUAL SOLUTION STARTS HERE
        for (Customer c : customerORs.keySet()) {
            List<OrderLine> orders = customerORs.get(c);

            int counter = 0;
            while (!orders.isEmpty() && counter < 100) {
                Map<Product, Integer> neededP = ORListtoProductMap(orders);
                List<Warehouse> totalWHs = customerWHs.get(c);
                List<Warehouse> treated = new ArrayList<>();
                double meanDistance = meanDistance(totalWHs, c);
                for (Warehouse wh : totalWHs) {
                    double dist = wh.getPosition().calculateDistance(c.getPosition());
                    if (dist <= (meanDistance - meanDistance/4.31)) {
                        treated.add(wh);
                    }
                }
                Warehouse epic = biggestProductOverlapWH(treated, neededP);

                for (OrderLine or : new ArrayList<>(orders)) {
                    try {
                        operations.ship(or, epic);
                        orders.remove(or);
                    } catch (Exception ignored) {}
                }
                counter++;
            }

            /*
            while (!orders.isEmpty()) {
                Map<Product, Integer> neededP = ORListtoProductMap(orders);
                Warehouse epic = biggestProductOverlapWH(customerWHs.get(c), neededP);

                for (OrderLine or : new ArrayList<>(orders)) {
                    try {
                        operations.ship(or, epic);
                        orders.remove(or);
                    } catch (Exception ignored) {}
                }
            }*/
        }
    }

    public static Map<Product, Integer> ORListtoProductMap(List<OrderLine> list) {
        // which products does the wh have, that the cust needs?
        Map<Product, Integer> ret = new HashMap<>();
        for (OrderLine innerOR : list) {
            if (ret.containsKey(innerOR.getProduct())) {
                ret.replace(innerOR.getProduct(), ret.get(innerOR.getProduct()) + 1);
            } else {
                ret.put(innerOR.getProduct(), 1);
            }
        }
        return ret;
    }

    public static int productOverlap(Map<Product, Integer> stock, Map<Product, Integer> needed) {
        Map<Product, Integer> temp = new HashMap<>(stock);
        temp.keySet().retainAll(needed.keySet());
        Set<Product> matches = temp.keySet();

        int sum = 0;
        for (Product p : matches) {
            int stockAmount = stock.get(p);
            int neededAmount = needed.get(p);

            sum += Math.min(stockAmount, neededAmount);
        }
        return sum;
    }

    public static Warehouse biggestProductOverlapWH(List<Warehouse> list, Map<Product, Integer> needed) {
        int biggestOverlap = 0;
        Warehouse overlapWH = null;
        for (Warehouse wh : list) {
            int overlap = productOverlap(wh.getCurrentStocks(), needed);
            if (overlap > biggestOverlap) {
                biggestOverlap = overlap;
                overlapWH = wh;
            }
        }
        return overlapWH;
    }

    public static double meanDistance(List<Warehouse> list, Customer c) {
        double sum = 0;
        for (Warehouse wh : list) {
            sum += wh.getPosition().calculateDistance(c.getPosition());
        }
        return sum / list.size();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    /**
     * Just for documentation purposes.
     * <p>
     * Method may be removed without any side-effects
     * <p>
     * divided into 4 sections
     *
     * <li><em>input methods</em>
     *
     * <li><em>main interaction methods</em>
     * - these methods are the ones that make (explicit) changes to the warehouse
     *
     * <li><em>information</em>
     * - information you might need for your solution
     *
     * <li><em>additional information</em>
     * - various other infos: statistics, information about (current) costs, ...
     */
    @SuppressWarnings("unused")
    private void apis() throws Exception {
        // ----- input -----

        final List<OrderLine> orderLines = input.getOrderLines();
        final List<Warehouse> warehouses = input.getWarehouses();

        final OrderLine orderLine = orderLines.get(0);
        final Warehouse warehouse = warehouses.get(0);

        // ----- main interaction methods -----

        operations.ship(orderLine, warehouse); // throws OrderLineAlreadyPackedException, NoStockInWarehouseException;

        // ----- information -----

        final boolean hasStock = warehouse.hasStock(orderLine.getProduct());
        final Map<Product, Integer> currentStocks = warehouse.getCurrentStocks();

        final double distance = orderLine.getCustomer().getPosition().calculateDistance(warehouse.getPosition());

        // ----- additional information -----

        final CostFactors costFactors = operations.getCostFactors();

        final InfoSnapshot info = operations.getInfoSnapshot();
        final int unfinishedOrderLineCount = info.getUnfinishedOrderLineCount();
        final double unfinishedOrderLinesCost = info.getUnfinishedOrderLinesCost();
        final double shipmentsCost = info.getShipmentsCost();
        final double totalCost = info.getTotalCost();
    }

    // ----------------------------------------------------------------------------
}
