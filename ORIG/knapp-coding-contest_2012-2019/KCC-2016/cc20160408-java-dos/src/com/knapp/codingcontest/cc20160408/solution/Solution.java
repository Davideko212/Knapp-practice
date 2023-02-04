package com.knapp.codingcontest.cc20160408.solution;

import java.util.List;

import com.knapp.codingcontest.cc20160408.Input;
import com.knapp.codingcontest.cc20160408.data.LocationCollection;
import com.knapp.codingcontest.cc20160408.data.PickOrderCollection;
import com.knapp.codingcontest.cc20160408.data.ProductCollection;
import com.knapp.codingcontest.cc20160408.entities.PickOrder;
import com.knapp.codingcontest.cc20160408.entities.ReplenishmentOrder;
import com.knapp.codingcontest.cc20160408.util.Contract;

public class Solution {
  /**
   * TODO: Your name
   */
  public final String participantName = null;

  /**
   * TODO: The Id of your institute - please refer to the handout
   */
  public final String instituteId = null;

  /**
   * local reference to the global location collection
   */
  private final LocationCollection locationCollection;

  /**
   * local reference to the global product collection
   */
  private final ProductCollection productCollection;

  /**
   * local reference to the global collection with unfulfilled pick-orders
   *
   * Note: the pickOrderCollection is always up to date when GetNextReplenishmentOrder is called
   */
  private final PickOrderCollection pickOrderCollection;

  // ----------------------------------------------------------------------------

  /**
   * Create the solution instance74
   *
   * Do all your preparations here
   *
   * @param input
   */
  public Solution(final Input input) {
    Contract.requires(input != null, "illegal argument");

    Contract.requires(input.getLocationCollection() != null, "illegal argument");
    Contract.requires(input.getLocationCollection().count() > 0, "illegal argument");

    Contract.requires(input.getProductCollection() != null, "illegal argument");
    Contract.requires(input.getProductCollection().count() > 0, "illegal argument");

    Contract.requires(input.getPickOrderCollection() != null, "illegal argument");
    Contract.requires(input.getPickOrderCollection().count() > 0, "illegal argument");

    Contract.requires(!Contract.isNullOrWhiteSpace(instituteId), "Please set InstituteId in Solution.java");
    Contract.requires(!Contract.isNullOrWhiteSpace(participantName), "Please set ParticipantName in Solution.java");

    //
    locationCollection = input.getLocationCollection();
    productCollection = input.getProductCollection();
    pickOrderCollection = input.getPickOrderCollection();

    // TODO: Your code goes here
  }

  // ----------------------------------------------------------------------------

  /**
   * return the next replen move for the caller to execute
   *
   * @return the next replen move for the caller to execute
   */
  public ReplenishmentOrder getNextReplenishmentOrder() {
    // The caller (KNAPP code) executes the replen and performs the next possible pick
    // If no replenishment order should be executed in this timeframe, return null

    // TODO: add your code here to select the next (best) replen move
    // TODO: and return it to the caller
    throw new UnsupportedOperationException(
        "TODO: return your created ReplenishmentOrder, or null if u do not want to do anything in this frame");
    // TODO: return your created ReplenishmentOrder, or null if u do not want to do anything in this frame
  }

  // ............................................................................

  // ----------------------------------------------------------------------------

  /**
   * This function is called after all picks have been performed by the framework.
   *
   * If necessary, you can handle it.
   * Note: the pickOrderCollection is also updated and always reflects the current state,
   *       which meansm the id's in pickedOrderIds can no longer be found in the pickOrderCollection
   *
   * @param pickedOrderIds read-only collection with the ids of the order that have been picked
   */
  public void handlePickedOrders(final List<PickOrder> pickedOrders) {
    Contract.requires(pickedOrders != null, "illegal argument");

    // TODO: Your code goes here - if needed
  }

  // ----------------------------------------------------------------------------
}
