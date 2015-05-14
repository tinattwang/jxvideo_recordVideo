package com.bsht.net;

public abstract interface MyCallback
{
  //public abstract void keepAlive();

  public abstract void log(Object paramObject);

  public abstract void parseCommand(byte[] paramArrayOfByte);

  public abstract void playAudio(byte[] paramArrayOfByte, int paramInt);
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.net.Callback
 * JD-Core Version:    0.6.0
 */