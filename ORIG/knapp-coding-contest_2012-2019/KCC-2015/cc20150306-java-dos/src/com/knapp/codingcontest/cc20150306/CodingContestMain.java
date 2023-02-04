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

package com.knapp.codingcontest.cc20150306;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.knapp.codingcontest.cc20150306.data.InputData;
import com.knapp.codingcontest.cc20150306.data.OutputData;
import com.knapp.codingcontest.cc20150306.solution.Solution;

public class CodingContestMain {
  // ----------------------------------------------------------------------------

  public static void main(final String... args) throws Exception {
    System.out.println("KNAPP Coding Contest 2015: STARTING...");

    final Properties properties = new Properties();
    properties.load(new FileInputStream("kcc2015.properties"));
    final String basedir = System.getProperty("basedir", properties.getProperty("basedir", "."));
    final String dataPath = basedir + File.separator + "data";
    final String resultPath = basedir;

    System.out.println("... LOADING DATA ...");
    final InputData inputData = CodingContestMain.createInputData(dataPath);

    System.out.println("... ASSIGN PRODUCTS/LOCATIONS ...");
    new Solution().assignProducts(inputData);

    System.out.println("... WRITING OUTPUT/RESULT ...");
    final OutputData outputData = new OutputData(resultPath);
    outputData.writeOutput(inputData.getZoneCollection());

    System.out.println("KNAPP Coding Contest 2015: FINISHED");
  }

  // ----------------------------------------------------------------------------

  private static InputData createInputData(final String dataPath) throws IOException {
    final InputData inputData = new InputData(dataPath);
    inputData.readData();
    return inputData;
  }

  // ----------------------------------------------------------------------------
}
