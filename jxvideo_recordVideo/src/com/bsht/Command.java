package com.bsht;

class Command
{
  static short client_id;
  static int cmd_size;
  static short command;
  static short length;
  static short method = 0;
  static int session_id;

  static
  {
    client_id = 0;
    session_id = 0;
    length = 0;
    cmd_size = 16;
  }

  public static byte[] get_command()
  {
    byte[] arrayOfByte = new byte[cmd_size];
    Utils.putShort(arrayOfByte, command, 0);
    Utils.putShort(arrayOfByte, method, 2);
    Utils.putShort(arrayOfByte, client_id, 4);
    Utils.putInt(arrayOfByte, session_id, 6);
    Utils.putShort(arrayOfByte, length, 10);
    Utils.putInt(arrayOfByte, 0, 12);
    return arrayOfByte;
  }

  public static void parse_reply(byte[] paramArrayOfByte)
  {
    int i = Packet.packet_size;
    command = Utils.getShort(paramArrayOfByte, i);
    method = Utils.getShort(paramArrayOfByte, i + 2);
    client_id = Utils.getShort(paramArrayOfByte, i + 4);
    session_id = Utils.getInt(paramArrayOfByte, i + 8);
    length = Utils.getShort(paramArrayOfByte, i + 12);
  }

  public static String to_string()
  {
    return "Command\n-------------\ncommand:" + command + "\nmethod:" + method + "\nclient_id:" + client_id + "\nsession_id:" + session_id + "\nlength:" + length;
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.Command
 * JD-Core Version:    0.6.0
 */