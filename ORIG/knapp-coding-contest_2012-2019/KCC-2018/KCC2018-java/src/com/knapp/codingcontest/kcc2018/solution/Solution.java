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

package com.knapp.codingcontest.kcc2018.solution;

import java.util.Collection;
import java.util.List;

import com.knapp.codingcontest.kcc2018.data.Container;
import com.knapp.codingcontest.kcc2018.data.Institute;
import com.knapp.codingcontest.kcc2018.data.Order;
import com.knapp.codingcontest.kcc2018.warehouse.Shuttle;
import com.knapp.codingcontest.kcc2018.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2018.warehouse.WorkStation;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Aisle;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Location;
import com.knapp.codingcontest.kcc2018.warehouse.aisle.Position;

/**
 * This is the code YOU have to provide
 *
 * @param warehouse all the operations you should need
 */
public class Solution {
  /**
   * TODO: Your name
   */
  public static final String PARTICIPANT_NAME = ;

  /**
   * TODO: The Id of your institute - please refer to the handout
   */
  public static final Institute PARTICIPANT_INSTITUTION = ;

  // ----------------------------------------------------------------------------

  private final Warehouse warehouse;

  // ----------------------------------------------------------------------------

  public Solution(final Warehouse warehouse) {
    this.warehouse = warehouse;
  }

  // ----------------------------------------------------------------------------

  public void runWarehouseOperations() {
    System.out.println("### Your output starts here");

    //  ==> CODE YOUR SOLUTION HERE !!!");

    System.out.println("### Your output stops here");

    System.out.println("");
    System.out.println(String.format("--> Total operation cost        : %10d", warehouse.getCurrentOperationsCost()));
    System.out.println(String.format("--> Total unfinished order cost : %10d", warehouse.getCurrentUnfinishedOrdersCost()));
    System.out.println(String.format("--> Total cleanup cost          : %10d", warehouse.getCurrentCleanupCost()));
    System.out.println(String.format("                                  ------------"));
    System.out.println(String.format("==> TOTAL COST                  : %10d", warehouse.getCurrentTotalCost()));
    System.out.println(String.format("                                  ============"));
  }

  // ----------------------------------------------------------------------------

  private void apis() {
    // collaborators
    final Shuttle shuttle = warehouse.getShuttle();
    final WorkStation workStation = warehouse.getWorkStation();

    // information
    final Warehouse.Characteristics c = warehouse.getCharacteristics();
    final int numberOfAisles = c.getNumberOfAisles();
    final int numberOfPositionsPerAisle = c.getNumberOfPositionsPerAisle();
    final int locationDepth = c.getLocationDepth();

    // information that change with operations! (orders/containers/locations/...)
    final Collection<Location> locations = warehouse.getAisle(0).getLocations();
    final Location location = warehouse.getAisle(0).getLocation(0, Aisle.Side.Left);

    final List<Order> orders = warehouse.getOrders();
    final Collection<Container> allContainers = warehouse.getAllContainers();

    final long currentTotalCost = warehouse.getCurrentTotalCost();

    final Container loadedContainer = shuttle.getLoadedContainer();
    final Position currentPosition = shuttle.getCurrentPosition();
    final boolean isAtWorkStation = shuttle.isAtWorkStation();

    //
    // operations
    shuttle.moveToPosition(workStation);
    shuttle.moveToPosition(location.getPosition());
    final Container expected = location.getContainers().get(0);
    shuttle.loadFrom(location, expected);

    final Order order = null;
    workStation.pickOrder(order);

    shuttle.storeTo(location);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
