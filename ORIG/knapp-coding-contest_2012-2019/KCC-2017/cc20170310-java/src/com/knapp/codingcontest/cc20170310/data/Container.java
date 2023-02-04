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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Container {
  // ----------------------------------------------------------------------------
  // ............................................................................

  private final String code;
  private final ContainerType type;
  private final ContainerSlot[] slots;
  private final List<ContainerSlot> slots_;

  // ----------------------------------------------------------------------------

  protected Container(final String code, final ContainerType type) {
    this.code = code;
    this.type = type;
    slots = new ContainerSlot[type.getNumberOfSlots()];
    for (int i = 0; i < slots.length; i++) {
      slots[i] = new ContainerSlot(this, i);
    }
    slots_ = Collections.unmodifiableList(Arrays.asList(slots));
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    final String[] slotStrings = new String[slots.length];
    for (int s = 0; s < slots.length; s++) {
      slotStrings[s] = slots[s].isEmpty() ? "<empty>" : (slots[s].getProduct().getCode() + "#" + slots[s].getQuantity());
    }
    return "Container[code=" + code + ", type=" + type + "]{" + Arrays.toString(slotStrings) + "}";
  }

  // ----------------------------------------------------------------------------

  public final String getCode() {
    return code;
  }

  public final ContainerType getType() {
    return type;
  }

  public boolean isEmpty() {
    for (final ContainerSlot slot : slots) {
      if (!slot.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public List<ContainerSlot> getEmptySlots() {
    final List<ContainerSlot> emptySlots = new ArrayList<ContainerSlot>();
    for (final ContainerSlot slot : slots) {
      if (slot.isEmpty()) {
        emptySlots.add(slot);
      }
    }
    return emptySlots;
  }

  // ----------------------------------------------------------------------------

  public List<ContainerSlot> getSlots() {
    return Collections.unmodifiableList(slots_);
  }

  // ----------------------------------------------------------------------------
}
