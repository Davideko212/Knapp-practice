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
public class Solution_11814 {
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

    public Solution_11814(final InputData input, final Operations operations) {
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

        for (OrderLine or : orlist) {
            Warehouse bestWH = null;
            double lowestDist = Double.MAX_VALUE;

            for (Warehouse wh : warehouses) {
                if (!wh.hasStock(or.getProduct())) continue;
                double dist = wh.getPosition().calculateDistance(or.getCustomer().getPosition());
                if (dist < lowestDist) {
                    bestWH = wh;
                    lowestDist = dist;
                }
            }

            operations.ship(or, bestWH);
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));

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
