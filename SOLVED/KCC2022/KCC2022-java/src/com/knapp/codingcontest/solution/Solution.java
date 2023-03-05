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
import com.knapp.codingcontest.warehouse.ex.LocationLengthExceededException;
import com.knapp.codingcontest.warehouse.ex.OrderIncompleteException;
import com.knapp.codingcontest.warehouse.ex.RobotLengthExeededException;

import javax.swing.text.Position;

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
        Map<String, Long> count = remProd.stream().collect(Collectors.groupingByConcurrent(Product::getCode, Collectors.counting()));
        count = sortByValue(count);
        //System.out.println(count);



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
                for (Location loc : locs) {
                    if (!loc.getCurrentProducts().isEmpty()) {
                        robot.pullFrom(loc);
                        break;
                    }
                }
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

    /*
    public void run() throws Exception {
        HashMap<Product, List<Location>> map = new HashMap<>();
        List<Product> remProd = new ArrayList<>(warehouse.getRemainingProductsAtEntry());
        List<Product> current = new ArrayList<>(warehouse.nextOrder().getProducts());
        Storage storage = warehouse.getStorage();
        int storLevel = 0;
        int storPos = 0;
        List<Location> openedUp = new ArrayList<>();

        while (!warehouse.getRemainingProductsAtEntry().isEmpty()) {
            //System.out.println(a);
            //System.out.println(storPos);
            //System.out.println(current);
            if (current.isEmpty()) {
                current = new ArrayList<>(warehouse.nextOrder().getProducts());
            }

            // Check if top of entry stack is what we need
            //System.out.println(remProd.get(0));
            if (current.contains(remProd.get(0))) {
                Product p = null;
                for (int i = 0; i < current.size(); i++) {
                    if (current.get(i) == remProd.get(0)) {
                        p = current.remove(i);
                        break;
                    }
                }

                // If product doesnt fit on the robot, offload everything
                assert p != null;
                if (robot.getCurrentProducts().stream().mapToInt(Product::getLength).sum() + p.getLength() > robot.getLength()) {
                    while (!(robot.getCurrentProducts().isEmpty())) {
                        robot.pushTo(exitLocation);
                    }
                }

                robot.pullFrom(entryLocation);
                remProd.remove(0);
            } else {
                int count = 0;
                for (Product rem : remProd) {
                    if (current.contains(rem)) {
                        break;
                    }
                    count++;
                }
                //System.out.println(count);

                if (!(remProd.contains(current.get(0)))) {
                    Product p = current.remove(0);

                    List<Location> old = map.get(p);
                    Location loc = old.remove(0);

                    robot.pullFrom(loc);
                    openedUp.add(loc);

                    robot.pushTo(exitLocation);
                    map.replace(p, old);
                } else {
                    for (int i = 0; i < count; i++) {
                        robot.pullFrom(entryLocation);
                        Product removed = remProd.remove(0);

                        if (!openedUp.isEmpty()) {
                            Location loc = openedUp.remove(0);
                            //System.out.println(loc);
                            robot.pushTo(loc);
                            map.replace(removed, new ArrayList<>(Arrays.asList(loc)));
                        } else {
                            //System.out.println(robot.getCurrentProducts());
                            robot.pushTo(storage.getLocation(storLevel, storPos));

                            if (map.containsKey(removed)) {
                                List<Location> curLocation = map.get(removed);
                                curLocation.add(storage.getLocation(storLevel, storPos));
                                map.replace(removed, curLocation);
                            } else {
                                map.put(removed, new ArrayList<>(Arrays.asList(storage.getLocation(storLevel, storPos))));
                            }
                            storPos++;

                            if (storPos >= 900) {
                                storLevel++;
                                storPos = 0;
                            }
                        }
                    }
                }
            }

            while (!(robot.getCurrentProducts().isEmpty())) {
                robot.pushTo(exitLocation);
            }
        }
    }


     */

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
