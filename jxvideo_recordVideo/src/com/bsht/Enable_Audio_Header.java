package com.bsht;

class Enable_Audio_Header
{
  static short port;
  static int remote;
  static short result;

  public static void parse_enable_audio_header(byte[] paramArrayOfByte)
  {
    int i = Packet.packet_size + Command.cmd_size;
    remote = Utils.getInt(paramArrayOfByte, i);
    port = Utils.getShort(paramArrayOfByte, i + 4);
    result = Utils.getShort(paramArrayOfByte, i + 6);
  }

  public static String to_string()
  {
    return "Enable_Audio_Header\nremote=" + remote + "\nport=" + port + "\nresult=" + result;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Enable_Audio_Header
 * JD-Core Version:    0.6.0
 */