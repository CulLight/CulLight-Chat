package com.CulLight.Chat;

import java.net.InetAddress;

public class test {

  public static void main(String args[]) throws Exception {

    InetAddress ca = InetAddress.getByName("www.google.com");
    InetAddress com = InetAddress.getByName("www.google.com");
    if (ca == com) {
      System.out.println("same");
    } else {
      System.out.println("not the same");
    }
  }

}