package com.chen.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {
	public static final String accessKeyId = "LTAI5t5cXpYTz17DNJDCcThm";
	public static final String accessKeySecret = "Dx0VG55ZpFldgO2EhKIHHHkae0MM7W";
	public static final String signName = "贸游兔";
	public static final String templateCode = "SMS_460775155";
	public static final String regionId = "cn-hangzhou";

	public static void smsAliyun(String phone, String code){
		DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
		IAcsClient client = new DefaultAcsClient(profile);
		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId(regionId);
		request.setPhoneNumbers(phone);
		request.setSignName(signName);
		request.setTemplateCode(templateCode);
		request.setTemplateParam("{\"code\":\"" + code + "\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功");
		}catch (ClientException e) {
			e.printStackTrace();
		}
	}

}
