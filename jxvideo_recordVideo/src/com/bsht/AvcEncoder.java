package com.bsht;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

@SuppressLint("NewApi")
public class AvcEncoder {
	private static final String TAG = "EncodeDecode";
	private static final boolean VERBOSE = false; // lots of logging
	private static final boolean DEBUG_SAVE_FILE = true;
	private static final int NUM_FRAMES = 30; // two seconds of video

	private MediaCodec m_mediaCodec;
	private int m_width = -1;
	private int m_height = -1;
	private int m_bitRate = -1;
	private int m_frameRate = -1;
	private int m_colorFormat = -1;
	private byte[] yuv420 = null;
	private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video
	private static final int IFRAME_INTERVAL = 1; // 1 seconds between
	private BufferedOutputStream outputStream; // I-frames

	@SuppressLint("NewApi")
	public AvcEncoder() {
		if (DEBUG_SAVE_FILE) {
			File f = new File(Environment.getExternalStorageDirectory(),
					"test.264");
			// touch (f);
			try {
				outputStream = new BufferedOutputStream(new FileOutputStream(f));
				Log.i("AvcEncoder", "outputStream initialized");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		m_mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);

	}

	@SuppressLint("NewApi")
	public void Init(int width, int height, int framerate, int bitrate) {
		// setParameters(width, height, bitrate, framerate);
		setParameters(width, height, bitrate, m_frameRate); // for test
		yuv420 = new byte[width * height * 3 / 2];

		MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE,
				m_width, m_height);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, m_bitRate);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 29);

		MediaCodecInfo codecInfo = selectCodec(MIME_TYPE);
		m_colorFormat = selectColorFormat(codecInfo, MIME_TYPE);
//		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, m_colorFormat);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, m_colorFormat);
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,
				IFRAME_INTERVAL);
		m_mediaCodec.configure(mediaFormat, null, null,
				MediaCodec.CONFIGURE_FLAG_ENCODE);
		m_mediaCodec.start();
	}

	@SuppressLint("NewApi")
	public void close() {
		try {
			m_mediaCodec.stop();
			m_mediaCodec.release();
			if (DEBUG_SAVE_FILE) {
				outputStream.flush();
				outputStream.close();
				outputStream = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static final double TIMEOUT_US = 1000000 * 0.5; // 500ms

	@SuppressLint("NewApi")
	public int offerEncoder(byte[] input, byte[] output) {
		final int TIMEOUT_USEC = 10000;
		int length = -1;
		YV12toYUV420PackedSemiPlanar(input, yuv420, m_width, m_height);
//		swapYV12toI420(input, yuv420, m_width, m_height);

		try {
			ByteBuffer[] inputBuffers = m_mediaCodec.getInputBuffers();
			ByteBuffer[] outputBuffers = m_mediaCodec.getOutputBuffers();
			int inputBufferIndex = m_mediaCodec.dequeueInputBuffer(0);
			if (inputBufferIndex >= 0) {
				ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
				inputBuffer.clear();
				inputBuffer.put(yuv420);
				m_mediaCodec.queueInputBuffer(inputBufferIndex, 0,
						yuv420.length, (System.currentTimeMillis()) * 1000, 0);
			}

			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			int outputBufferIndex = -1;
			int startPos = 0;
			while (true) {
				outputBufferIndex = m_mediaCodec.dequeueOutputBuffer(
						bufferInfo, 0);
				if (VERBOSE) {
					Log.d(TAG, "Queue Buffer out " + outputBufferIndex);
				}
				if (outputBufferIndex >= 0) {
					ByteBuffer buffer = outputBuffers[outputBufferIndex];
					if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
						// Config Bytes means SPS and PPS
						if (VERBOSE) {
							Log.d(TAG, "Got config bytes");
						}
					}
					if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0) {
						// Marks a Keyframe
						if (VERBOSE) {
							Log.d(TAG, "Got Sync Frame");
						}
					}
					if (bufferInfo.size != 0) {
						if (buffer == null) {
							throw new RuntimeException("encoderOutputBuffer "
									+ outputBufferIndex + " was null");
						}
						buffer.position(bufferInfo.offset);
						buffer.limit(bufferInfo.offset + bufferInfo.size);
						byte[] outData = new byte[bufferInfo.size];
						buffer.get(outData);
						if (DEBUG_SAVE_FILE) {
							outputStream.write(outData, 0, outData.length);
						}
						System.arraycopy(outData, 0, output, startPos,
								outData.length);
						length = outData.length + startPos;
						if (VERBOSE) {
							Log.d(TAG, "write  " + outData.length
									+ " bytes to file" + "  length = " + length);
						}
						startPos += length;
					}
					m_mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
					if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
						// Stream is marked as done,
						// break out of while
						if (VERBOSE) {
							Log.d(TAG, "Marked EOS");
						}
						break;
					}
					// outputBufferIndex =
					// m_mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
				} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
					outputBuffers = m_mediaCodec.getOutputBuffers();
					if (VERBOSE) {
						Log.d(TAG, "Output Buffer changed " + outputBuffers);
					}

				} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
					MediaFormat newFormat = m_mediaCodec.getOutputFormat();
					if (VERBOSE) {
						Log.d(TAG, "Media Format Changed " + newFormat);
					}
				} else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
					// No Data, break out
					if (VERBOSE) {
						Log.d(TAG, "No Data, break out");
					}
					break;
				} else {
					// Unexpected State, ignore it
					if (VERBOSE) {
						Log.d(TAG, "Unexpected State " + outputBufferIndex);
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return length;
	}

	
	public static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final byte[] output, final int width, final int height) {
	    /* 
	     * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12
	     * We convert by putting the corresponding U and V bytes together (interleaved).
	     */
	    final int frameSize = width * height;
	    final int qFrameSize = frameSize/4;

	    System.arraycopy(input, 0, output, 0, frameSize); // Y

	    for (int i = 0; i < qFrameSize; i++) {
	        output[frameSize + i*2] = input[frameSize + i + qFrameSize]; // Cb (U)
	        output[frameSize + i*2 + 1] = input[frameSize + i]; // Cr (V)
	    }
	    return output;
	}
	
	// yv12 转 yuv420p yvu -> yuv
	private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width,
			int height) {
		System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
		System.arraycopy(yv12bytes, width * height + width * height / 4,
				i420bytes, width * height, width * height / 4);
		System.arraycopy(yv12bytes, width * height, i420bytes, width * height
				+ width * height / 4, width * height / 4);
	}

	private void setParameters(int width, int height, int bitRate, int frameRate) {
		if ((width % 16) != 0 || (height % 16) != 0) {
			Log.w(TAG, "WARNING: width or height not multiple of 16");
		}
		m_width = width;
		m_height = height;
		m_bitRate = bitRate;
		m_frameRate = frameRate;
	}

	@SuppressLint("NewApi")
	private static MediaCodecInfo selectCodec(String mimeType) {
		int numCodecs = MediaCodecList.getCodecCount();
		for (int i = 0; i < numCodecs; i++) {
			MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
			if (!codecInfo.isEncoder()) {
				continue;
			}
			String[] types = codecInfo.getSupportedTypes();
			for (int j = 0; j < types.length; j++) {
				if (types[j].equalsIgnoreCase(mimeType)) {
					return codecInfo;
				}
			}
		}
		return null;
	}

	private static int selectColorFormat(MediaCodecInfo codecInfo,
			String mimeType) {
		MediaCodecInfo.CodecCapabilities capabilities = codecInfo
				.getCapabilitiesForType(mimeType);
		for (int i = 0; i < capabilities.colorFormats.length; i++) {
			int colorFormat = capabilities.colorFormats[i];
			if (isRecognizedFormat(colorFormat)) {
				return colorFormat;
			}
		}
		// fail("couldn't find a good color format for " + codecInfo.getName() +
		// " / " + mimeType);
		return 0; // not reached
	}

	private static boolean isRecognizedFormat(int colorFormat) {
		switch (colorFormat) {
		// these are the formats we know how to handle for this test
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
			return true;
		default:
			return false;
		}
	}

	private static boolean isSemiPlanarYUV(int colorFormat) {
		switch (colorFormat) {
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
			return false;
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
			return true;
		default:
			throw new RuntimeException("unknown format " + colorFormat);
		}
	}

}
