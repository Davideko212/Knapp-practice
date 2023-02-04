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

package com.knapp.codingcontest.cc20170310.data;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InputData {
  private static final String PATH_INPUT_DATA;
  static {
    try {
      PATH_INPUT_DATA = new File("./input").getCanonicalPath();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ----------------------------------------------------------------------------

  private final String dataPath;
  private final List<Product> products = new ArrayList<Product>();
  private final List<Container> containers = new ArrayList<Container>();

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
    return "InputData@" + dataPath + "[" + products + ",\n " + containers + "]";
  }

  // ----------------------------------------------------------------------------

  public void readData() throws IOException {
    readProducts();
    readContainers();
  }

  // ----------------------------------------------------------------------------

  public final List<Product> getProducts() {
    return Collections.unmodifiableList(products);
  }

  public final List<Container> getContainers() {
    return Collections.unmodifiableList(containers);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void readProducts() throws IOException {
    final Reader fr = new FileReader(fullFileName("products.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // code;(container-type;max-slot-quantity;)+
        final String[] columns = splitCsv(line);
        final String code = columns[0];
        final Product product = new Product(code);
        for (int i = 1; i < columns.length; i += 2) {
          final ContainerType ct = ContainerType.valueOf(columns[i + 0]);
          final int ctmax = Integer.parseInt(columns[i + 1]);
          product.setMaxQuantity(ct, ctmax);
        }
        products.add(product);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  private void readContainers() throws IOException {
    final Reader fr = new FileReader(fullFileName("containers.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // code;type(;product;quantity>0)*{#lots4type};
        final String[] columns = splitCsv(line);
        final String code = columns[0];
        final ContainerType type = ContainerType.valueOf(columns[1]);
        final Container container = new Container(code, type);
        for (int i = 2, s = 0; i < columns.length; i += 2, s++) {
          final String productCode = columns[i + 0];
          if (!"".equals(productCode)) {
            final Product product = findProductByCode(productCode);
            final int quantity = Integer.parseInt(columns[i + 1]);
            container.getSlots().get(s)._setProduct(product);
            container.getSlots().get(s)._setQuantity(quantity);
          }
        }
        containers.add(container);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  private Product findProductByCode(final String productCode) {
    for (final Product product : products) {
      if (productCode.equals(product.getCode())) {
        return product;
      }
    }
    throw new IllegalArgumentException("no product found for code=" + productCode);
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
        exception.printStackTrace(System.out);
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
