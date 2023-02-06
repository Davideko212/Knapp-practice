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

package com.knapp.codingcontest.kcc2021.solution;

import java.util.*;

import com.knapp.codingcontest.kcc2021.data.InputData;
import com.knapp.codingcontest.kcc2021.data.Institute;
import com.knapp.codingcontest.kcc2021.data.Packet;
import com.knapp.codingcontest.kcc2021.data.Pallet;
import com.knapp.codingcontest.kcc2021.data.Pallet.PacketPos;
import com.knapp.codingcontest.kcc2021.data.PalletType;
import com.knapp.codingcontest.kcc2021.warehouse.PalletExtendsViolatedException;
import com.knapp.codingcontest.kcc2021.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2021.warehouse.WarehouseInfo;

/**
 * This is the code YOU have to provide
 */
public class Solution {
    public String getParticipantName() {
        return "David Koch";
    }

    public Institute getParticipantInstitution() {
        return Institute.HTL_Rennweg_Wien;
    }

    protected final InputData input;
    protected final Warehouse warehouse;

    public Solution(final Warehouse warehouse, final InputData input) {
        this.input = input;
        this.warehouse = warehouse;
        // TODO: prepare data structures (what does that mean lol)

    }

    /**
     * The main entry-point
     */
    public void run() throws Exception {
        List<Packet> all = input.getPackets();
        all.sort(Comparator.comparingInt(i -> i.getLength() * i.getWidth()));
        Collections.reverse(all);
        Map<Integer, List<Packet>> packetMap = new HashMap<>();

        for (Packet packet : all) {
            if (packetMap.containsKey(packet.getTruckId())) {
                List<Packet> list = packetMap.get(packet.getTruckId());
                list.add(packet);
                packetMap.replace(packet.getTruckId(), list);
            } else {
                List<Packet> newList = new ArrayList<>();
                newList.add(packet);
                packetMap.put(packet.getTruckId(), newList);
            }
        }

        // Go through each truck and its assigned stuff
        for (Map.Entry<Integer, List<Packet>> entry : packetMap.entrySet()) {

            int truckId = entry.getKey();
            System.out.println(truckId);

            List<Packet> packets = entry.getValue();
            List<Pallet> pallets = new ArrayList<>();

            for (Packet packet : packets) {
                boolean placed = false;

                // Check if the truck has nothing on it yet
                if (pallets.isEmpty()) {
                    Pallet newPallet = warehouse.preparePallet(truckId, mostSuitableType(packet));
                    warehouse.putPacket(newPallet, packet, 0, 0, false);
                    pallets.add(newPallet);
                    continue;
                }

                // Check if the packet fits on an existing pallet of this truck (going through each layer)
                palletbreak:
                for (Pallet pallet : pallets) {
                    PalletType pt = pallet.getType();

                    // Check if adding the packet would exceed the pallet weight limit
                    if (pt.getMaxWeight() < pallet.getCurrentWeight() + packet.getWeight()) {
                        // Proceed with next pallet if so
                        continue;
                    }

                    // Go through each layer of the pallet
                    for (int l = 0; l < 10; l++) {
                        Pallet.Layer currentLayer = pallet.getLayer(l);

                        // See if the free area on the current layer even allows the current packet to be placed
                        int blockedcount = 0;
                        for (Packet packet1 : currentLayer.getPackets().values()) {
                            blockedcount += packet1.getLength()*packet1.getWidth();
                        }
                        if (pt.getLength()*pt.getWidth() < blockedcount + (packet.getLength()*packet.getWidth())) {
                            // Proceed with next layer if so
                            continue;
                        }

                        // See where there is free space on the layer
                        int[][] blocked = new int[pt.getWidth()][pt.getLength()];
                        for (Map.Entry<PacketPos, Packet> packetEntry : currentLayer.getPackets().entrySet()) {
                            PacketPos pos = packetEntry.getKey();
                            Packet packet1 = packetEntry.getValue();

                            for (int x = pos.getX(); x < pos.getX()+packet1.getLength(); x++) {
                                for (int y = pos.getY(); y < pos.getY()+packet1.getWidth(); y++) {
                                    blocked[y][x] = 1;
                                }
                            }
                        }

                        // Go through each possible position of the packet on the layer to see if it would fit
                        for (int x = 0; x <= (pt.getLength() - packet.getLength()); x++) {
                            for (int y = 0; y <= (pt.getWidth() - packet.getWidth()); y++) {
                                if (blocked[y][x] == 1) {
                                    continue;
                                }

                                //System.out.println("x: " + x);
                                //System.out.println("y: " + y);

                                // Try placing packet at every possible position
                                try {
                                    warehouse.putPacket(pallet, packet, x, y, false);
                                    placed = true;
                                    break palletbreak;
                                } catch (Exception e) {
                                    try {
                                        // DONT WORK
                                        warehouse.putPacket(pallet, packet, x, y, true);
                                        placed = true;
                                        break palletbreak;
                                    } catch (Exception ignored) {

                                    }
                                }
                            }
                        }
                    }
                }

                // When nothing else succeeds, make a new pallet
                if (!placed) {
                    Pallet newPallet = warehouse.preparePallet(truckId, mostSuitableType(packet));
                    warehouse.putPacket(newPallet, packet, 0, 0, false);
                    pallets.add(newPallet);
                }
            }

            //System.out.println(pallets.get(0));
            //System.out.println(pallets.get(1));
        }
    }

    private PalletType mostSuitableType(Packet pt) {
        return input.getPalletTypes().stream()
                .filter(i -> (i.getLength() >= pt.getLength()) && (i.getWidth() >= pt.getWidth()))
                .filter(i -> i.getMaxWeight() >= pt.getWeight())
                .sorted(Comparator.comparingInt(i -> i.getLength() * i.getWidth()))
                .toList()
                .get(0);
    }

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

        final PalletType palletType = input.getPalletTypes().iterator().next();
        final Packet packet = input.getPackets().iterator().next();

        // ----- main interaction methods -----

        final Pallet pallet = warehouse.preparePallet(packet.getTruckId(), palletType);

        final int x = 0;
        final int y = 0;
        final boolean rotated = false;
        warehouse.putPacket(pallet, packet, x, y, rotated);

        // ----- information -----
        final int csh = pallet.getCurrentStackedHeight();
        final int cw = pallet.getCurrentWeight();
        final Pallet.Layer layer = pallet.getLayer(0);
        final Map<PacketPos, Packet> lpackets = layer.getPackets();

        // ----- additional information -----
        final WarehouseInfo info = warehouse.getInfo();

        final long tc = info.getTotalCost();
        final long upc = info.getUnfinishedPacketsCost();
        final long pac = info.getPalletsAreaCost();
        final long pvuc = info.getPalletsVolumeUsedCost();

        final int up = info.getUnfinishedPacketCount();
        final long pc = info.getPalletCount();
        final long pa = info.getPalletsArea();
        final long pvu = info.getPalletsVolumeUsed();
    }
}
