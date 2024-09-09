package it.linearsystem.indago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author by Andrea Zaccanti
 * @Created 31 Agosto 2023 
 * 
 */
@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		
		ObjectMapper mapper = builder.build();

//		mapper.getDeserializationConfig();
//		mapper.getSerializationConfig();

//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
//		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
//		mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, false);
		
		//mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
		//mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		//mapper.configure(SerializationFeature.INDENT_OUTPUT, true); // INDENTA BENE IL JSON, ANCHE IL JSON DELL'OUTPUT DI OGNI MICROSERVIZIO oppure mapper.enable(SerializationFeature.INDENT_OUTPUT);	
		//mapper.enable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		
		mapper.setSerializationInclusion(Include.NON_NULL); // OK TOGLIE TUTTE LE KEY-VALUE CON VALUE A NULL
		//mapper.setSerializationInclusion(Include.NON_EMPTY); // OK TOGLIE TUTTE LE KEY-VALUE CON VALUE A NULL
		//mapper.setSerializationInclusion(Include.NON_ABSENT); // OK TOGLIE TUTTE LE KEY-VALUE CON VALUE A NULL

//		mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_MISSING_VALUES, false); // DEPRECATA
		//mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		//mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);		
//		mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);

//    	mapper.configure(com.fasterxml.jackson.databind.MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true); // DEPRECATO CON ARGOMENTO MapperFeature

//    	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false); // N.A.
//		mapper.configure(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_MISSING_VALUES, true); // N.A
		
//		mapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE); // DEPRECATO TUTTA L'ANNOTATION
		
	    return mapper;
	    
	}

}
