/* -*- java -*- ************************************************************************** *
 *
 *                     Copyright (C) KNAPP AG
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.kcc2019.solution;

import java.util.*;
import java.util.stream.Collectors;

import com.knapp.codingcontest.kcc2019.data.*;
import com.knapp.codingcontest.kcc2019.warehouse.*;

/**
 * This is the code YOU have to provide
 */
public class Solution {
    public String getParticipantName() {
        return "David Koch";
    }

    public Institute getParticipantInstitution() {
        return Institute.HTL_Rennweg;
    }

    // ----------------------------------------------------------------------------

    private final InputData input;
    private final Warehouse warehouse;
    private List<Order> orders;
    private List<WarehouseLane> lanes;
    private Order currentOrder;
    private List<OrderLine> currentLines;
    int currentLane;
    int nextReleasing;
    int currentSmall;
    int currentLarge;


    // ----------------------------------------------------------------------------

    public Solution(final Warehouse warehouse, final InputData input) throws LaneOverflowException, InvalidQuantityException, MissingPrecedingFetchException, LaneNotAvailableException, OrderLineExceededException, ProductMismatchException, OnlyOneFetchPerTickAllowedException {
        this.input = input;
        this.warehouse = warehouse;
        this.orders = new ArrayList<>(input.getOrders());
        ArrayList<Order> temp = new ArrayList<>();

        while (!(this.orders.isEmpty())) {
            int biggest = this.orders.get(0).getOrderLines().size();
            int bindex = 0;
            for (int i = 1; i < this.orders.size(); i++) {
                int current = this.orders.get(i).getOrderLines().size();
                if (biggest < current) {
                    biggest = current;
                    bindex = i;
                }
            }
            temp.add(this.orders.remove(bindex));
        }
        this.orders = temp;

        this.lanes = new ArrayList<>(warehouse.getLanes());
        this.currentSmall = orders.size()-1;
        this.currentLarge = 0;
        this.currentOrder = orders.get(currentSmall);
        this.currentLines = currentOrder.getOrderLines();
        this.currentLane = 0;
        this.nextReleasing = 0;
    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point that is called (by scheduler) for each time increment
     * <p>
     * This will be called as long as there are open orders (limited by an upper bound - see getMaxTickCount() below)
     *
     * @param currentTick
     */

    private int modulo = 0;
    private int huch = 0;
    public void tick(final int currentTick) throws Exception {
        if (!(orders.isEmpty())) {
            //System.out.println(currentLines);
            //System.out.println(orders.size());
            //System.out.println("current: " + currentLane);
            //System.out.println("release: " + nextReleasing);
            //System.out.println(currentLines);

            if (currentLines.isEmpty()) {
                try {
                    warehouse.releaseLane(lanes.get(nextReleasing));
                    nextReleasing++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("mod: " + modulo);

                if ((modulo % 2) == 0) {
                    this.orders.remove(currentSmall);
                    if (!(orders.isEmpty())) {
                        this.currentOrder = orders.get(currentLarge);
                    }
                } else {
                    this.orders.remove(currentLarge);
                    currentSmall = orders.size()-1;
                    if (!(orders.isEmpty())) {
                        this.currentOrder = orders.get(currentSmall);
                    }
                }
                modulo++;

                this.currentLines = currentOrder.getOrderLines();

                currentLane++;
                if (nextReleasing == 10) {
                    nextReleasing -= 10;
                }
                if (currentLane == 10) {
                    currentLane -= 10;
                }
            }

            if (!(orders.isEmpty())) {
                OrderLine line = currentLines.get(0);
                warehouse.fetchProduct(line.getProductCode());

                try {
                    warehouse.pickToLane(lanes.get(currentLane), line, line.getRequestedQuantity());

                    currentLines.remove(0);
                } catch (Exception ignored) {
                }
            }
        } else {
           if (warehouse.getReleasingLane() == null) {
               huch++;
               warehouse.releaseLane(lanes.get(huch));
           }
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

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
     * divided into 3 sections
     *
     * <li><em>main interaction methods</em>
     * - these 5 methods are the ones that make (explicit) changes to the warehouse
     * (scheduler makes (implicit) changes while releasing a lane: state, current releasing lane)
     *
     * <li><em>information</em>
     * - information you might need for your solution: current releasing lane (if any); current state and occupation of buffer, lane;
     *
     *
     * <li><em>additional information</em>
     * - various other infos: statistics, information about (current) costs, ...
     */
    private void apis() throws Exception {
        //
        final String productCode = null;
        final WarehouseLane lane = null;
        final OrderLine orderLine = null;
        final int pickQuantity = 0;

        // ----- main interaction methods -----

        warehouse.fetchProduct(productCode); // MAX ONCE per tick
        warehouse.pickToLane(lane, orderLine, pickQuantity);
        warehouse.pickToBuffer(pickQuantity);
        warehouse.moveFromBuffer(lane, orderLine, pickQuantity);
        warehouse.releaseLane(lane);

        // ----- information -----

        // which lane is currently releasing/sorting - may be <null>
        final WarehouseLane rlane = warehouse.getReleasingLane();

        // infos for each lane
        final WarehouseLane lane0 = warehouse.getLanes().get(0);
        final WarehouseLane.State lstate = lane0.getState();
        final int lsize = lane0.getSize();
        final int lcq = lane0.getCurrentQuantity();
        final List<WarehouseLane.PickLine> lpls = lane0.getCurrentPickLines();
        final int sscost = lane0.getCurrentSortingTicks();

        // infos for buffer
        final WarehouseBuffer buffer = warehouse.getBuffer();
        final int bsize = buffer.getSize();
        final int bcq = buffer.getCurrentQuantity();
        final Map<String, Integer> bps = buffer.getCurrentProducts();

        // ----- additional information -----

        // current counters & costs
        final WarehouseStatistics statistics = warehouse.getStatistics();
        // Operation, [#, cost]
        final Map<WarehouseStatistics.Operation, long[]> currentOperationsDetails = statistics.getCurrentOperationsDetails();
        final long currentOperationsCost = statistics.getCurrentOperationsCost();
        final long currentUnfinishedOrdersCost = statistics.getCurrentUnfinishedOrdersCost();
        // { #orders, #lines, #quantity }
        final int[] currentUnfinishedOrders = statistics.getCurrentUnfinishedOrders();
        final int currentRouteSequenceErrors = statistics.getCurrentRouteSequenceErrors();
        final long currentRouteSequenceErrorCost = statistics.getCurrentRouteSequenceErrorCost();
        final long currentTotalCost = statistics.getCurrentTotalCost();

        // characteristics (warehouse layout, ticks/costs per operation)
        final WarehouseCharacteristics characteristics = input.getWarehouseCharacteristics();
        final int numberOfLanes = characteristics.getNumberOfLanes();
        final int laneSize = characteristics.getLaneSize();
        final int bufferSize = characteristics.getBufferSize();
        final int releaseLaneFixedTicks = characteristics.getReleaseLaneFixedTicks();
        final int releaseLaneItemsTicks = characteristics.getReleaseLaneItemsTicks();
        final int maxTickCount = characteristics.getMaxTickCount();
        final long costPerTick = characteristics.getCostPerTick();
        final long costFetchProduct = characteristics.getCostFetchProduct();
        final long costRouteSequenceError = characteristics.getCostRouteSequenceError();
        final long costUnfinishedOrdersPenalty = characteristics.getCostUnfinishedOrdersPenalty();
        final long costPerUnfinishedItem = characteristics.getCostPerUnfinishedItem();
    }

    // ----------------------------------------------------------------------------
}
