package com.wxpay.sdk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class WXPayService {

	private static ThreadLocal<WXPay> threadLocal = new ThreadLocal<WXPay>();
	public static WXPayConfigImpl config=WXPayConfigImpl.getInstance();
	
	/**
	 * 获取wxpay
	 * @return
	 * @throws Exception
	 */
	public static WXPay getWXPay() throws Exception{
		WXPay wxpay = threadLocal.get();
		if(null==wxpay){
			wxpay= new WXPay(config);
			threadLocal.set(wxpay);
		}
		return wxpay;
	}
	
	/**
	 * 统一下单接口
	 * @param body 商品描述
	 * @param total_fee 商品金额
	 * @param trade_type 交易类型 NATIVE,JSAPI,APP
	 * @param spbill_create_ip APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> unifiedorder(String body,
			String total_fee,String trade_type,String spbill_create_ip) throws Exception{
		WXPayUtil.getLogger().info("微信统一下单接口开始：");
		//组装请求参数
		Map<String,String> reqData = new HashMap<String,String>();
		reqData.put("body", body);
		reqData.put("total_fee", total_fee);
		reqData.put("trade_type", trade_type);
		reqData.put("out_trade_no", getWXOrderNo());
		reqData.put("spbill_create_ip", spbill_create_ip);
		reqData.put("notify_url", config.getUnifiedorderNotifyUrl());
		WXPayUtil.getLogger().info("微信统一下单接口请求参数："+reqData);
		//调用统一下单接口
		Map<String,String> result = getWXPay().unifiedOrder(reqData);
		WXPayUtil.getLogger().info("微信统一下单接口请求结果："+result);		
		WXPayUtil.getLogger().info("微信统一下单接口结束！");		
		return result;
	}
	
	/**
	 * 查询订单
	 * @param orderNo 订单号
	 * @param type 订单号类型 wx:微信自己的订单号 self：商户自己生成的订单号
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> orderQuery(String orderNo,String type) throws Exception{
		WXPayUtil.getLogger().info("微信查询订单接口开始,订单号："+orderNo);
		Map<String,String> reqData = new HashMap<String,String>();
		if("wx".equals(type)){
			reqData.put("transaction_id", orderNo);
		}else if("self".equals(type)){
			reqData.put("out_trade_no", orderNo);
		}
		Map<String,String> result = getWXPay().orderQuery(reqData);
		WXPayUtil.getLogger().info("微信查询订单接口结果："+result);
		return result;
	}
	
	public static void main(String[] args) {
		try {
			Map<String,String> result = orderQuery("wx_order_*20180305112809820", "self");
			System.out.println(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生产订单号
	 * @return
	 */
	private static String getWXOrderNo(){
		String prefix = "wx_order_*";
		String content = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		return prefix+content;
	}
}
