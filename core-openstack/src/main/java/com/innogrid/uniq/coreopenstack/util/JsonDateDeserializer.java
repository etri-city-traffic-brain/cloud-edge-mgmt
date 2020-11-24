package com.innogrid.uniq.coreopenstack.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateDeserializer extends JsonDeserializer<Date> {

	private static Logger logger = LoggerFactory.getLogger(JsonDateDeserializer.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Timestamp deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		String date = jsonParser.getText();
		if(date == null || date.equals("")) return null;
		try {
			return new Timestamp(dateFormat.parse(date).getTime());
		} catch (ParseException e) {
			logger.error("Failed to deserialize JSON : '{}'", e.getMessage());
			e.printStackTrace();
		}
		return null;


	}


}
