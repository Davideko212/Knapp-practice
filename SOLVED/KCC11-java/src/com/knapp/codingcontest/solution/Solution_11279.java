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

import com.knapp.codingcontest.data.*;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.InfoSnapshot;
import com.knapp.codingcontest.operations.Operations;

import java.util.*;

/**
 * This is the code YOU have to provide
 */
public class Solution_11279 {
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

    public Solution_11279(final InputData input, final Operations operations) {
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

        // go through each orderline, check the customer, check if the customer has other orderlines containing products,
        // which can be shipped from the same warehouse as the current orderline
        for (OrderLine or : orlist) {
            Customer c = or.getCustomer();
            List<OrderLine> cOR = customerORs.get(c);
            List<Warehouse> cWH = customerWHs.get(c);

            for (Warehouse wh : cWH) {
                // which products does the wh have, that the cust needs?
                Map<Product, Integer> inStockNeeded = new HashMap<>();
                for (OrderLine innerOR : cOR) {
                    if (wh.hasStock(innerOR.getProduct())) {
                        if (inStockNeeded.containsKey(innerOR.getProduct())) {
                            inStockNeeded.replace(innerOR.getProduct(), inStockNeeded.get(innerOR.getProduct()) + 1);
                        } else {
                            inStockNeeded.put(innerOR.getProduct(), 1);
                        }
                    }
                }

                for (OrderLine stockedOR : cOR) {
                    if (inStockNeeded.containsKey(stockedOR.getProduct()) && inStockNeeded.get(stockedOR.getProduct()) > 0) {
                        try {
                            operations.ship(stockedOR, wh);
                        } catch (Exception e) {

                        }
                        //cOR.remove(stockedOR);
                        inStockNeeded.replace(stockedOR.getProduct(), inStockNeeded.get(stockedOR.getProduct()) - 1);
                    }
                }

                if (cOR.isEmpty()) break;
            }
        }
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
