/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.soebes.ip;

import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Represents an IP Version 6 address.
 *
 * @author Karl Heinz Marbaise
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc4291">IP Version 6 Addressing Architecture</a>
 *
 * @implNote Currently using internally {@code int} instead of {@code short} or alike because
 * it's easier to handle conversions from/to hex etc. without handling 2'th complements etc.
 * @implNote Maybe we should reconsider to use {@code short} or {@code char} instead? Not sure yet.
 */
@API(status = EXPERIMENTAL, since = "0.0.1")
public final class IPv6Address implements Comparator<IPv6Address> {

  private final int[] tuples;

  /**
   * Only used for internal purposes.
   */
  private IPv6Address(int[] tuples) {
    this.tuples = tuples;
  }

  /**
   * Only used for internal purposes.
   */
  private IPv6Address(int t1, int t2, int t3, int t4, int t5, int t6, int t7, int t8) {
    this.tuples = new int[]{t1, t2, t3, t4, t5, t6, t7, t8};
  }

  /**
   * The loopback address
   *
   * @link <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.5.3">The Loopback Address</a>
   */
  public static final IPv6Address LOOPBACK_ADDRESS = new IPv6Address(0,0,0,0,0,0,0,1);

  /**
   * The unspecified address use used to be compared to or as a default initialization.
   *
   * @link <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.5.2">The Unspecified Address</a>
   */
  public static final IPv6Address UNSPECIFIED_ADDRESS = new IPv6Address(0,0,0,0,0,0,0,0);

  public boolean isUnicastAddress() {
    return false;
  }

  public boolean isMulticastAddress() {
    return false;
  }

  /**
   * @return true if the current IPv6 represents the {@link #LOOPBACK_ADDRESS}, false otherwise.
   */
  public boolean isLoopbackAddress() {
    return this.equals(LOOPBACK_ADDRESS);
  }

  /**
   * @return true if the current IPv6 represents the {@link #UNSPECIFIED_ADDRESS}, false otherwise.
   */
  public boolean isUnspecifiedAddress() {
    return this.equals(UNSPECIFIED_ADDRESS);
  }

  /*
   2001:DB8:0:0:8:800:200C:417A   a unicast address
         FF01:0:0:0:0:0:0:101           a multicast address
         0:0:0:0:0:0:0:1                the loopback address
         0:0:0:0:0:0:0:0                the unspecified address

   2001:DB8::8:800:200C:417A      a unicast address
         FF01::101                      a multicast address
         ::1                            the loopback address
         ::                             the unspecified address
   */

  /*
the decimal values of the four low-order 8-bit pieces of the
      address (standard IPv4 representation).  Examples:

         0:0:0:0:0:0:13.1.68.3
         0:0:0:0:0:FFFF:129.144.52.38

      or in compressed form:
         ::13.1.68.3
         ::FFFF:129.144.52.38
   */

  /**
   * @param ip6 String representing of an IP V6 address.
   * @return Instance of the {@link IPv6Address}
   */
  //1. check the content of the string... valid "0-9A-Fa-f", ".", ":", "::", "/" ?
  //      0:0:0:0:0:0:13.1.68.3
  //      0:0:0:0:0:FFFF:129.144.52.38
  //      ::13.1.68.3
  //      ::FFFF:129.144.52.38
  //
  //      2001:0DB8:0000:CD30:0000:0000:0000:0000/60
  //      2001:0DB8::CD30:0:0:0:0/60
  //      2001:0DB8:0:CD30::/60
  //
  //      2001:0DB8:0:CD3/60   may drop leading zeros, but not trailing
  //                           zeros, within any 16-bit chunk of the address
  //
  //      2001:0DB8::CD30/60   address to left of "/" expands to
  //                           2001:0DB8:0000:0000:0000:0000:0000:CD30
  //
  //      2001:0DB8::CD3/60    address to left of "/" expands to
  //                           2001:0DB8:0000:0000:0000:0000:0000:0CD3
  private static String ZERO_ABBREVIATION = "::";

  private static int[] convert(String x) {
    var ipTuples = x.split(":");
    int[] digits = new int[ipTuples.length];
    for (int i = 0; i < ipTuples.length; i++) {
      digits[i] = HexFormat.fromHexDigits(ipTuples[i]);
      var isValid = digits[i] >= 0 && digits[i] <= 65535;
      if (!isValid) {
        throw new IllegalArgumentException("The valid range from 0...65535 is violated for [" + i + "]=" + ipTuples[i]);
      }
    }
    return digits;
  }

  public static IPv6Address from(String ip6) {
    if (!ip6.matches("[0-9a-fA-F.:/]+")) {
      throw new IllegalArgumentException("Invalid characters only 0-9a-fA-F.:/ are allowed.");
    }


    if (ip6.equals(ZERO_ABBREVIATION)) {
      return IPv6Address.UNSPECIFIED_ADDRESS;
    }

    var split = ip6.split(ZERO_ABBREVIATION);

    if (split.length > 1) {
      int[] result = new int[8];

      int[] digitsFirst = convert(split[0]);
      int[] digitsSecond = convert(split[1]);

      int pos = 7;
      for (int i = digitsSecond.length-1; i >=0; i--) {
        result[pos--] = digitsSecond[i];
      }

      pos -= (8 - digitsSecond.length - digitsFirst.length);
      for (int i = digitsFirst.length-1; i >=0; i--) {
        result[pos--] = digitsFirst[i];
      }

      return new IPv6Address(result);
    } else {
      int[] digits = convert(ip6);
      return new IPv6Address(digits);
    }

  }

  private static Predicate<Integer> isGreaterOrEqualsZero = s -> s >= 0;
  private static Predicate<Integer> isLessOrEqualsMaxValue = s -> s <= 0xffff;
  private static Predicate<Integer> isInValidRange = isGreaterOrEqualsZero.and(isLessOrEqualsMaxValue);

  public static IPv6Address from(int[] ip6) {
    if (ip6.length != 8) {
      throw new IllegalArgumentException("There must be eight components.");
    }
    var allValid = Arrays.stream(ip6).boxed().allMatch(isInValidRange);
    if (!allValid) {
      throw new IllegalArgumentException("All values must be in the range from 0...65535 (0x000...0xffff)");
    }

    return new IPv6Address(ip6);
  }

  @Override
  public int compare(IPv6Address o1, IPv6Address o2) {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (IPv6Address) obj;
    return Arrays.equals(this.tuples, that.tuples);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tuples);
  }


  @Override
  public String toString() {
    return Arrays.stream(tuples).boxed().map(v -> HexFormat.of().withUpperCase().toHexDigits((short) v.intValue())).collect(joining(":"));
  }

}
