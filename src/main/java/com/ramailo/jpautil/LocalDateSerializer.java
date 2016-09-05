package com.ramailo.jpautil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void serialize(LocalDate date, JsonGenerator generator, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		String dateString = date.format(formatter);
		generator.writeString(dateString);
	}
}