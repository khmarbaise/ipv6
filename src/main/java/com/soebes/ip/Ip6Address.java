package com.soebes.ip;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public final class Ip6Address {
  private final int[] q;

  public Ip6Address(int[] q) {
    this.q = q;
  }

  @Override
  public String toString() {
    return Arrays.stream(q).boxed().map(v -> HexFormat.of().withUpperCase().toHexDigits((short) v.intValue())).collect(joining(":"));
  }

  public static final Ip6Address LOOPBACK_ADDRESS = Ip6Address.from("0:0:0:0:0:0:0:1");
  public static final Ip6Address UNSPECIFIED_ADDRESS = Ip6Address.from("0:0:0:0:0:0:0:0");

  public boolean isUnicastAddress() {
    return false;
  }

  public boolean isMulticastAddress() {
    return false;
  }

  public boolean isLoopbackAddress() {
    return false;
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

  public static Ip6Address from(String ip6) {
    var ipTuples = ip6.split(":");
    int[] digits = new int[8];
    for (int i = 0; i < ipTuples.length; i++) {
      digits[i] = HexFormat.fromHexDigits(ipTuples[i]);
    }
    return new Ip6Address(digits);
  }

  public int[] q() {
    return q;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (Ip6Address) obj;
    return Arrays.equals(this.q, that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(q);
  }


//    static Comparator<Ip6Address> IP_ADDRESS_COMPARATOR = Comparator
//        .comparingInt(Ip6Address.q[0]);

}
