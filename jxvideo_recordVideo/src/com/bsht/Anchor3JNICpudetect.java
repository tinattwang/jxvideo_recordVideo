package com.bsht;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Anchor3JNICpudetect {

	/*
    static {
        System.loadLibrary("cpudetect");
    }*/
	private String sz_cpuinfo;
	
	public Anchor3JNICpudetect(){
		File f_cpuinfo = new File("/proc/cpuinfo");
		try{
			FileInputStream input = new FileInputStream(f_cpuinfo);
			byte[] array_cpuinfo = new byte[1024*8];
			int readed = input.read(array_cpuinfo);
			input.close();
			sz_cpuinfo = new String(array_cpuinfo, 0, readed);
//			Utility.LogD("cpu_info", sz_cpuinfo);
		}
		catch (IOException ioe){
		}
	}
	
	private String getTagString(String tag){
		String content = "";
		String[] lines = sz_cpuinfo.split("\n");
		for (String line : lines){
			String[] values = line.split(":");
			String title = "";
			if (values.length > 1){
				title = values[0].trim();
				if (title.contentEquals(tag)){
					content = values[1].trim();
					break;
				}
			}
		}
		return content;
	}
	
	private boolean getItem(String content, String item){
		boolean b_has = false;
		String[] items = content.split("\\s+");
		for (String it : items){
			String it_trim = it.trim();
			if (it_trim.equalsIgnoreCase(item)){
				b_has = true;
				break;
			}
		}
		return b_has;
	}
	
	public String getCPUFamily(){
		String cpu_family = getTagString("Processor");
		String family = cpu_family.substring(0, 5);
		return family;
	}
	
	public String getCPUArch(){
		String arch = getTagString("CPU architecture");
		return arch;
	}
    
	public String getCPUFeature(){
		String feature = getTagString("Features");
		return feature;
	}
	
	public boolean isSupportNeon(){
		String feature = getTagString("Features");
		return getItem(feature, "neon");
	}
	
	public boolean isSupportVFPV3(){
		String feature = getTagString("Features");
		return getItem(feature, "vfpv3");
	}
	
	public boolean isSupportVFP(){
		String feature = getTagString("Features");
		return getItem(feature, "vfp");
	}
}
