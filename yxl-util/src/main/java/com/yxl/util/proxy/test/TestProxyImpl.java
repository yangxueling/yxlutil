package com.yxl.util.proxy.test;

/**
 * 測試代理實現
 * @createTime 2016年1月20日 下午3:06:38 
 * @author yxl
 */
public class TestProxyImpl implements TestProxy {

	@Override
	public void println() {
		System.out.println("TestProxyImpl println...");
	}

}
