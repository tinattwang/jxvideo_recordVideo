package com.bsht;

class Register_Reply
{
  static short client_id;
  static byte level;
  static byte result;
  static int session_id;

  public static String parse_reply(byte[] paramArrayOfByte)
  {
    int i = Packet.packet_size + Command.cmd_size;
    result = paramArrayOfByte[i];
    level = paramArrayOfByte[(i + 1)];
    client_id = Utils.getShort(paramArrayOfByte, i + 2);
    session_id = Utils.getInt(paramArrayOfByte, i + 4);
    return "Register_Reply\nresult:" + result + "\nlevel:" + level + "\nclient_id:" + client_id + "\nsession_id:" + session_id;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Register_Reply
 * JD-Core Version:    0.6.0
 */