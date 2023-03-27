package com.soebes.ip;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public record IPv4Address(int t1, int t2, int t3, int t4) {
  @Override
  public String toString() {
    return t1 + "." + t2 + "." + t3 + "." + t4;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (IPv4Address) obj;
    return this.t1 == that.t1 &&
           this.t2 == that.t2 &&
           this.t3 == that.t3 &&
           this.t4 == that.t4;
  }


  static Comparator<IPv4Address> IP_ADDRESS_COMPARATOR = Comparator
      .comparingInt(IPv4Address::t1)
      .thenComparingInt(IPv4Address::t2)
      .thenComparingInt(IPv4Address::t3)
      .thenComparingInt(IPv4Address::t4);

  static Function<String, IPv4Address> toIpAddress = s -> {
    var ipTuples = s.split("\\.");
    return new IPv4Address(
        Integer.parseInt(ipTuples[0]),
        Integer.parseInt(ipTuples[1]),
        Integer.parseInt(ipTuples[2]),
        Integer.parseInt(ipTuples[3])
    );
  };

}
