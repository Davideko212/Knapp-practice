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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class OutputData {
  private final String resultPath;

  // ----------------------------------------------------------------------------

  public OutputData(final String resultPath) {
    this.resultPath = resultPath;
  }

  // ----------------------------------------------------------------------------

  /**
   * write locationCode/ productCode - tuple into csv
   *
   * WARNING: The output format must nor be changed, otherwise the result can and will not be ranked
   */
  public void writeOutput(final ZoneCollection zoneCollection) throws IOException {
    final Writer fw = new FileWriter(fullFileName("assignments.csv"));
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(fw);
      // loc-code;product-code [suppress unassigned locations]
      for (final Zone zone : zoneCollection) {
        for (final Shelf shelf : zone.getShelfs()) {
          for (final Location location : shelf.getLocations()) {
            if (location.getProduct() != null) {
              writer.append(location.getCode()).append(";");
              writer.append(location.getProduct().getCode()).append(";");
              writer.newLine();
            }
          }
        }
      }
    } finally {
      close(writer);
      close(fw);
    }
  }

  // ----------------------------------------------------------------------------

  private File fullFileName(final String fileName) {
    final String fullFileName = resultPath + File.separator + fileName;
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
}
