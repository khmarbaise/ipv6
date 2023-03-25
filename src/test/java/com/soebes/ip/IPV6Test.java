package com.soebes.ip;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

class IPV6Test {

  record Ip6Address(int[] q) {
    @Override
    public String toString() {
      return Arrays.stream(q).boxed().map(v -> HexFormat.of().withUpperCase().toHexDigits((short)v.intValue())).collect(joining(":"));
    }

    static Ip6Address from(String ip6) {
      var ipTuples = ip6.split(":");
      int[] digits = new int[8];
      for (int i = 0; i < ipTuples.length; i++) {
        digits[i] = HexFormat.fromHexDigits(ipTuples[i]);
      }
      return new Ip6Address(digits);
    }

//    static Comparator<Ip6Address> IP_ADDRESS_COMPARATOR = Comparator
//        .comparingInt(Ip6Address.q[0]);

  }

  record IpAddress(int t1, int t2, int t3, int t4) {
    @Override
    public String toString() {
      return t1 + "." + t2 + "." + t3 + "." + t4;
    }

    static Comparator<IpAddress> IP_ADDRESS_COMPARATOR = Comparator
        .comparingInt(IpAddress::t1)
        .thenComparingInt(IpAddress::t2)
        .thenComparingInt(IpAddress::t3)
        .thenComparingInt(IpAddress::t4);

    static Function<String, IpAddress> toIpAddress = s -> {
      var ipTuples = s.split("\\.");
      return new IpAddress(
          Integer.parseInt(ipTuples[0]),
          Integer.parseInt(ipTuples[1]),
          Integer.parseInt(ipTuples[2]),
          Integer.parseInt(ipTuples[3])
      );
    };

  }

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

  @Test
  void ipv6() {
    String ipv6 = "2001:DB8:0:0:8:800:200C:417A";

    String examples = """
        ABCD:EF01:2345:6789:ABCD:EF01:2345:6789
        2001:DB8:0:0:8:800:200C:417A
         """;
  }
}