package it.linearsystem.indago.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author by Andrea Zaccanti
 * @Created 05 Ottobre 2020
 * 
 */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

//	@Autowired
//    Environment env;
	
	String path;

}
