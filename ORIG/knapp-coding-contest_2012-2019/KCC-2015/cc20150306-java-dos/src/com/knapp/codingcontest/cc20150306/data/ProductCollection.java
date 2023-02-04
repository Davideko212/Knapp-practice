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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProductCollection implements Iterable<Product> {
  private final Map<String, Product> products = new HashMap<String, Product>();

  // ----------------------------------------------------------------------------

  ProductCollection() {
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "ProductCollection[" + products.values() + "]";
  }

  /**
   * Get an iterator for all products in the collection
   */
  @Override
  public Iterator<Product> iterator() {
    return Collections.unmodifiableCollection(products.values()).iterator();
  }

  /**
   * Get the product with the given code
   *
   * @param productCode code of the product to return
   * @return product with the given code if it was found in the collection; null otherwise
   */
  public Product findByCode(final String productCode) {
    return products.get(productCode);
  }

  // ----------------------------------------------------------------------------

  void add(final Product product) {
    products.put(product.getCode(), product);
  }

  // ----------------------------------------------------------------------------
}