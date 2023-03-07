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
        List<OrderLine> ols = input.getOrderLines();

        List<OrderLine> newL = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            for (OrderLine ol : ols) {
                if (Integer.parseInt(ol.getCustomer().getCode().substring(ol.getCustomer().getCode().length() - 4)) == i) {
                    newL.add(ol);
                }
            }
        }

        ols = newL;

        deliverLoop:
        for (OrderLine ol : ols) {
            Product p = ol.getProduct();

            Warehouse shortest = null;
            double shortestAmount = 0;
            for (Warehouse wh : input.getWarehouses()) {
                if (wh.hasStock(p)) {
                    shortest = wh;
                    shortestAmount = wh.getCurrentStocks().get(p);
                    break;
                }
            }

            for (Warehouse wh : input.getWarehouses()) {
                if (wh.hasStock(p)) {
                    assert shortest != null; // just so the ide doesnt cry --> should never happen
                    double currDist = ol.getCustomer().getPosition().calculateDistance(wh.getPosition());
                    if (currDist * p.getSize() <
                            ol.getCustomer().getPosition().calculateDistance(shortest.getPosition()) * p.getSize()) {
                        shortest = wh;
                    }
                }
            }

            operations.ship(ol, shortest);
        }

        /*
        Map<Customer, List<OrderLine>> custMap = new HashMap<>();
        for (OrderLine ol : ols) {
            Customer cm = ol.getCustomer();

            if (custMap.containsKey(cm)) {
                List<OrderLine> old = custMap.get(cm);
                old.add(ol);
                custMap.replace(cm, old);
            } else {
                custMap.put(cm, new ArrayList<>(Arrays.asList(ol)));
            }
        }

        HashMap<Product, List<OrderLine>> olMap = new HashMap<>();
        for (OrderLine ol : ols) {
            Product p = ol.getProduct();

            if (olMap.containsKey(p)) {
                List<OrderLine> old = olMap.get(p);
                old.add(ol);
                olMap.replace(p, old);
            } else {
                olMap.put(p, new ArrayList<>(Arrays.asList(ol)));
            }
        }

         */

        /*
        Map<Customer, Map<Product, Long>> amMap = new HashMap<>();
        for (Map.Entry<Customer, List<OrderLine>> entry : custMap.entrySet()) {
            Map<OrderLine, Long> temp = entry.getValue().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            amMap.
        }

         */

        /*
        for (Map.Entry<Customer, List<OrderLine>> entry : custMap.entrySet()) {
            Customer cm = entry.getKey();

            List<Product> products = new ArrayList<>();
            for (OrderLine ol : entry.getValue()) {
                products.add(ol.getProduct());
            }
            Map<Product, Long> prodMap = products.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            for (Map.Entry<Product, Long> ientry : prodMap.entrySet()) {
                Warehouse suited = input.getWarehouses().get(0);
                long needed = ientry.getValue();

                for (Warehouse wh : input.getWarehouses()) {
                    Map<Product, Integer> stock = wh.getCurrentStocks();
                    stock = sortByValue(stock);

                    long stocked = stock.get(ientry.getKey());

                    if (needed <= stocked) {
                        operations.ship(ol);
                    }
                }
            }
        }
        */




        /*
        Map<Warehouse, Integer> whCost = new HashMap<>();
        Map<Customer, List<Warehouse>> whMap = new HashMap<>();
        for (Warehouse wh : input.getWarehouses()) {
            Map<Product, Integer> stock = wh.getCurrentStocks();
            stock = sortByValue(stock);

            for (Product p : stock.keySet()) {
                if (wh.hasStock(p)) {
                    int stockAmount = stock.get(p);

                    for ()
                }
            }
        }

         */

        /*
        for (Customer cm : custMap.keySet()) {
            List<Warehouse> whs = input.getWarehouses();
            Map<Warehouse, Double> distMap = new HashMap<>();

            for (Warehouse wh : whs) {
                distMap.put(wh, cm.getPosition().calculateDistance(wh.getPosition()));
            }

            distMap = distMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            whMap.put(cm, new ArrayList<>(distMap.keySet()));
        }

         */

        /*
        // Get Map of customers and their ordered products
        for (OrderLine ol : ols) {
            Customer cm = ol.getCustomer();
            String code = cm.getCode();

            if (custMap.containsKey(code)) {
                List<Product> old = custMap.get(code);
                old.add(ol.getProduct());
                custMap.replace(code, old);
            } else {
                custMap.put(code, new ArrayList<>(Arrays.asList(ol.getProduct())));
            }
        }
        System.out.println(custMap.get("C0089"));

        HashMap<String, List<Product>> betterMap = new HashMap<>();
        for (Map.Entry<String, List<Product>> entry : custMap.entrySet()) {
            Map<Product, Long> occur = entry.getValue().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            List<Product> newL = new ArrayList<>();

            for (Map.Entry<Product, Long> ientry : occur.entrySet()) {
                for (int i = 0; i < ientry.getValue(); i++) {
                    newL.add(ientry.getKey());
                }
            }

            betterMap.put(entry.getKey(), newL);
        }
        custMap = betterMap;
        System.out.println(custMap.get("C0089"));



        // Get all customers
        /*
        for (OrderLine ol : ols) {
            Set<Cus>


            for (Warehouse wh : input.getWarehouses()) {
                if (wh.hasStock(p)) {
                    assert shortest != null; // just so the ide doesnt cry --> should never happen
                    if (ol.getCustomer().getPosition().calculateDistance(wh.getPosition()) <
                            ol.getCustomer().getPosition().calculateDistance(shortest.getPosition())) {
                        shortest = wh;
                    }
                }
            }

            operations.ship(ol, shortest);
        }

         */

        /*
        deliverLoop:
        for (OrderLine ol : ols) {
            Product p = ol.getProduct();
            
            Warehouse shortest = null;
            for (Warehouse wh : input.getWarehouses()) {
                if (wh.hasStock(p)) {
                    shortest = wh;
                    break;
                }
            }
            for (Warehouse wh : input.getWarehouses()) {
                if (wh.hasStock(p)) {
                    assert shortest != null; // just so the ide doesnt cry --> should never happen
                    if (ol.getCustomer().getPosition().calculateDistance(wh.getPosition()) <
                            ol.getCustomer().getPosition().calculateDistance(shortest.getPosition())) {
                        shortest = wh;
                    }
                }
            }

            operations.ship(ol, shortest);
        }

         */


        /*
        //HashMap<String, List<Warehouse>> map = new HashMap<>();
        // key: orderline
        HashMap<OrderLine, List<Warehouse>> map = new HashMap<>();
        List<OrderLine> ols = input.getOrderLines();
        //System.out.println(ols);


        for (OrderLine ol : ols) {
            // Make list of warehouses with ascending distance to customer
            Customer cm = ol.getCustomer();
            Map<Warehouse, Double> distance = new HashMap<>();

            for (Warehouse wh : input.getWarehouses()) {
                if (wh.hasStock(ol.getProduct())) {
                    distance.put(wh, cm.getPosition().calculateDistance(wh.getPosition()));
                }
            }
            distance = sortByValue(distance);

            List<Warehouse> whs = new ArrayList<>(distance.keySet());

            //map.put(cm.getCode(), whs);
            map.put(ol, whs);
        }

        // Deliver
        deliverLoop:
        for (OrderLine ol : ols) {

            //Customer cm = ol.getCustomer();
            //List<Warehouse> whs = map.get(cm.getCode());
            List<Warehouse> whs = map.get(ol);
            //System.out.println(whs.get(0).hasStock(ol.getProduct()));
            //System.out.println(whs);

            for (Warehouse wh : whs) {
                if (!wh.hasStock(ol.getProduct())) {
                    whs.remove(wh);
                    map.replace(ol, whs);
                }

                operations.ship(ol, wh);
                //System.out.println(wh.getCurrentStocks());

                continue deliverLoop;
            }
        }

         */
        }

        public static <K, V extends Comparable<? super V>>Map<K, V> sortByValue (Map < K, V > map){
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
        private void apis () throws Exception {
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
