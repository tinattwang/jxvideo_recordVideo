package com.bsht;

public class Utils
{
  public static int getInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (0xFF & paramArrayOfByte[(paramInt + 3)]) << 24 | (0xFF & paramArrayOfByte[(paramInt + 2)]) << 16 | (0xFF & paramArrayOfByte[(paramInt + 1)]) << 8 | 0xFF & paramArrayOfByte[paramInt];
  }

  public static short getShort(byte[] paramArrayOfByte, int paramInt)
  {
    return (short)((0xFF & paramArrayOfByte[(paramInt + 1)]) << 8 | 0xFF & paramArrayOfByte[paramInt]);
  }

  public static void putInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[(paramInt2 + 3)] = (byte)(paramInt1 >> 24);
    paramArrayOfByte[(paramInt2 + 2)] = (byte)(paramInt1 >> 16);
    paramArrayOfByte[(paramInt2 + 1)] = (byte)(paramInt1 >> 8);
    paramArrayOfByte[paramInt2] = (byte)paramInt1;
  }

  public static void putShort(byte[] paramArrayOfByte, short paramShort, int paramInt)
  {
    paramArrayOfByte[(paramInt + 1)] = (byte)(paramShort >> 8);
    paramArrayOfByte[paramInt] = (byte)paramShort;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Utils
 * JD-Core Version:    0.6.0
 */