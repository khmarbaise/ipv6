/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
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
import java.util.function.IntPredicate;

import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Represents an IP Version 6 address.
 *
 * @author Karl Heinz Marbaise
 * @implNote Currently using internally {@code int} instead of {@code short} or alike because
 * it's easier to handle conversions from/to hex etc. without handling 2'th complements etc.
 * @implNote Maybe we should reconsider to use {@code short} or {@code char} instead? Not sure yet.
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc4291">IP Version 6 Addressing Architecture</a>
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
  @SuppressWarnings("java:S107")
  private IPv6Address(int t1, int t2, int t3, int t4, int t5, int t6, int t7, int t8) {
    this.tuples = new int[]{t1, t2, t3, t4, t5, t6, t7, t8};
  }

  /**
   * The loopback address
   *
   * @link <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.5.3">The Loopback Address</a>
   */
  public static final IPv6Address LOOPBACK = new IPv6Address(0, 0, 0, 0, 0, 0, 0, 1);

  /**
   * The unspecified address is used to be compared to or as a default initialization.
   *
   * @link <a href="https://datatracker.ietf.org/doc/html/rfc4291#section-2.5.2">The Unspecified Address</a>
   */
  public static final IPv6Address UNSPECIFIED = new IPv6Address(0, 0, 0, 0, 0, 0, 0, 0);

  public boolean isUnicastAddress() {
    return false;
  }

  public boolean isMulticastAddress() {
    return (this.tuples[0] & 0xff00) == 0xff00;
  }

  /**
   * @return true if the current IPv6 represents the {@link #LOOPBACK}, false otherwise.
   */
  public boolean isLoopbackAddress() {
    return this.equals(LOOPBACK);
  }

  /**
   * @return true if the current IPv6 represents the {@link #UNSPECIFIED}, false otherwise.
   */
  public boolean isUnspecifiedAddress() {
    return this.equals(UNSPECIFIED);
  }

  private static final String ZERO_ABBREVIATION = "::";

  private static int[] convert(String x) {
    var ipTuples = x.split(":");
    int[] digits = new int[ipTuples.length];
    for (int i = 0; i < ipTuples.length; i++) {
      digits[i] = HexFormat.fromHexDigits(ipTuples[i]);
      var isInvalid = digits[i] < 0 || digits[i] > 65535;
      if (isInvalid) {
        throw new IllegalArgumentException("The valid range from 0...65535 is violated for [" + i + "]=" + ipTuples[i]);
      }
    }
    return digits;
  }

  /**
   * @param ip6 The given string representation of an IP Version 6 address.
   * @return The instance of {@link IPv6Address}.
   */
  public static IPv6Address of(String ip6) {
    if (!ip6.matches("[0-9a-fA-F.:/]+")) {
      throw new IllegalArgumentException("Invalid characters only 0-9a-fA-F.:/ are allowed.");
    }

    if (ip6.equals(ZERO_ABBREVIATION)) {
      return IPv6Address.UNSPECIFIED;
    }

    var split = ip6.split(ZERO_ABBREVIATION);

    if (split.length > 2) {
      throw new IllegalArgumentException("Grouping with :: only allowed once.");
    }

    if (split.length == 2) {
      int[] result = new int[8];

      int[] digitsFirst = convert(split[0]);

      int[] digitsSecond;
      if (split[1].contains(".")) { //FIXME: Better checking
        // having an IP4 in there...
        var ip4 = IPv4Address.toIpAddress.apply(split[1]);
        digitsSecond = new int[]{ip4.first16(), ip4.second16()};
      } else {
        digitsSecond = convert(split[1]);
      }

      int pos = 7;
      for (int i = digitsSecond.length - 1; i >= 0; i--) {
        result[pos--] = digitsSecond[i];
      }

      pos -= (8 - digitsSecond.length - digitsFirst.length);
      for (int i = digitsFirst.length - 1; i >= 0; i--) {
        result[pos--] = digitsFirst[i];
      }

      return new IPv6Address(result);
    } else {
      int[] digits = convert(ip6);
      return new IPv6Address(digits);
    }

  }

  private static final IntPredicate isGreaterOrEqualsZero = s -> s >= 0;
  private static final IntPredicate isLessOrEqualsMaxValue = s -> s <= 0xffff;
  private static final IntPredicate inValidRange = isGreaterOrEqualsZero.and(isLessOrEqualsMaxValue);

  /**
   * @param ip6 The eight tuples each 16 bit unsigned.
   * @return An {@link IPv6Address}.
   * @throws IllegalArgumentException in case of failures.
   */
  public static IPv6Address of(int[] ip6) {
    if (ip6.length != 8) {
      throw new IllegalArgumentException("There must be eight components.");
    }
    var allValid = Arrays.stream(ip6).boxed().allMatch(inValidRange::test);
    if (!allValid) {
      throw new IllegalArgumentException("All values must be in the range from 0...65535 (0x0000...0xffff)");
    }

    return new IPv6Address(ip6);
  }

  @Override
  public int compare(IPv6Address o1, IPv6Address o2) {
    return Arrays.compare(o1.tuples, o2.tuples);
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
    return Arrays.hashCode(this.tuples);
  }


  @Override
  public String toString() {
    return Arrays.stream(tuples).boxed().map(v -> HexFormat.of().withUpperCase().toHexDigits((short) v.intValue())).collect(joining(":"));
  }

}
