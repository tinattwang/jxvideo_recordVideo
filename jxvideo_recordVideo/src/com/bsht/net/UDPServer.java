package com.bsht.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

public class UDPServer
  implements Runnable
{
  private MyCallback callback;
  DatagramPacket in;
  private boolean run = true;
  private DatagramSocket socket;

  public UDPServer(int paramInt,String address ,byte[] paramArrayOfByte, MyCallback paramCallback)
  {
    this.callback = paramCallback;
    try
    {
      this.socket = new DatagramSocket(paramInt, InetAddress.getByName(address));
      this.in = new DatagramPacket(paramArrayOfByte, paramArrayOfByte.length);
      new Thread(this).start();
      return;
    }
    catch (Exception localException)
    {
      while (true)
        paramCallback.log("UDPServer Error:" + localException.getMessage());
    }
  }

  public void destroy()
  {
    this.run = false;
    this.socket.close();
    this.socket = null;
  }

  public void run()
  {
    try
    {
      while (this.run)
      {
        this.socket.receive(this.in);
        
    	Log.d("test", "receive...............................");
        
        byte[] data = this.in.getData();
        
        //this.callback.playAudio(this.in.getData(), this.in.getLength());
      }
    }
    catch (Exception localException)
    {
      this.callback.log("UDPServer Receive Error:" + localException.getMessage());
    }
  }

  public void send(DatagramPacket paramDatagramPacket)
  {
    try
    {
      this.socket.send(paramDatagramPacket);
      return;
    }
    catch (Exception localException)
    {
      this.callback.log("UDPServer send error:" + localException.getMessage());
    }
  }
}

/* Location:           D:\Program Files\Android反编译工具V2.1\classes_dex2jar.jar
 * Qualified Name:     com.bsht.net.UDPServer
 * JD-Core Version:    0.6.0
 */