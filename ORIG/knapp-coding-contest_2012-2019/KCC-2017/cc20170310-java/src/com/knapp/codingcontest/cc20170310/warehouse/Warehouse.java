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

package com.knapp.codingcontest.cc20170310.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.knapp.codingcontest.cc20170310.data.Container;
import com.knapp.codingcontest.cc20170310.data.ContainerSlot;
import com.knapp.codingcontest.cc20170310.data.InputData;
import com.knapp.codingcontest.cc20170310.data.Product;

public class Warehouse {
  // ----------------------------------------------------------------------------
  // -- DO NOT CHANGE THESE -----------------------------------------------------
  //    (your result will be evaluated on the server with these settings)

  public static final int WORK_STATION_CAPACITY = 5;
  public static final int CONTAINER_MAX_GET = 5000;

  //----------------------------------------------------------------------------

  final InputData input;
  //
  private final Map<String, Container> workStation;
  //
  int currentNumberOfGets = 0;
  int currentNumberOfMoves = 0;
  int currentNumberOfPuts = 0;
  private final List<WarehouseOperation> result = new LinkedList<WarehouseOperation>();

  private WarehouseInfos warehouseInfos;

  // ----------------------------------------------------------------------------

  public Warehouse(final InputData input) {
    this.input = input;
    workStation = new HashMap<String, Container>();
  }

  // ----------------------------------------------------------------------------
  // -- query & statistics operations

  public final List<Container> getAllContainers() {
    return input.getContainers();
  }

  public final List<Container> getContainersAtWorkStation() {
    final List<Container> containers = new ArrayList<Container>(Warehouse.WORK_STATION_CAPACITY);
    for (final Container c : workStation.values()) {
      containers.add(c);
    }
    return containers;
  }

  public final List<Container> getContainersInStorage() {
    final List<Container> containers = new ArrayList<Container>(getAllContainers());
    containers.removeAll(getContainersAtWorkStation());
    return containers;
  }

  public final int getRemainingMovesToWorkStation() {
    return Warehouse.CONTAINER_MAX_GET - currentNumberOfGets;
  }

  //
  public final WarehouseInfos buildWarehouseInfos() {
    if (warehouseInfos == null) {
      warehouseInfos = new WarehouseInfos(this);
    }
    return warehouseInfos;
  }

  // ----------------------------------------------------------------------------

  public final Iterable<WarehouseOperation> result() {
    return result;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public void moveToWorkStation(final Container container) throws IllegalArgumentException, ContainerNotInStorageException,
      WorkStationCapcityExceededException, ContainerMaxGetExceededException {
    if (container == null) {
      throw new IllegalArgumentException("container must not be <null>");
    }

    result.add(new WarehouseOperation.GetContainer(container));

    if (workStation.containsKey(container.getCode())) {
      throw new ContainerNotInStorageException("container is already at work-station: " + container);
    }

    if (workStation.size() >= Warehouse.WORK_STATION_CAPACITY) {
      throw new WorkStationCapcityExceededException("containers at workStation are limited to: "
          + Warehouse.WORK_STATION_CAPACITY + " =>  @workStation=" + workStation.values());
    }

    if (currentNumberOfGets >= Warehouse.CONTAINER_MAX_GET) {
      throw new ContainerMaxGetExceededException("gets are limited to: " + Warehouse.CONTAINER_MAX_GET);
    }

    currentNumberOfGets++;

    workStation.put(container.getCode(), container);
  }

  public void transferItems(final ContainerSlot source, final ContainerSlot destination, final int quantity)
      throws IllegalArgumentException, ContainerNotAtWorkStationException, SourceFillingExhaustedException,
      ProductMismatchException, DestinationFillingExceededException {
    warehouseInfos = null;
    if ((source == null) || (destination == null)) {
      throw new IllegalArgumentException("incomplete move (source or dest is <null>)");
    }
    final WarehouseOperation.MoveItems moveItems = new WarehouseOperation.MoveItems(source, destination, quantity);

    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity (must be > 0): " + moveItems);
    }
    if (source.isEmpty()) {
      throw new IllegalArgumentException("no products at source: " + moveItems);
    }

    if (!workStation.containsKey(moveItems.source.getContainer().getCode())) {
      throw new ContainerNotAtWorkStationException("source container not at workStation: " + moveItems + " =>  @workStation="
          + workStation.values());
    }
    if (!workStation.containsKey(moveItems.destination.getContainer().getCode())) {
      throw new ContainerNotAtWorkStationException("destination container not at workStation: " + moveItems
          + " =>  @workStation=" + workStation.values());
    }

    final Product sourceProduct = source.getProduct();
    final Product destinationProduct = destination.getProduct();

    if (source.getQuantity() < quantity) {
      throw new SourceFillingExhaustedException("can't take #" + quantity + " from source: " + moveItems);
    }
    if (!destination.isEmpty()) {
      if (sourceProduct != destinationProduct) {
        throw new ProductMismatchException("mismatching products: " + moveItems.toString());
      }
    }

    if ((destination.getQuantity() + quantity) > sourceProduct.getMaxQuantity(destination.getContainer().getType())) {
      throw new DestinationFillingExceededException("can't put #" + quantity + " to destination: " + moveItems
          + " => max/slot=" + sourceProduct.getMaxQuantity(destination.getContainer().getType()));
    }

    //
    source._setQuantity(source.getQuantity() - quantity);
    destination._setQuantity(destination.getQuantity() + quantity);
    destination._setProduct(sourceProduct);

    result.add(moveItems);
    currentNumberOfMoves++;
  }

  public void moveToStorage(final Container container) throws IllegalArgumentException, ContainerNotAtWorkStationException {
    if (container == null) {
      throw new IllegalArgumentException("container must not be <null>");
    }

    result.add(new WarehouseOperation.PutContainer(container));

    if (!workStation.containsKey(container.getCode())) {
      throw new ContainerNotAtWorkStationException("container not at workStation: " + container);
    }

    currentNumberOfPuts++;

    workStation.remove(container.getCode());
  }

  // ----------------------------------------------------------------------------
}
