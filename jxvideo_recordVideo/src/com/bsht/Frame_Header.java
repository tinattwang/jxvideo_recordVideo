package com.bsht;

class Frame_Header
{
  static int frame_seq;
  static int frame_size = 12;
  static short frame_type;
  static int session_id;
  static short size;

  public static byte[] get_frame_header()
  {
    byte[] arrayOfByte = new byte[frame_size];
    Utils.putInt(arrayOfByte, session_id, 0);
    Utils.putShort(arrayOfByte, size, 4);
    Utils.putShort(arrayOfByte, frame_type, 6);
    Utils.putInt(arrayOfByte, frame_seq, 8);
    return arrayOfByte;
  }

  public static String log()
  {
    return "sessionid:" + session_id + "/size:" + size + "/frame_type:" + frame_type + "/frame_seq:" + frame_seq;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Frame_Header
 * JD-Core Version:    0.6.0
 */