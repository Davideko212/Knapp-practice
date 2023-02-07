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

package com.knapp.codingcontest.kcc2019.data;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class InputData {
  private static final String PATH_INPUT_DATA;
  static {
    try {
      PATH_INPUT_DATA = new File("./data").getCanonicalPath();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ----------------------------------------------------------------------------

  private final String dataPath;

  private final Set<String> productCodes = new TreeSet<String>();
  private final List<Order> orders = new LinkedList<Order>();
  private WarehouseCharacteristics warehouseCharacteristics;

  // ----------------------------------------------------------------------------

  public InputData() {
    this(InputData.PATH_INPUT_DATA);
  }

  public InputData(final String dataPath) {
    this.dataPath = dataPath;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "InputData@" + dataPath + "[\n " + orders + ",\n " + productCodes + "\n]";
  }

  // ----------------------------------------------------------------------------

  public void readData() throws IOException {
    readWarehouseCharacteristics();
    readOrders();
  }

  // ----------------------------------------------------------------------------

  public WarehouseCharacteristics getWarehouseCharacteristics() {
    return warehouseCharacteristics;
  }

  public final List<Order> getOrders() {
    return Collections.unmodifiableList(orders);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void readWarehouseCharacteristics() throws IOException {
    final Reader fr = new FileReader(fullFileName("warehouse-characteristics.properties"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      final Properties _warehouseCharacteristics = new Properties();
      _warehouseCharacteristics.load(reader);
      warehouseCharacteristics = new WarehouseCharacteristics(_warehouseCharacteristics);
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  private void readOrders() throws IOException {
    final Reader fr = new FileReader(fullFileName("orders.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // code;routeDeparture;(productCode;requestedquantity;)+
        final String[] columns = splitCsv(line);
        final String code = columns[0];
        final int routeDeparture = Integer.parseInt(columns[1]);
        final Order order = new Order(code, routeDeparture);
        for (int i = 2; i < columns.length; i += 2) {
          final String productCode = columns[i + 0];
          productCodes.add(productCode);
          final int requestedQuantity = Integer.parseInt(columns[i + 1]);
          order.getOrderLines().add(new OrderLine(order, productCode, requestedQuantity));
        }
        orders.add(order);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  protected File fullFileName(final String fileName) {
    final String fullFileName = dataPath + File.separator + fileName;
    return new File(fullFileName);
  }

  protected void close(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (final IOException exception) {
        exception.printStackTrace(System.err);
      }
    }
  }

  // ----------------------------------------------------------------------------

  protected String[] splitCsv(final String line) {
    return line.split(";");
  }

  // ----------------------------------------------------------------------------
  // ............................................................................
}
