//package it.linearsystem.indago.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.google.gson.FieldNamingPolicy;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
///**
// * 
// * @author by Andrea Zaccanti
// * @Created 05 Settembre 2023 
// * 
// * https://github.com/google/gson/blob/main/UserGuide.md
// * https://howtodoinjava.com/gson/gson-gsonbuilder-configuration/
// * 
// *  <dependency>
// *      <groupId>com.google.code.gson</groupId>
// *      <artifactId>gson</artifactId>
// *      <version>2.10.1</version>
// *      <scope>compile</scope>
// *  </dependency>
// * 
// * 	@Autowired
// *	Gson gson;
// * 
// */
//@Configuration
//public class GsonConfig {
//
//	@Bean
//	public Gson gson(GsonBuilder builder) {
//		
//		Gson mapper = builder
//			.serializeNulls() // INCLUDE ANCHE I CAMPI VALORIZZATI A NULL
//			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//			.setPrettyPrinting() // INDENTA IL JSON
//			.create();
//		
//	    return mapper;
//	    
//	}
//
//}
