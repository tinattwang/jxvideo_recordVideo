package com.bsht.jxvideo;

public class G711
{
  public static final int BIAS = 132;
  static final int QUANT_MASK = 15;
  static final int SEG_MASK = 112;
  static final int SEG_SHIFT = 4;
  static final int SIGN_BIT = 128;
  static final int[] _a2u;
  static final int[] _u2a;
  static final int[] seg_end = { 255, 511, 1023, 2047, 4095, 8191, 16383, 32767 };

  static
  {
    _u2a = new int[] { 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 27, 29, 31, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128 };
    _a2u = new int[] { 1, 3, 5, 7, 9, 11, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 32, 33, 33, 34, 34, 35, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 48, 49, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127 };
  }

  public static int alaw2linear(byte paramByte)
  {
    int i = (byte)(paramByte ^ 0x55);
    int j = (i & 0xF) << 4;
    int k = (i & 0x70) >> 4;
    int m = 0;
    switch (k)
    {
    default:
      m = j + 264 << k - 1;
    case 0:
    case 1:
    }
    while ((i & 0x80) != 0)
    {
      return m;
//      fixme
//      m = j + 8;
//      continue;
//      m = j + 264;
    }
    return -m;
  }

  public static int alaw2ulaw(int paramInt)
  {
    int i = paramInt & 0xFF;
    if ((i & 0x80) != 0)
      return 0xFF ^ _a2u[(i ^ 0xD5)];
    return 0x7F ^ _a2u[(i ^ 0x55)];
  }

  public static byte linear2alaw(int paramInt)
  {
    int i = 0;
    if (paramInt >= 0)
      i = 213;
    int j;
    while (true)
    {
      j = search(paramInt, seg_end);
      if (j < 8)
        break;
      return (byte)(i ^ 0x7F);
//      fixme
//      i = 85;
//      paramInt = -8 + -paramInt;
    }
    int k = (byte)(j << 4);
    if (j < 2);
    for (int m = (byte)(k | 0xF & paramInt >> 4); ; m = (byte)(k | 0xF & paramInt >> j + 3))
      return (byte)(m ^ i);
  }

  public static int linear2ulaw(int paramInt)
  {
//    int i;
//    if (paramInt < 0)
//      i = 132 - paramInt;
//    int k;
//    for (int j = 127; ; j = 255)
//    {
//      k = search(i, seg_end);
//      if (k < 8)
//        break;
//      return j ^ 0x7F;
//      i = paramInt + 132;
//    }
//    return j ^ (k << 4 | 0xF & i >> k + 3);
	  
	  return 1;
  }

  static int search(int paramInt, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramArrayOfInt.length; i++)
      if (paramInt <= paramArrayOfInt[i])
        return i;
    return paramArrayOfInt.length;
  }

  public static int ulaw2alaw(int paramInt)
  {
    int i = paramInt & 0xFF;
    if ((i & 0x80) != 0)
      return 0xD5 ^ -1 + _u2a[(i ^ 0xFF)];
    return 0x55 ^ -1 + _u2a[(i ^ 0x7F)];
  }

  public static int ulaw2linear(int paramInt)
  {
    int i = paramInt ^ 0xFFFFFFFF;
    int j = 132 + ((i & 0xF) << 3) << ((i & 0x70) >> 4);
    if ((i & 0x80) != 0)
      return 132 - j;
    return j - 132;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.jxvideo.G711
 * JD-Core Version:    0.6.0
 */