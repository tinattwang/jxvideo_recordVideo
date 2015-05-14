package com.bsht;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.PropertyInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SoapRequest {
	private static final String NAMESPACE = "http://ms.dayang.com/m3/xsd";
	private static final String METHOD_NAME = "GetM3InfoByDevIDRequest";

	private static String wsdl_url = "";

	public static boolean Register(String servise, int id, String password, 
			SharedPreferences lastSp) {

		boolean flag = false;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
		wsdl_url = "http://" + servise + "/ms/services/QueryDevInfoService?wsdl";
		HttpTransportSE ht = new HttpTransportSE(wsdl_url);
		ht.debug = true;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	
		PropertyInfo pwd = new PropertyInfo();
		pwd.setName("PassWord");
		pwd.setValue(password);
		pwd.setNamespace(NAMESPACE);
		
		PropertyInfo IP = new PropertyInfo();
		IP.setName("DevIP");
		String ip = getLocalIpAddress();
		IP.setValue(ip);
		IP.setNamespace(NAMESPACE);
		
		PropertyInfo ID = new PropertyInfo();
		ID.setName("DevID");
		ID.setValue(id);
		ID.setNamespace(NAMESPACE);
		
		request.addProperty(pwd);
		request.addProperty(IP);
		request.addProperty(ID);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		
		try {
			ht.call(null, envelope);			

			if (envelope.getResponse() != null) {
				SoapObject result = (SoapObject) envelope.bodyIn;
				SoapObject statusObj = (SoapObject)result.getProperty(0);
				int nStatus =Integer.parseInt(statusObj.getProperty("Status").toString());
				if(nStatus != 2)
				{
					return flag;
				}
				SoapObject m3InfoObj = (SoapObject)result.getProperty(1);
				String nM3ID = m3InfoObj.getProperty("M3ID").toString();
				String strM3IP = m3InfoObj.getProperty("M3LocalInIP").toString();
				String nM3Port = m3InfoObj.getProperty("M3InPort").toString();
				
				flag = true;
				Editor editor = lastSp.edit();
				editor.putString("play_url", strM3IP);
				editor.putString("play_port", nM3Port);
				editor.putString("id", Integer.toString(id));
				editor.commit();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return flag;
	}

	public static boolean isInnerIP(String ipAddress) {
		boolean isInnerIp = false;
		long ipNum = getIpNum(ipAddress);
		/**
		 * 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
		 * 192.168.0.0-192.168.255.255 当然，还有127这个网段是环回地址
		 **/
		long aBegin = getIpNum("10.0.0.0");
		long aEnd = getIpNum("10.255.255.255");
		long bBegin = getIpNum("172.16.0.0");
		long bEnd = getIpNum("172.31.255.255");
		long cBegin = getIpNum("192.168.0.0");
		long cEnd = getIpNum("192.168.255.255");
		isInnerIp = isInner(ipNum, aBegin, aEnd)
				|| isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
				|| ipAddress.equals("127.0.0.1");
		return isInnerIp;
	}

	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}

	// public static List<DeviceInfo> getDeviceList() {
	//
	// // Thread thread = new Thread(new Runnable() {
	// //
	// // @Override
	// // public void run() {
	//
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	//
	// request.addProperty("userId", deviceIntId);
	//
	// SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	// SoapEnvelope.VER11);
	// envelope.dotNet = false;
	//
	// HttpTransportSE ht = new HttpTransportSE(wsdl_url);
	// envelope.bodyOut = request;
	// ht.debug = true;
	// envelope.setOutputSoapObject(request);
	//
	// try {
	// ht.call(null, envelope);
	//
	// if (envelope.getResponse() != null) {
	//
	// SoapObject result = (SoapObject) envelope.bodyIn;
	//
	// int n = result.getPropertyCount();
	//
	// String str = result.toString();
	//
	// Log.d("test", "result= " + result);
	//
	// // <serverport>3060</serverport>
	//
	// String port = str.split("<serverport>")[1]
	// .split("</serverport>")[0];
	//
	// Utils.PLAY_PORT = Integer.parseInt(port);
	//
	// String[] s1 = str.split("return=");
	//
	// String[] s2 = s1[1].split(";");
	//
	// Document doc = getDocument(s2[0]);
	//
	// if (doc != null) {
	// NodeList nl = doc.getElementsByTagName("Deviceinfo");
	//
	// int m = nl.getLength();
	//
	// for (int i = 0; i < nl.getLength(); i++) {
	// org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl
	// .item(i);
	// if (nameElement.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
	//
	// DeviceInfo device = new DeviceInfo();
	// device.setDeviceid(getTextByTagName(nameElement,
	// "id"));
	// device.setDevicename(getTextByTagName(nameElement,
	// "name"));
	// device.setDeviceintid(Integer
	// .parseInt(getTextByTagName(nameElement,
	// "intid")));
	// // device.setDeviceip(getTextByTagName(nameElement,
	// // "deviceip"));
	// device.setDeviceradioport(Integer
	// .parseInt(getTextByTagName(nameElement,
	// "radioport")));
	// device.setDevicevideoport(Integer
	// .parseInt(getTextByTagName(nameElement,
	// "videoport")));
	// device.setDevicestate(Integer
	// .parseInt(getTextByTagName(nameElement,
	// "state")));
	// // device.setDevicetype(Integer.parseInt(getTextByTagName(nameElement,
	// // "devicetype")));
	//
	// _list.add(device);
	// }
	// }
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// // }
	// // });
	// //
	// // thread.start();
	//
	// return _list;
	//
	// }

	private static String getTextByTagName(org.w3c.dom.Element nameElement,
			String name) {
		NodeList nl = nameElement.getElementsByTagName(name);
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				Node n = nl.item(i);
				if (n.getChildNodes().getLength() > 0) {
					String strValue = n.getFirstChild().getNodeValue();
					return strValue;
				} else
					return "";
			}
		}
		return "";
	}

	public static Document getDocument(String xmlString) {
		if (xmlString == null || xmlString == "")
			return null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlString)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String encodeHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * MD5加密方法
	 * 
	 * @param data
	 *            需要加密的数据
	 * @return 加密后的数据
	 */
	public synchronized static final String hashByMD5(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data.getBytes());
			return encodeHex(digest.digest());
		} catch (NoSuchAlgorithmException nsae) {
			System.err.println("Failed to load the MD5 MessageDigest. "
					+ "will be unable to function normally.");
			nsae.printStackTrace();
			return null;
		}
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("testAndroid1", ex.toString());
		}
		return null;
	}

}