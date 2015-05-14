package com.bsht;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.rb_codec.rb_codec;

@SuppressLint({ "NewApi", "SdCardPath" })
class AudioRecoder extends Thread {
	public boolean bThreadFlag = true;
	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	static final int frequency = 44100;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	static final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	private static final boolean VERBOSE = false;
	private static final boolean DEBUG_SAVE_FILE = true;

	int recBufSize;
	AudioRecord audioRecord;
	byte[] buffer = null;
	private int frameSize;
	byte[] mAudioRecordBuffer;

	@SuppressLint("NewApi")
	public void InitAudioRecoder() {
		recBufSize = AudioRecord.getMinBufferSize(frequency, // 8192
				channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				frequency, channelConfiguration, audioEncoding, recBufSize);
		buffer = new byte[recBufSize];
		mAudioRecordBuffer = new byte[frameSize];
	}

	public AudioRecoder() {
		InitAudioRecoder();
	}

	@SuppressLint({ "NewApi", "SdCardPath" })
	public void KillThread() throws IOException {
		Log.e("audio", "this is audioRecord() KillThread");
		try{
			audioRecord.stop();
			Log.e("audio", "this is audioRecord() IOExceptionIOExceptionIOException");
		}catch(IllegalStateException e){
			e.printStackTrace();
			Log.e("audio", "this is audioRecord() IOExceptionIOExceptionIOException" + e.getMessage());
		}

		bThreadFlag = false;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(VERBOSE){
			Log.e("audio", "this is interrupt()!-------");
		}

		this.interrupt();

		audioRecord.release();
		audioRecord = null;

		if(DEBUG_SAVE_FILE){
			copyWaveFile("/sdcard/audio.raw", "/sdcard/new.wav");// 给裸数据加上头文件
			deleteTempFile();
		}

	}

	private void deleteTempFile() {
		File file = new File("/sdcard/audio.raw");
		file.delete();
	}

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	public void run() {
		FileOutputStream fos = null;
		if(DEBUG_SAVE_FILE){
			try {
				File file = new File("/sdcard/audio.raw");
				if (file.exists()) {
					file.delete();
				}
				fos = new FileOutputStream(file);// 建立一个可存取字节的文件
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		audioRecord.startRecording();

		while (bThreadFlag) {
			if(VERBOSE){
				Log.d("audio", "audio run() is start!--------------");
			}

			int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);
			if(VERBOSE){
				Log.d("audio", "bufferReadResult = " + bufferReadResult);
			}

			if (AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult) {
				if(DEBUG_SAVE_FILE){
					try {
						fos.write(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
			rb_codec.GetInstance().EncodePCMToRTP(buffer, bufferReadResult);
		}
		if(DEBUG_SAVE_FILE){
			try {
				fos.close();// 关闭写入流
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(VERBOSE){
			Log.e("audio", "audio run() is over!--------------");
		}

	}

	// 这里得到可播放的音频文件
	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = frequency;
		int channels = 1;
		long byteRate = 16 * frequency * channels / 8;
		byte[] data = new byte[recBufSize];
		try {
			//			(MediaRecorder.VideoEncoder.MPEG_4_SP
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out,
			long totalAudioLen, long totalDataLen, long longSampleRate,
			int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}

	public void parseCommand(byte[] paramArrayOfByte) {
	}
}
