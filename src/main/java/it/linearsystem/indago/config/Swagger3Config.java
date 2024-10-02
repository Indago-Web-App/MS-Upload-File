package it.linearsystem.indago.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by Andrea Zaccanti
 * Date 17 Agosto 2024
 */
//@EnableSwagger2
@Configuration
//@ComponentScan(basePackageClasses = {ClusterController.class})
public class Swagger3Config {

    @Bean
    public OpenAPI openAPI() {
        Map<String, Object> extensions = new HashMap<String, Object>();
//    	extensions.put("Compilazione", "Andrea Zaccanti");
//    	extensions.put("Approvazione", "Andrea Taverna");
//    	extensions.put("Ultima modifica", "25 Novembre 2022");
        Contact contact = new Contact();
        contact.addExtension("Compilazione", "Andrea Zaccanti");
        contact.addExtension31("Approvazione", "Andrea Zaccanti");
        contact.email("andrea.zaccanti@linearsystem.it");
        contact.email(""); // SFORMATTA LA TERZA PAGINA abstract E OLTRE A NON SCRIVERE L'EMAIL, NON SCRIVE NEANCHE LA VERSIONE
        contact.email(null); // NON SCRIVE L'EMAIL NE LA VERSIONE
        contact.email("------------------------------ Compilazione: Andrea Zaccanti ------------------------------ "
                + "------------------------------ Approvazione: Andrea Zaccanti ------------------------------ "
                + "------------------------------ Ultima modifica: 17 Agosto 2024 -----------------------");
        contact.name("Name");
//    	contact.url("URL"); // -attribute info.contact.URL
        return new OpenAPI()
                .info(new Info()
                        .title("Microservizio Indago WebApp")
                        .description("Questo microservizio si occupera' di gestire le distribuzioni nel sistema MDM")
                        .version("Version 1.0.0")
                        .contact(contact)
                        .summary("Summary")
                        .extensions(extensions)
                        .termsOfService("termsOfService")
                )
                ;
    }

}
