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

import com.knapp.codingcontest.kcc2021.data.*;
import com.knapp.codingcontest.kcc2021.data.Pallet.PacketPos;
import com.knapp.codingcontest.kcc2021.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2021.warehouse.WarehouseInfo;

import java.util.Map;

/**
 * This is the code YOU have to provide
 */
public class Solution_OLD {
    public String getParticipantName() {
        return "David Koch"; // TODO: return your name
    }

    public Institute getParticipantInstitution() {
        return Institute.HTL_Rennweg_Wien; // TODO: return the Id of your institute - please refer to the hand-out
    }

    protected final InputData input;
    protected final Warehouse warehouse;

    public Solution_OLD(final Warehouse warehouse, final InputData input) {
        this.input = input;
        this.warehouse = warehouse;
        // TODO: prepare data structures

    }

    /**
     * The main entry-point
     */
    public void run() throws Exception {
        /*
        List<PalletType> ptAll = input.getPalletTypes().stream().toList();
        List<Packet> pAll = input.getPackets();

        for (Packet packet : pAll) {
            System.out.println(packet.getId());

            int truckId = packet.getTruckId();
            Pallet p;

            List<Pallet> curTruckPallets = warehouse.getPallets().stream()
                    .filter(i -> i.getTruckId() == truckId)
                    .toList();

            if (curTruckPallets.isEmpty()) {
                p = warehouse.preparePallet(truckId, mostSuitableType(packet));
                warehouse.putPacket(p, packet, 0, 0, false);
            } else {
                palletsearch:
                for (Pallet curTruckPallet : curTruckPallets) {
                    p = curTruckPallet;

                    if (p.getCurrentStackedHeight() == 10) {
                        p = warehouse.preparePallet(truckId, mostSuitableType(packet));
                    }

                    if ((p.getCurrentWeight() + packet.getWeight()) > p.getType().getMaxWeight()) {
                        p = warehouse.preparePallet(truckId, mostSuitableType(packet));
                    }

                    if (p.getType().getWidth() < packet.getWidth() || p.getType().getLength() < packet.getLength()) {
                        p = warehouse.preparePallet(truckId, mostSuitableType(packet));
                    }

                    PalletType pt = p.getType();

                    layersearch:
                    for (int l = 0; l < 10; l++) {
                        //System.out.println(l);

                        Map<PacketPos, Packet> map = p.getLayer(l).getPackets();
                        // 0 --> free field
                        int[][] free = new int[pt.getWidth()][pt.getLength()];
                        int blockedcount = 0;

                        for (Map.Entry<PacketPos, Packet> entry : map.entrySet()) {
                            for (int y = entry.getKey().getY(); y < entry.getKey().getY() + entry.getValue().getWidth(); y++) {
                                for (int x = entry.getKey().getX(); x < entry.getKey().getX() + entry.getValue().getLength(); x++) {
                                    free[y][x] = 1;
                                    blockedcount++;
                                }
                            }
                        }

                        // Seeing if there even is enough space on the current layer to fit the packet at all
                        if (blockedcount + (packet.getLength() * packet.getWidth()) > (pt.getLength() * pt.getWidth())) {
                            continue;
                        }

                        // Looking for a free space on the layer
                        outersearch:
                        for (int y1 = 0; y1 < pt.getWidth() - packet.getWidth(); y1++) {
                            for (int x1 = 0; x1 < pt.getLength() - packet.getLength(); x1++) {
                                if (free[y1][x1] == 0) {

                                    // Will the packet fit in the free space?
                                    innersearch:
                                    for (int y2 = y1; y2 < y1 + packet.getWidth(); y2++) {
                                        for (int x2 = x1; x2 < x1 + packet.getLength(); x2++) {
                                            if (free[y2][x2] != 0) {
                                                break innersearch;
                                            }
                                        }
                                    }

                                    //System.out.println("x1: " + x1 + " y1: " + y1);
                                    warehouse.putPacket(p, packet, x1, y1, false);
                                    break palletsearch;
                                    //break outersearch;
                                }
                            }
                        }
                    }
                }
            }
        }

         */
    }

    private PalletType mostSuitableType(Packet pt) {
        return input.getPalletTypes().stream()
                .filter(i -> (i.getLength() >= pt.getLength()) && (i.getWidth() >= pt.getWidth()))
                .filter(i -> i.getMaxWeight() >= pt.getWeight())
                .toList()
                .get(0);
    }

    /*
        Packet test = input.getPackets().get(0);

        List<PalletType> all = input.getPalletTypes().stream().toList();
        PalletType ptEuro = all.get(0);

        Pallet p = warehouse.preparePallet(test.getTruckId(), ptEuro);

        warehouse.putPacket(p, test, 0, 0, false);
    */

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
