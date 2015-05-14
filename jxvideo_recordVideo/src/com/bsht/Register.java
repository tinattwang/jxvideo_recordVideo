package com.bsht;

class Register
{
  static int address = 0;
  static short port = 0;
  static int register_size = 8;
  static short uuid_size;

  public static byte[] get_register()
  {
    byte[] arrayOfByte = new byte[register_size];
    Utils.putInt(arrayOfByte, address, 0);
    Utils.putShort(arrayOfByte, port, 4);
    Utils.putShort(arrayOfByte, uuid_size, 6);
    return arrayOfByte;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Register
 * JD-Core Version:    0.6.0
 */