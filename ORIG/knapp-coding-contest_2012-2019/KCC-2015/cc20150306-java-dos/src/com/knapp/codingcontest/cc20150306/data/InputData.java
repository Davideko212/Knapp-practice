/* -*- java -*- ************************************************************************** *
 *
 *            Copyright (C) Knapp Logistik Automation GmbH
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.cc20150306.data;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 *  Container class for all input into the solution
 */
public class InputData {
  // ----------------------------------------------------------------------------

  private final String dataPath;

  private final ZoneCollection zoneCollection = new ZoneCollection();
  private final ProductCollection productCollection = new ProductCollection();
  private final PickOrderCollection pickOrderCollection = new PickOrderCollection();

  // ----------------------------------------------------------------------------

  public InputData(final String dataPath) {
    this.dataPath = dataPath;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "InputData@" + dataPath + "[" + zoneCollection + ",\n " + productCollection + ",\n " + pickOrderCollection + "]";
  }

  // ----------------------------------------------------------------------------

  public void readData() throws IOException {
    readZones();
    readProducts();
    readOrders();
  }

  // ----------------------------------------------------------------------------

  /**
   * Container for all zones in the warehouse
   * (and contained therein all shelves and locations)
   */
  public ZoneCollection getZoneCollection() {
    return zoneCollection;
  }

  /**
   * Container for all products that have to be slotted (assigned to locations)
   */
  public ProductCollection getProductCollection() {
    return productCollection;
  }

  /**
   * Container for all pickorders that will be picked out of the warehouse
   * U do not need to implement picking, this will be done by KNAPP during evaluation
   */
  public PickOrderCollection getOrderCollection() {
    return pickOrderCollection;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void readZones() throws IOException {
    {
      final Reader fr = new FileReader(fullFileName("zones.csv"));
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(fr);
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
          // code;cool;frozen;valuable;cost/loc;cost/shelf
          final String[] columns = splitCsv(line);
          final String code = columns[0];
          final boolean cool = Boolean.parseBoolean(columns[1]);
          final boolean frozen = Boolean.parseBoolean(columns[2]);
          final boolean valuable = Boolean.parseBoolean(columns[3]);
          final int costPerLocation = Integer.parseInt(columns[4]);
          final int costPerShelf = Integer.parseInt(columns[5]);
          zoneCollection.add(new Zone(code, cool, frozen, valuable, costPerShelf, costPerLocation));
        }
      } finally {
        close(reader);
        close(fr);
      }
    }

    {
      final Reader fr = new FileReader(fullFileName("locations.csv"));
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(fr);
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
          // zone;shelf;location
          final String[] columns = splitCsv(line);
          final String zone = columns[0];
          final String shelf_ = columns[1];
          final String location_ = columns[2];
          zoneCollection.findZone(zone).addLocation(shelf_, location_);
        }
      } finally {
        close(reader);
        close(fr);
      }
    }
  }

  private void readProducts() throws IOException {
    final Reader fr = new FileReader(fullFileName("products.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        // code;cool;frozen;valuable
        final String[] columns = splitCsv(line);
        final String code = columns[0];
        final boolean cool = Boolean.parseBoolean(columns[1]);
        final boolean frozen = Boolean.parseBoolean(columns[2]);
        final boolean valuable = Boolean.parseBoolean(columns[3]);
        productCollection.add(new Product(code, cool, frozen, valuable));
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  private void readOrders() throws IOException {
    final Map<String, PickOrder> orders_ = new HashMap<String, PickOrder>();
    final Reader fr = new FileReader(fullFileName("pickorders.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        // order;product
        final String[] columns = splitCsv(line);
        final String order = columns[0];
        final String product = columns[1];
        if (!orders_.containsKey(order)) {
          orders_.put(order, new PickOrder(order));
        }
        orders_.get(order).addLine(new PickOrderLine(product));
      }
    } finally {
      close(reader);
      close(fr);
    }
    for (final PickOrder order : orders_.values()) {
      pickOrderCollection.add(order);
    }
  }

  // ----------------------------------------------------------------------------

  private File fullFileName(final String fileName) {
    final String fullFileName = dataPath + File.separator + fileName;
    return new File(fullFileName);
  }

  private void close(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (final IOException exception) {
        exception.printStackTrace(System.err);
      }
    }
  }

  // ----------------------------------------------------------------------------

  private String[] splitCsv(final String line) {
    return line.split(";");
  }
}
