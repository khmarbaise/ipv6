package com.soebes.ip;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IPv4AddressTest {

  @Test
  void first() {
    var ip = new IPv4Address(128, 234, 192, 12);
    var first = ip.first16();
    var second = ip.second16();
    assertThat(first).isEqualTo(0x80EA);
    assertThat(second).isEqualTo(0xC00C);
  }
}