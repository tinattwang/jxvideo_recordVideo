package com.bsht.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient
  implements Runnable
{
  private MyCallback callback;
  private Socket clientSocket;
  private OutputStream dos;
  private InputStream is;
  private byte[] response = new byte[256];
  private boolean run = true;

  public TCPClient(String paramString, int paramInt, MyCallback paramCallback)
  {
    try
    {
      this.clientSocket = new Socket(InetAddress.getByName("192.168.5.254"), paramInt);
      this.clientSocket.setSoTimeout(1000);
      this.dos = this.clientSocket.getOutputStream();
      this.is = this.clientSocket.getInputStream();
      this.callback = paramCallback;
      new Thread(this).start();
      return;
    }
    catch (Exception localException)
    {
      paramCallback.log("TCPClient Error:" + localException.getMessage());
    }
  }

  public void destroy()
  {
    try
    {
      this.run = false;
      this.clientSocket.close();
      this.clientSocket = null;
      return;
    }
    catch (Exception localException)
    {
      while (true)
      {
        if (this.callback == null)
          continue;
        this.callback.log("TCPClient destroy Error:" + localException.getMessage());
      }
    }
  }

  public void run()
  {
    while (this.run)
      try
      {
        int i = this.is.read(this.response);
        if (i > 0)
        {
          byte[] arrayOfByte = new byte[i];
          System.arraycopy(this.response, 0, arrayOfByte, 0, i);
          this.callback.parseCommand(arrayOfByte);
        }
        Thread.sleep(100L);
      }
      catch (Exception localException)
      {
      }
  }

  public void send(byte[] paramArrayOfByte)
  {
    try
    {
      this.dos.write(paramArrayOfByte, 0, paramArrayOfByte.length);
      return;
    }
    catch (Exception localException)
    {
      this.callback.log("TCPClient send Error:" + localException.getMessage());
    }
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.net.TCPClient
 * JD-Core Version:    0.6.0
 */