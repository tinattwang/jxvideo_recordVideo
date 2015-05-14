package com.bsht;

class Keep_Alive_Message
{
  static int msg_size;
  static int next_time = 500;
  static int session_id;

  static
  {
    msg_size = 8;
  }

  public static byte[] get_keep_alive_message()
  {
    byte[] arrayOfByte = new byte[msg_size];
    Utils.putInt(arrayOfByte, session_id, 0);
    Utils.putInt(arrayOfByte, next_time, 4);
    return arrayOfByte;
  }

  public static String to_string()
  {
    return "Keep Alive Message\nSession Id:" + session_id + "\nnext time:" + next_time;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Keep_Alive_Message
 * JD-Core Version:    0.6.0
 */