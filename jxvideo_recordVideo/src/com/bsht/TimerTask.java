package com.bsht;

import com.bsht.net.MyCallback;

public class TimerTask extends java.util.TimerTask
{
  private MyCallback callback;

  public TimerTask(MyCallback paramCallback)
  {
    this.callback = paramCallback;
  }

  public void run()
  {
    //this.callback.keepAlive();
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.TimerTask
 * JD-Core Version:    0.6.0
 */