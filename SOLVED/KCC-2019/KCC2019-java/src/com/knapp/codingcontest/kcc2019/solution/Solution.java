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

import com.knapp.codingcontest.kcc2019.data.*;
import com.knapp.codingcontest.kcc2019.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseBuffer;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseLane;
import com.knapp.codingcontest.kcc2019.warehouse.WarehouseStatistics;

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
    // K: ProductID
    // V: List of Orders starting with that Product
    private Map<String, List<Order>> poMap;
    private Map<String, Integer> paMap;

    // ----------------------------------------------------------------------------

    public Solution(final Warehouse warehouse, final InputData input) {
        this.input = input;
        this.warehouse = warehouse;
        this.poMap = new HashMap<>();
        this.paMap = new HashMap<>();

        List<Order> orders = input.getOrders();

        for (Order order : orders) {
            OrderLine ol = order.getOrderLines().get(0);
            String first = ol.getProductCode();

            if (poMap.containsKey(first)) {
                List<Order> currentPo = poMap.get(first);
                currentPo.add(order);
                poMap.replace(first, currentPo);

                int currentPa = paMap.get(first);
                currentPa += ol.getRequestedQuantity();
                paMap.replace(first, currentPa);
            } else {
                List<Order> newList = new ArrayList<>();
                newList.add(ol.getOrder());
                poMap.put(first, newList);
                paMap.put(first, ol.getRequestedQuantity());
            }
        }
        paMap = sortByValue(paMap);
        //System.out.println(poMap.get("PROD00001"));


    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point that is called (by scheduler) for each time increment
     * <p>
     * This will be called as long as there are open orders (limited by an upper bound - see getMaxTickCount() below)
     *
     * @param currentTick
     */
    public void tick(final int currentTick) throws Exception {
        //String product = new ArrayList<>(paMap.keySet()).get(0);
        //System.out.println(product);

        //poMap.get(product).get()

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
