package com.wxpay.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * 微信支付配置实现类
 * @author xwl
 *
 */
public class WXPayConfigImpl extends WXPayConfig{

    private byte[] certData;//证书数据
    private static WXPayConfigImpl INSTANCE;//当前类
    private static Properties pros = null;//properties
    
    static{
    	pros = new Properties();
    	try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("wxpay.properties"));
		} catch (IOException e) {
		}
    }

    public static void main(String[] args) {
		System.out.println(pros);
	}
    
    //构造方法私有化
    private WXPayConfigImpl() throws Exception{
    	//获取证书文件
        File file = new File(pros.getProperty("cert_path"));
        //获取证书文件流
        InputStream certStream = new FileInputStream(file);
        //初始化证书数据
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        //关闭文件流
        certStream.close();
    }

    //获取该类的唯一方法
    public static WXPayConfigImpl getInstance(){
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    try {
						INSTANCE = new WXPayConfigImpl();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
            }
        }
        return INSTANCE;
    }

    //获取微信公众号appid
    public String getAppID() {
        return pros.getProperty("app_id");
    }

    //获取微信商户id
    public String getMchID() {
        return pros.getProperty("mch_id");
    }
    
    //获取 API 密钥
    public String getKey() {
        return pros.getProperty("api_secret");
    }

    //获取证书流
    public InputStream getCertStream() {
        return new ByteArrayInputStream(this.certData);
    }

    //HTTP(S) 连接超时时间，单位毫秒
    public int getHttpConnectTimeoutMs() {
        return 2000;
    }
    
    //HTTP(S) 读数据超时时间，单位毫秒
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    //获取WXPayDomain, 用于多域名容灾自动切换
    public IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }

    //主域名
    public String getPrimaryDomain() {
        return pros.getProperty("primary_domain");
    }

    //替换域名
    public String getAlternateDomain() {
        return pros.getProperty("alternate_domain");
    }

    public String getUnifiedorderNotifyUrl(){
    	return pros.getProperty("unifiedorder_notify_url");
    }
    
    //进行健康上报的线程的数量
    @Override
    public int getReportWorkerNum() {
        return 1;
    }

    //批量上报，一次最多上报多个数据
    @Override
    public int getReportBatchSize() {
        return 2;
    }
    
}
