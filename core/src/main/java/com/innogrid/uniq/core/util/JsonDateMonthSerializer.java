package com.innogrid.uniq.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateMonthSerializer extends JsonSerializer<Date> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");


    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeString(dateFormat.format(value));
    }

}
