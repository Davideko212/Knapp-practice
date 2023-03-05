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

import com.knapp.codingcontest.data.InputData;
import com.knapp.codingcontest.data.Institute;
import com.knapp.codingcontest.data.Location;
import com.knapp.codingcontest.data.Order;
import com.knapp.codingcontest.data.Product;
import com.knapp.codingcontest.warehouse.Robot;
import com.knapp.codingcontest.warehouse.Storage;
import com.knapp.codingcontest.warehouse.Warehouse;
import com.knapp.codingcontest.warehouse.WarehouseInfo;

/**
 * This is the code YOU have to provide
 */
public class Solution {
    public String getParticipantName() {
        return "David Koch"; // TODO: return your name
    }

    public Institute getParticipantInstitution() {
        return Institute.HTL_Rennweg_Wien; // TODO: return the Id of your institute - please refer to the hand-out
    }

    // ----------------------------------------------------------------------------

    protected final InputData input;
    protected final Warehouse warehouse;

    protected final Storage storage;
    protected final Location entryLocation;
    protected final Location exitLocation;
    protected final Robot robot;

    // ----------------------------------------------------------------------------

    public Solution(final Warehouse warehouse, final InputData input) {
        this.input = input;
        this.warehouse = warehouse;

        storage = warehouse.getStorage();
        entryLocation = storage.getEntryLocation();
        exitLocation = storage.getExitLocation();
        robot = storage.getRobot();

        // TODO: prepare data structures
    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point
     */

    public void run() throws Exception {
        HashMap<String, List<Location>> map = new HashMap<>();

        // Sorting all products in to storage
        List<Product> remProd = new ArrayList<>(warehouse.getRemainingProductsAtEntry());
        //Map<String, Long> count = remProd.stream().collect(Collectors.groupingByConcurrent(Product::getCode, Collectors.counting()));
        Map<Product, Long> count = remProd.stream().collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
        count = sortByValue(count);
        //System.out.println(count);
        /* TODO: make this WOOOOOORK
            List<Product> temp = new ArrayList<>();
            for (Map.Entry<Product, Long> entry : count.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    temp.add(entry.getKey());
                }
            }
            remProd = temp;
         */

        pullLoop:
        for (Product p : remProd) {
            String code = p.getCode();
            robot.pullFrom(entryLocation);

            if (map.containsKey(code) && (map.get(code).size() != 0)) {
                List<Location> current = map.get(code);
                Location pushLoc = null;

                for (Location loc : current) {
                    if (loc.getCurrentProducts().stream().mapToInt(Product::getLength).sum() + p.getLength() > loc.getLength()) {
                        continue;
                    }
                    pushLoc = loc;
                    break;
                }

                if (pushLoc == null) {
                    for (int i = 0; i < 1000; i++) {
                        for (int j = 0; j < 15; j++) {
                            Location loc = warehouse.getStorage().getLocation(j, i);

                            // Check if storage loc has nothing on it
                            if (loc.getCurrentProducts().size() == 0) {
                                List<Location> old = map.get(code);
                                old.add(loc);
                                map.replace(code, old);
                                robot.pushTo(loc);
                                continue pullLoop;
                            }
                        }
                    }
                } else {
                    robot.pushTo(pushLoc);
                    continue pullLoop;
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 1000; j++) {
                        Location loc = warehouse.getStorage().getLocation(i, j);

                        // Check if storage loc has nothing on it
                        if (loc.getCurrentProducts().size() == 0) {
                            map.put(code, new ArrayList<>(Arrays.asList(loc)));
                            robot.pushTo(loc);
                            continue pullLoop;
                        }
                    }
                }
            }
        }


        // Going through orders
        while (true) {
            Order order;
            try {
                order = warehouse.nextOrder();
            } catch (Exception e) {
                break;
            }

            // Going through all the products needed for the order
            for (Product need : order.getProducts()) {
                // Getting all storage locations containing the product
                List<Location> locs = map.get(need.getCode());

                // First storage location still containing the product gets chosen
                // also: try picking up (correct) products until its not possible anymore
                for (Location loc : locs) {
                    if (!loc.getCurrentProducts().isEmpty()) {
                        try {
                            robot.pullFrom(loc);
                        } catch (Exception e) {
                            while (!robot.getCurrentProducts().isEmpty()) {
                                robot.pushTo(exitLocation);
                            }
                            robot.pullFrom(loc);
                        }
                        break;
                    }
                }
            }

            while (!robot.getCurrentProducts().isEmpty()) {
                robot.pushTo(exitLocation);
            }
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

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

        final List<Product> allProductsAtEntry = input.getAllProductsAtEntry();
        final List<Order> allOrders = input.getAllOrders();

        final List<Product> remainingProducts = warehouse.getRemainingProductsAtEntry();
        final List<Order> remainingOrders = warehouse.getRemainingOrders();

        final Location location0 = storage.getLocation(0, 0);
        final List<Location> allLocations = storage.getAllLocations();

        // ----- main interaction methods -----

        Location location;

        location = entryLocation;
        robot.pullFrom(location);

        location = exitLocation;
        robot.pushTo(location);

        final Order order = warehouse.nextOrder();

        // ----- information -----

        final Product product = order.getProducts().get(0);

        location.getType();
        location.getLevel();
        location.getPosition();
        location.getLength();
        location.getRemainingLength();
        location.getCurrentProducts();

        final Location lamLocation = robot.getCurrentLocation();
        robot.getCurrentProducts();
        robot.getLength();
        robot.getRemainingLength();
        robot.getCurrentMaxWidth();

        // ----- additional information -----
        final WarehouseInfo info = warehouse.getInfoSnapshot();
    }

    // ----------------------------------------------------------------------------
}
