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
import java.util.HexFormat;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Represents an IP V6 address.
 *
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.0.1")
public final class IpV6Address {
  private final int[] tuples;

  public IpV6Address(int[] tuples) {
    this.tuples = tuples;
  }


  public static final IpV6Address LOOPBACK_ADDRESS = IpV6Address.from("0:0:0:0:0:0:0:1");
  public static final IpV6Address UNSPECIFIED_ADDRESS = IpV6Address.from("0:0:0:0:0:0:0:0");

  public boolean isUnicastAddress() {
    return false;
  }

  public boolean isMulticastAddress() {
    return false;
  }

  public boolean isLoopbackAddress() {
    return this.equals(LOOPBACK_ADDRESS);
  }

  public boolean isUnspecifiedAddress() {
    return false;
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
   * @param ip6 String representing the IPV6 address.
   *
   * @return Instance of the {@link IpV6Address}
   */
  public static IpV6Address from(String ip6) {
    var ipTuples = ip6.split(":");
    int[] digits = new int[8];
    for (int i = 0; i < ipTuples.length; i++) {
      digits[i] = HexFormat.fromHexDigits(ipTuples[i]);
    }
    return new IpV6Address(digits);
  }

  private static Predicate<Integer> isXX = s -> s.intValue() < 0 && s.intValue() > 0xffff;

  public static IpV6Address from(int[] ip6) {
    if (ip6.length != 8) {
      throw new IllegalArgumentException("There must be eight components.");
    }
    var invalidRange = Arrays.stream(ip6).boxed().noneMatch(isXX);
    if (invalidRange) {

    }
    return new IpV6Address(ip6);
  }

  public int[] q() {
    return tuples;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (IpV6Address) obj;
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

//    static Comparator<Ip6Address> IP_ADDRESS_COMPARATOR = Comparator
//        .comparingInt(Ip6Address.q[0]);

}
