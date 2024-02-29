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

import java.util.*;

/**
 * This is the code YOU have to provide
 */
public class Solution_2294538 {
    public String getParticipantName() {
        return "David Koch";
    }

    public Institute getParticipantInstitution() {
        return Institute.HTL_Rennweg_Wien;
    }

    // ----------------------------------------------------------------------------

    protected final InputData input;
    protected final Warehouse warehouse;

    // ----------------------------------------------------------------------------

    public Solution_2294538(final Warehouse warehouse, final InputData input) {
        this.input = input;
        this.warehouse = warehouse;
        // TODO: prepare data structures

    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point
     */
    public void run() throws Exception {
        List<Packet> packets = input.getPackets();
        Set<PalletType> palletTypes = input.getPalletTypes();
        List<PalletType> palletTypeList = palletTypes.stream().toList();
        Map<Integer, List<Pallet>> truckPallets = new HashMap<>();

        outer:
        while(!packets.isEmpty()) {
            //System.out.println(packets.size());
            Packet cPacket = packets.remove(0);
            int truck = cPacket.getTruckId();
            Pallet current;

            if (!truckPallets.containsKey(truck)) {
                current = warehouse.preparePallet(truck, fittingType(cPacket));
                List<Pallet> newList = new ArrayList<>();
                newList.add(current);
                truckPallets.put(truck, newList);
            } else {
                current = truckPallets.get(truck).get(truckPallets.get(truck).size()-1);
                if (current.getCurrentStackedHeight() == 10
                        || (current.getCurrentWeight() + cPacket.getWeight()) > current.getType().getMaxWeight()
                        || cPacket.getWidth() > 8
                        || cPacket.getLength() > 10) {
                    current = warehouse.preparePallet(truck, fittingType(cPacket));
                    List<Pallet> temp = truckPallets.get(truck);
                    temp.add(current);
                    truckPallets.replace(truck, temp);
                }
            }

            for (int y = 0; y < current.getType().getLength(); y++) {
                for (int x = 0; x < current.getType().getWidth(); x++) {
                    try {
                        warehouse.putPacket(current, cPacket, x, y, false);
                        continue outer;
                    } catch (Exception ignored) {}
                }
            }
            for (int y = 0; y < current.getType().getLength(); y++) {
                for (int x = 0; x < current.getType().getWidth(); x++) {
                    try {
                        warehouse.putPacket(current, cPacket, x, y, true);
                        continue outer;
                    } catch (Exception ignored) {}
                }
            }

            // last ditch
            current = warehouse.preparePallet(truck, fittingType(cPacket));
            warehouse.putPacket(current, cPacket, 0, 0, false);
        }
    }

    public PalletType fittingType(Packet p) {
        Set<PalletType> palletTypes = input.getPalletTypes();
        List<PalletType> palletTypeList = palletTypes.stream().toList();

        if (p.getWidth() > 8 || p.getWeight() > 960) {
            return palletTypes.stream().filter(i -> Objects.equals(i.getId(), "Q10")).toList().get(0);
        }

        return palletTypeList.get(0);
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

    // ----------------------------------------------------------------------------
}
