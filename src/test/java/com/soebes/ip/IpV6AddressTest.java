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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpV6AddressTest {

  @Test
  void loop_back_ip_address() {
    String loopBack = "0:0:0:0:0:0:0:1";
    var loop = IpV6Address.from(loopBack);
    assertThat(loop).isEqualTo(IpV6Address.LOOPBACK_ADDRESS);
    System.out.println("ip6Address = " + loop);

  }

  @Test
  void first_basic_conversion() {
    String ipv6 = "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789";
    var ip6Address = IpV6Address.from(ipv6);
    assertThat(ip6Address.toString()).hasToString(ipv6);
    System.out.println("ip6Address = " + ip6Address);
  }

  @Test
  void convertion() {
    String ipv6 = "FFF8:FFF9:FFFA:FFFB:FFFC:FFFD:FFFE:FFFF";
    var ip6Address = IpV6Address.from(ipv6);
    assertThat(ip6Address.toString()).hasToString(ipv6);
    System.out.println("ip6Address = " + ip6Address);
  }



}