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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.soebes.ip.IPv6Address.LOOPBACK;
import static com.soebes.ip.IPv6Address.UNSPECIFIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.of;

class IPv6AddressTest {

  @Nested
  class Constants {

    @Test
    void given_loop_back_address() {
      String givenIpV6 = "0:0:0:0:0:0:0:1";

      var resultingIpV6 = IPv6Address.from(givenIpV6);

      assertThat(resultingIpV6).isEqualTo(LOOPBACK);
    }

    @Test
    void given_unspecified_address() {
      String givenIpV6 = "0:0:0:0:0:0:0:0";

      var resultingIpV6 = IPv6Address.from(givenIpV6);

      assertThat(resultingIpV6).isEqualTo(UNSPECIFIED);
    }

  }

  @Test
  void first_basic_conversion() {
    String givenIPAsString = "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789";

    var ip6Address = IPv6Address.from(givenIPAsString);

    assertThat(ip6Address).hasToString(givenIPAsString);
  }

  @Test
  void conversion() {
    String givenIPAsString = "FFF8:FFF9:FFFA:FFFB:FFFC:FFFD:FFFE:FFFF";

    var ip6Address = IPv6Address.from(givenIPAsString);

    assertThat(ip6Address).hasToString(givenIPAsString);
  }

  @Test
  @DisplayName("Given a IP v 6 address with a single invalid value.")
  void invalid_values() {
    String givenIPAsString = "FFF8:FFF9:FFFA:FFFB:FFFC:FFFD:FFFE:FFFFF";

    assertThatIllegalArgumentException()
        .isThrownBy(() -> IPv6Address.from(givenIPAsString))
        .withMessage("The valid range from 0...65535 is violated for [7]=FFFFF");
  }

  @Nested
  @DisplayName("Unspecified addresses...")
  class UnspecifiedAddress {

    @Test
    @DisplayName("Given constant should be an unspecified address.")
    void given_constant_should_be_unspecified_address() {
      assertThat(UNSPECIFIED.isUnspecifiedAddress()).isTrue();
    }

    @Test
    @DisplayName("Given address should be an unspecified address.")
    void given_address_should_be_unspecified_address() {
      String givenIPAsString = "0000:0000:0000:0000:0000:0000:0000:0000";

      assertThat(IPv6Address.from(givenIPAsString).isUnspecifiedAddress()).isTrue();
    }

    @Test
    @DisplayName("Give address should not be an unspecified address.")
    void given_address_should_not_be_an_unspecified_address() {
      String givenIPAsString = "0000:0000:0000:0000:0000:0000:0000:0001";

      assertThat(IPv6Address.from(givenIPAsString).isUnspecifiedAddress()).isFalse();
    }

  }

  // This is not working yet!
  static Stream<Arguments> conversions_ip6_with_ip4_with_double_quote() {
    return Stream.of(
        of("0:0:0:0:0:0:13.1.68.3", "0000:0000:0000:0000:0000:0000:0D01:4403"),
        of("::13.1.68.3", "0000:0000:0000:0000:0000:0000:0D01:4403"),
        of("::FFFF:129.144.52.38", "0000:0000:0000:0000:0000:FFFF:0D01:4403")
    );
  }

  @ParameterizedTest
  @MethodSource
  void conversions_ip6_with_ip4_with_double_quote(String given, String expected) {
    var ip6Address = IPv6Address.from(given);
    assertThat(ip6Address).hasToString(expected);
  }



  static Stream<Arguments> conversions_ip6_with_double_quote() {
    return Stream.of(
        of("2001:DB8::8:800:200C:417A", "2001:0DB8:0000:0000:0008:0800:200C:417A"),
        of("FF01::101", "FF01:0000:0000:0000:0000:0000:0000:0101"),
        of("::1", "0000:0000:0000:0000:0000:0000:0000:0001"),
        of("::", "0000:0000:0000:0000:0000:0000:0000:0000")
    );
  }

  @ParameterizedTest
  @MethodSource
  void conversions_ip6_with_double_quote(String given, String expected) {
    var ip6Address = IPv6Address.from(given);
    assertThat(ip6Address).hasToString(expected);
  }

  @Nested
  class FromForInt {

    @Test
    void validation_1() {
      int[] tuples = {0, 0, 0, 0, 0, 0, 0, 0};
      assertThatCode(() -> IPv6Address.from(tuples)).doesNotThrowAnyException();
    }

    @Test
    void invalid_1() {
      int[] tuples = {0, 0, 0, 0, 0, 0, 0, -1};
      assertThatIllegalArgumentException()
          .isThrownBy(() -> IPv6Address.from(tuples))
          .withMessage("All values must be in the range from 0...65535 (0x000...0xffff)");
    }

    @Test
    void invalid_2() {
      int[] tuples = {0, 0, 0, 0, 0, 0, 0, 0x10000};
      assertThatIllegalArgumentException()
          .isThrownBy(() -> IPv6Address.from(tuples))
          .withMessage("All values must be in the range from 0...65535 (0x000...0xffff)");
    }

    @Test
    void invalid_3() {
      int[] tuples = {0, 0, 0, 0, 0, 0, 0x10000};
      assertThatIllegalArgumentException()
          .isThrownBy(() -> IPv6Address.from(tuples))
          .withMessage("There must be eight components.");
    }

    @Test
    void validation_2() {
      int[] tuples = {0xffff, 0xffff, 0xffff, 0xffff, 0xffff, 0xffff, 0xffff, 0xffff};
      assertThatCode(() -> IPv6Address.from(tuples)).doesNotThrowAnyException();
    }
  }
}