package com.soebes.ip;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Ip6AddressTest {

  @Test
  void first_basic_conversion() {
    String ipv6 = "ABCD:EF01:2345:6789:ABCD:EF01:2345:6789";
    var ip6Address = Ip6Address.from(ipv6);
    assertThat(ip6Address.toString()).hasToString(ipv6);
    System.out.println("ip6Address = " + ip6Address);
  }

  @Test
  void convertion() {
    String ipv6 = "FFF8:FFF9:FFFA:FFFB:FFFC:FFFD:FFFE:FFFF";
    var ip6Address = Ip6Address.from(ipv6);
    assertThat(ip6Address.toString()).hasToString(ipv6);
    System.out.println("ip6Address = " + ip6Address);
  }



}