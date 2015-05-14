package com.bsht;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.json.JSONObject;
import org.json.JSONStringer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UnicomActivity extends Activity {

	// 十进制
	// String modulus =
	// "95712792088598846953110119101697811019859719460012036627539001471600396723761238062609843032363965277332317285054542966489695054194256813743897856044333375921925676794518130104741531616376185915509275210251163761860002741139948634172481088707342852832221242549418908919859978775749104539233212538758936501033";
	//
	// String publicExponent = "92867";
	//
	// String privateExponet =
	// "19689417985512532677831907085604520246410458834290651660250735827726253448595676526086752466325833640132195391405795264537662833033554246069792548934184849615121880765669925146530385525243029211551441392849992411421786687184588445298582612022533604778553999373312464914136179879085061299100508023879783858539";

	// 十六进制
	String modulus = "884CAEAB0CDFEB160D53FA7A8EE84359C6F99FE94AF262AE9889544D6CB17B068166C3EFA4F21CE6AF8EE3619D3DA1D86D59128479D389A6E1EC5A3DCD8771C0480D1638905FD924B59B41FC34B851BA3B2D1202C7C32723E8F420C640CAB0A012FE3BCC8A11C7FCF142D914385AB29BA3272A9044A1DAE3EBA9EF47104D9B29";

	String publicExponent = "16AC3";

	String privateExponet = "1C09E5BF5EA97D07D337AAC6A47D48FB5EDEF0BD872574526A63008E9997822DB1444807AF19F3E95668CF57F7D5C9DEB97053C60CD6A302E6D2876265E51F1EC93C527B6EA946097FAF76948674EC110B3D82FCE89C674FFFFCCD6315776238FA6B5E8DA0EB8B7E3BE8555E2A47FE71D9AE7BBCEBF6B63BEBFBBEF778DEC16B";

	private static final String KEY_ALGORITHM = "RSA";

	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static final String PUBLIC_KEY = "RSAPublicKey";

	private static final String PRIVATE_KEY = "RSAPrivateKey";

	private static final int MAX_ENCRYPT_BLOCK = 117;

	private static final int MAX_DECRYPT_BLOCK = 128;

	private static String ORDERTOKEN = "";

	String KEY = "30819F300D06092A864886F70D010101050003818D0030818902818100843512CBDB5566792273F9A7D2C7E889E76024C89E58E539D89A5D4993A5AFA6333D6A29C4FB26D6C3233880646AF6AF66F5F86F2F9179387D23724449425251083C7914FD10957F83407BD124014CDE94A2B789424769475D4F67CF8DE456B93BC90EDE33BA681884EF53411CC0BB0723F7528628583CACCF66C843F62781B10203010001";
	String P_KEY = "30820275020100300D06092A864886F70D01010105000482025F3082025B02010002818100A09553C149BB1025932CF5057DBACAC7C7C96E491DCD9339EB6CDC3889C230F306D3B00561B30ED10119CDDC8DFEF9C373F7FF99BB5D6E0A4C9DF15F39F4FF22DDEF1DCBBDFBBE63D35589FA90AE072632C18F504A3916C63F869B8483BE8FDFB22A2A1EEBAF98FD56D6C8B9E5025E30E6B46D98B35741928530BF4B1BE5257F020301000102818049885793F8E939D99AED493DBC771E88210A3200352F7FAD540AC344F87FFDDE2B50201836C6C1901F4AEBA3853A2EB0BB97E269490CDD68C5F0C5286FE7AA34594311707BE9883DC9EA8BC76810D4F99708B2C072AA712D8C6B603B9F560BBFAE0A32F52C199C5124D1CD867481CF5486F643179CB63C8D292AB26723D774E1024100D3BEE8E25A09CD8AB5F9E73DC6AF6C8614D2F288D8DE95A766B8D0BC14D561630CDC52518CCDC9D1C800033CBF709C7FAF1C299502682F2751975345304693C7024100C2250F548596D57895126DFC3D7A70D6EAADAE1ECCCAF46272283A102DA7CB6EE578B10FE641175DA3A98E913E8B5C31907E2973988559F18AC83951BCBF70890240010B28D4ACC2ED9B686831E6910B1E4ACEE177468B631573BCC2DE6C7EE1CA5815A1245AB889EF1BB493A370723518E05C39944EE2B326C057DB9A12763972D3024006562550F1E7416265232BD9CEBBDF50F469E4C3F6952D32C55D10C0146D64936303F46B7B6D6670D4F5A4C492D1039A4E87FF9C593F08F77C3B1ABBD33840590240494C1FAE882E93282B5CE8F07508EF552677F24D095132080B4463ADDB4F00D9AE2D43B1E2696BCFCE54B36140C75ECB6FDE6D8727FB7055845A13F501554A3B";

	SharedPreferences _lastSp;

	private static final int MSG_SUCCESS = 101;
	private static final int MSG_FAILED = 102;
	
	String _deviceid ,_telNum;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.unicom);

		_lastSp = this.getSharedPreferences("lastSp", MODE_PRIVATE);

		TelephonyManager tm = (TelephonyManager) this.getSystemService(UnicomActivity.TELEPHONY_SERVICE);
		_deviceid = tm.getDeviceId();
	    _telNum = tm.getLine1Number();
//		String imei =tm.getSimSerialNumber();
//		String imsi =tm.getSubscriberId();
		
		
		Button back = (Button) findViewById(R.id.back_button);

		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				UnicomActivity.this.finish();

			}
		});

		Button order = (Button) findViewById(R.id.order);
		Button unOrder = (Button) findViewById(R.id.unsubscribe);
		Button query = (Button) findViewById(R.id.query);

		order.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						testHttpUrlCon(1);
					}
				});
				thread.start();
			}
		});
		unOrder.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Thread thread = new Thread(new Runnable() {
					public void run() {
						testHttpUrlCon(2);
					}
				});
				thread.start();

			}
		});
		query.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				testHttpUrlCon(3);
			}
		});
	}

	public Handler openViewHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == MSG_SUCCESS) {
				ShowDialog((String) msg.obj);
			}
		}
	};

	public void testHttpUrlCon(int num) {
		String name = "zh";

		// 861462010066629
		// String deviceId = ((TelephonyManager) getSystemService("phone"))
		// .getDeviceId();
		//
		// Log.d("test", "deviceId = " + deviceId);

		// {"errorinfo":"","ordertoken":"0EF1AD5ABCE766FDD1969C0BE4D832CF0F4EE582967A601C16EA3CD87C573219A21E3CCFFD0F51E49BD4B9F6D242A06B547A60A394DB1FA751F06A2285BA2F09EF266F01E7171A51B7CBD0915764965EA7612B87E89F784EAB27A25D6FCD33DF10E45998BB94C92C9E57284CE1544342C5375E6862DA31E7CE3A4CF6569537BE","resultcode":"0"}


		
		try {

			byte[] data = null;

			if (num == 1) {
				data = getOrderData().getBytes();
			} else if (num == 2) {
				data = getUnOrderData().getBytes();
			} else if (num == 3) {
				data = getQueryData().getBytes();
			}

			URL url = new URL("http://114.255.201.228:86/app-video/app800.do");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("content-type", "text/plain");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.connect();

			OutputStream outStream = conn.getOutputStream();
			outStream.write(data);
			outStream.flush();
			outStream.close();

			InputStream input = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			String line;
			StringBuilder inputsb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				inputsb.append(line);
			}

			String result = inputsb.toString();
			Log.d("test", "result = " + result);

			// result =
			// "{\"errorinfo\":\"\",\"ordertoken\":\"0EF1AD5ABCE766FDD1969C0BE4D832CF0F4EE582967A601C16EA3CD87C573219A21E3CCFFD0F51E49BD4B9F6D242A06B547A60A394DB1FA751F06A2285BA2F09EF266F01E7171A51B7CBD0915764965EA7612B87E89F784EAB27A25D6FCD33DF10E45998BB94C92C9E57284CE1544342C5375E6862DA31E7CE3A4CF6569537BE\",\"resultcode\":\"0\"}";

			JSONObject jsonObject = new JSONObject(result);

			String resultcode = (String) jsonObject.get("resultcode");

			if (resultcode.equals("0")) {
				String errorInfo = "";
				if (num == 1) {
					errorInfo = "订购成功";
				} else if (num == 2) {
					errorInfo = "退订成功";
				} else {

				}

				Message msg = new Message();
				msg.arg1 = MSG_SUCCESS;
				msg.obj = errorInfo;
				openViewHandler.sendMessage(msg);

				String orderToken = (String) jsonObject.get("ordertoken");

				String str = decryptByPublicKey(orderToken, KEY);

				Editor editor = _lastSp.edit();
				editor.putString("ordertoken", str);
				editor.commit();
			} else {
				String errorInfo = (String) jsonObject.get("errorinfo");

				Message msg = new Message();
				msg.arg1 = MSG_SUCCESS;
				msg.obj = errorInfo;
				openViewHandler.sendMessage(msg);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getOrderData() throws Exception {

		StringBuffer json = new StringBuffer("{\"cpid\":\"yangtian\",\"data\":");
		
		String str = "{\"busiid\":\"order\",\"deviceid\":\""+_deviceid+"\",\"userid\":\""+_telNum+"\",\"cardtype\":\"100\"}";
		json.append("\"");
		// json.append("AD76A4CE67470F3DE724779B4B21A878C7B5182590D1B519AFDF2BC6D34B2C013C6CE4ABB97CD6B72EE054A894B1793932F2B276BF44E170C08FC73B0B773EC0FE35CC8C13683F4036E60B02BDDDFA6847377606441671F11610BC5ACCFE5F4FFFB5DBD0D3CA61DC608EDEDBB2811082FF2EB76D02865418037AB751900830C7");

		String s1 = encryptByPublicKey(str, KEY);

		try {
			json.append(s1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json.append("\"");
		json.append("}");

		Log.d("test", "json = " + json.toString());

		return json.toString();
	}

	private String getUnOrderData() {
		StringBuffer json = new StringBuffer("{\"cpid\":\"yangtian\",\"data\":");

		String orderToken = _lastSp.getString("ordertoken", "");

		String str = "{\"busiid\":\"cancelOrder\",\"userid\":\""+_telNum+"\",\"ordertoken\":\""
				+ orderToken + "\"}";

		Log.d("test", "user = " + str);

		json.append("\"");
		String s1 = null;
		try {
			s1 = encryptByPublicKey(str, KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.append(s1);
		json.append("\"");
		json.append("}");

		Log.d("test", "json = " + json.toString());
		return json.toString();
	}

	private String getQueryData() {
		StringBuffer json = new StringBuffer("{\"cpid\":\"yangtian\",\"data\":");

		String orderToken = _lastSp.getString("ordertoken", "");

		String str = "{\"ordertoken\":\"" + orderToken + "\"}";

		Log.d("test", "user = " + str);

		json.append("\"");
		String s1 = null;
		try {
			s1 = encryptByPublicKey(str, KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.append(s1);
		json.append("\"");
		json.append("}");

		Log.d("test", "json = " + json.toString());
		return json.toString();
	}

	public static String decryptByPublicKey(String data, String publicKey)
			throws Exception {
		byte[] keyBytes = parseHexStr2Byte(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, publicK);
		byte[] encryptedData = parseHexStr2Byte(data);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher
						.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher
						.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return new String(decryptedData, "UTF-8");
	}

	public static String encryptByPublicKey(String data, String publicKey)
			throws Exception {
		byte[] keyBytes = parseHexStr2Byte(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密 RSA/ECB/PKCS1Padding
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		byte[] decryptedData = data.getBytes("UTF-8");
		int inputLen = decryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher
						.doFinal(decryptedData, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher
						.doFinal(decryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return parseByte2HexStr(encryptedData);
	}

	public static String parseByte2HexStr(byte[] buf) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1) {
			return null;
		}
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public String RSAEncrypt(String str) {
		String result = "";
		try {

			PublicKey publicKey = getPublicKey(modulus, publicExponent);

			PrivateKey privateKey = getPrivateKey(modulus, privateExponet);

			// 加解密类
			Cipher cipher = Cipher.getInstance("RSA");

			// 明文
			byte[] plainText = str.getBytes();

			Log.d("test", "加密 = " + str);
			// 加密
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] enBytes = cipher.doFinal(plainText);

			result = enBytes.toString();

			// 解密
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] deBytes = cipher.doFinal(enBytes);

			String s = new String(deBytes);

			Log.d("test", "解密 = " + s);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public PublicKey getPublicKey(String modulus, String publicExponent)
			throws Exception {

		BigInteger m = new BigInteger(modulus, 16);

		BigInteger e = new BigInteger(publicExponent, 16);

		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		return publicKey;

	}

	public PrivateKey getPrivateKey(String modulus, String privateExponent)
			throws Exception {

		BigInteger m = new BigInteger(modulus, 16);

		BigInteger e = new BigInteger(privateExponent, 16);

		RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		return privateKey;

	}

	public void ShowDialog(String str) {
		AlertDialog.Builder builder = new Builder(UnicomActivity.this);
		builder.setMessage(str);

		builder.setTitle("提示");

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
