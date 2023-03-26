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

import static com.soebes.ip.IpV6Address.LOOPBACK_ADDRESS;
import static com.soebes.ip.IpV6Address.UNSPECIFIED_ADDRESS;
import static org.assertj.core.api.Assertions.assertThat;

class IpV6AddressTest {

  @Test
  void given_loop_back_address() {
    String givenIpV6 = "0:0:0:0:0:0:0:1";

    var resultingIpV6 = IpV6Address.from(givenIpV6);

    assertThat(resultingIpV6).isEqualTo(LOOPBACK_ADDRESS);
  }

  @Test
  void given_unspecified_address() {
    String givenIpV6 = "0:0:0:0:0:0:0:0";

    var resultingIpV6 = IpV6Address.from(givenIpV6);

    assertThat(resultingIpV6).isEqualTo(UNSPECIFIED_ADDRESS);
  }

  @Test
  void first_basic_conversion() {
    String givenIPAsString = "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789";

    var ip6Address = IpV6Address.from(givenIPAsString);

    assertThat(ip6Address).hasToString(givenIPAsString);
  }

  @Test
  void convertion() {
    String givenIPAsString = "FFF8:FFF9:FFFA:FFFB:FFFC:FFFD:FFFE:FFFF";

    var ip6Address = IpV6Address.from(givenIPAsString);

    assertThat(ip6Address).hasToString(givenIPAsString);
  }


}