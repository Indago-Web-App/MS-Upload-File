package it.linearsystem.indago.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author by Andrea Zaccanti
 * @Created 05 Ottobre 2020
 */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    String path;

}
