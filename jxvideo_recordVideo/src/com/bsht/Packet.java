package com.bsht;

class Packet
{
  static int packet_size;
  static short size;
  static short version = 1;

  static
  {
    packet_size = 4;
  }

  public static byte[] get_package()
  {
    byte[] arrayOfByte = new byte[packet_size];
    Utils.putShort(arrayOfByte, version, 0);
    Utils.putShort(arrayOfByte, size, 2);
    return arrayOfByte;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Packet
 * JD-Core Version:    0.6.0
 */