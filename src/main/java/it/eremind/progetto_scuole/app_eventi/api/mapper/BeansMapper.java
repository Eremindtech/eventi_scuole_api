package it.eremind.progetto_scuole.app_eventi.api.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;


public class BeansMapper {
	
	private static Logger logger=LoggerFactory.getLogger(BeansMapper.class.getName());
	private static SimpleDateFormat twsDateFormat = new SimpleDateFormat("yyyyMMdd  HH:mm:ss");
	
	/*public static Trade toDb(TwsTrade dto, Trade db){
		if(db==null)
			db=new Trade();		
		BeanUtils.copyProperties(dto, db);
		db.setAccountId(dto.getAccount());
		try {
			db.setExecTime(twsDateFormat.parse(dto.getTime()));
		} catch (ParseException e) {
			logger.warn("toDb-trade exec time parsing error.",e);
		}
		return db;
	}
	
	public static TwsTrade toTwsDto(Trade db, TwsTrade dto){
		if(dto==null)
			dto=new TwsTrade();
		BeanUtils.copyProperties(db, dto);
		dto.setAccount(db.getAccountId());
		return dto;
	}*/
	



}
