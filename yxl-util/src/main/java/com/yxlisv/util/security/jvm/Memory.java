package com.yxlisv.util.security.jvm;

import java.lang.management.ManagementFactory;

public class Memory {
	
	//清理内存最大循环次数
	private static int maxRestoreJvmLoops = 10;

	/**
	 * 回收内存
	 * @autor yxl
	 */
	public static String restoreJvm() {
		
		String vStr = "restoreJvm...";
		long memUsedPrev = memoryUsed();
		for (int i = 0; i < maxRestoreJvmLoops; i++) {
			System.runFinalization();
			System.gc();

			long memUsedNow = memoryUsed();
			vStr += "  " + memUsedNow;
			// 如果多次GC后内存稳定了，就退出
			if ((ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount() == 0) && (memUsedNow >= memUsedPrev)) {
				break;
			} else {
				memUsedPrev = memUsedNow;
			}
		}
		//System.out.println(vStr);
		return vStr;
	}

	/**
	 * 得到内存占用量
	 * @autor yxl
	 */
	public static long memoryUsed() {
		Runtime rt = Runtime.getRuntime();
		long memUsedNow = rt.totalMemory() - rt.freeMemory();
		return memUsedNow;
	}
}
