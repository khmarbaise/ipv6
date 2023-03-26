package com.soebes.ip;

import java.util.Comparator;
import java.util.function.Function;

public record IpAddress(int t1, int t2, int t3, int t4) {
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
