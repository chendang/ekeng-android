package com.cnnet.otc.health.comm;

public enum CheckType {
	BLOOD_GLUCOSE(),   //血糖仪
	BLOOD_PRESSURE,   //血压计
	THERMOMETER,       //体温计
	OXIMETRY,          //血氧饱和度
	LIPID,            //血脂
	WEIGHT,    //体重
	URIC_ACID,   //尿酸

	LS_WIFI_BLOOD_PRESSURE,//乐心wifi 血压计
	LS_WIFI_WEIGHT,//乐心wifi 体重秤
	NONE;
}
