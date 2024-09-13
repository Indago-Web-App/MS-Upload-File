package it.linearsystem.indago.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.linearsystem.indago.bean.dto.DataAssetRequest;
import it.linearsystem.indago.bean.dto.FileProcessDto;
import it.linearsystem.indago.bean.dto.FileValidityDto;
import it.linearsystem.indago.bean.dto.response.ResponseDto;
import it.linearsystem.indago.bean.dto.response.UploadFileRequest;
import it.linearsystem.indago.bean.dto.response.UploadFileResponse;
import it.linearsystem.indago.config.FileUploadConfig;
import it.linearsystem.indago.openmetadata.OpenMetadataClient;
import it.linearsystem.indago.service.FileProcessService;
import it.linearsystem.indago.service.FileUploadService;
import it.linearsystem.indago.service.OpenMetadataService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * http://localhost:8080/Indago/indago/
 *
 * @author by Andrea Zaccanti
 * @Created 05 Maggio 2020
 * @Updated 15 Giugno 2023
 */
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/indago")
//@Validated
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);


    @Value("${openmetadata.server.url}")
    String openmetadataServerURL;

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileProcessService fileProcessService;


    // OPEN METADATA
    @Value("${openmetadata.auth.token}")
    private String authToken;
    @Autowired
    private OpenMetadataClient openMetadataClient;
    @Autowired
    private OpenMetadataService openMetadataService;


//	@Autowired
//	FilesStorageService storageService;

    @Autowired
    FileUploadConfig multimediaConfig;

//	@Autowired
//	public TemplateController(TemplateConfig templateConfig) {
//		log.debug("TemplateController - Costruttore");
//		this.multimediaConfig = templateConfig;
//	}

    @Operation(
            summary = "/indago/uploadFile",
            description = "Esegue l'upload multiplo di foto e video raggruppati per ogni ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ad inserimento avvenuto con successo ritorna una risposta con un risultato positivo."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "In caso di errore di validazione viene ritornata una risposta con il codice e la descrizione d'errore relativo."
                    )//,
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "In caso di cluster non trovato relativo all'identificativo viene ritornata una risposta con il codice d'errore relativo."
//                    )
            })
    @PostMapping(
            value = "/uploadFile"
            , headers = {"Content-Type=multipart/form-data", "Accept=application/json"}
            , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.MULTIPART_MIXED_VALUE}
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
//	@PermitAll
//	@DenyAll // Exception: org.springframework.security.access.AccessDeniedException: Access Denied
    @RolesAllowed("ADMIN")
    // ("USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied
//	@Secured("ROLE_ADMIN") // ("ROLE_USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied
//	@PreAuthorize("hasAuthority('ROLE_ADMIN')") // ("ROLE_USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied
//	@PreAuthorize("hasRole('ROLE_ADMIN')") // ("ROLE_USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied
    public ResponseEntity<ResponseDto<UploadFileResponse>> uploadFile(
            @Valid
            UploadFileRequest uploadFileRequest) {

        logger.info("INIZIO uploadFile - uploadFileRequest: " + uploadFileRequest);
        FileValidityDto fileValidityDto;
        try {
            fileValidityDto = fileUploadService.checkValidationFile(uploadFileRequest);
            if (!fileValidityDto.getIsValid()) {
                return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().error("400", fileValidityDto.getMessage(), null).build());
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().error("400", e.getMessage(), null).build());
        }

        List<String> urlMultimedias = new ArrayList<>();

        logger.info("INIZIO uploadFile: " + fileValidityDto.getNameFile());
        logger.info("INIZIO uploadFile - file.getSize: " + fileValidityDto.getSize());

//		try {
//			String urlMultimedia = storageService.save("E", "1", uploadFileRequest.getFile());
//			
//			urlMultimedias.add( urlMultimedia );
//
//		} catch ( IOException e ) {
//			log.log(Level.SEVERE, "ERROR uploadMultiFile: " + e.getMessage());
//			urlMultimedias.add( "Errore uploadMultiFile: " + uploadFileRequest.getFile().getOriginalFilename());
//		}

        FileProcessDto fileProcessed;
        try {
            fileProcessed = fileProcessService.processFile(fileValidityDto);
        } catch (IOException e) {
            return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().error("400", e.getMessage(), null).build());
        }


        logger.info("### OPEN METADATA ###");
        openMetadataService.processFile(fileProcessed);
        logger.info("### FINEEE OPEN METADATA ###");

        /*openMetadataService.testOpenMetadataService();*/
        // Call OpenMetadata API
        try {
            DataAssetRequest request = new DataAssetRequest();


            /*responseOpenMetada = openMetadataClient.createDataAsset(request, "Bearer " + authToken);*/

            ResponseEntity<?> respEntityBots = openMetadataClient.getListBots("Bearer " + authToken);
            if (respEntityBots.getStatusCode().value() == 200) {

                logger.info("Body info: ", respEntityBots.getBody());
            }

            if (respEntityBots.getStatusCode().is2xxSuccessful()) {
                UploadFileResponse response = UploadFileResponse.builder()
                        .resultCode(0)
                        .resultMessage(null)
                        .urlMultimedias(urlMultimedias)
                        .build();
                return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().noinfo(response).build());
            } else {
                UploadFileResponse response = UploadFileResponse.builder()
                        .resultCode(respEntityBots.getStatusCode().value())
                        .resultMessage("Errore sconosciuto openMetadata")
                        .urlMultimedias(urlMultimedias)
                        .build();
                return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().error(String.valueOf(respEntityBots.getStatusCode().value()), "Stato non conosciuto", response).build());
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().error("400", e.getMessage(), null).build());
        }


//	    OpenMetadataClient client = new OpenMetadataClient();

        /*        OpenMetadataConnection server = new OpenMetadataConnection();*/
//	    OpenMetadataServerConnection server = new OpenMetadataServerConnection();
    /*    server.setHostPort(openmetadataServerURL); // http://localhost:{port}}/api
        server.setApiVersion("v1");*/
//	    server.setAuthProvider(OpenMetadataConnection.AuthProvider.{auth_provider});
//	    server.setAuthProvider(OpenMetadataServerConnection.AuthProvider.NO_AUTH);
        /* server.setAuthProvider(AuthProvider.AUTH_0);*/
//	    NoOpAuthenticationProvider noOpAuthenticationProvider = new NoOpAuthenticationProvider();
//	    server.setAuthProvider(noOpAuthenticationProvider);
      /*  OpenMetadataJWTClientConfig openMetadataJWTClientConfig = new OpenMetadataJWTClientConfig();
        openMetadataJWTClientConfig.setJwtToken("{jwt_token}");*/
//	    server.setAuthProvider(OpenMetadataServerConnection.AuthProvider.OPENMETADATA);
//	    server.setSecurityConfig(openMetadataJWTClientConfig);

//	    server.setSecurityConfig({security_client_config});

//	    CustomOIDCSSOClientConfig customOIDCSSOClientConfig = new CustomOIDCSSOClientConfig();
//	    customOIDCSSOClientConfig.setClientId("{client_id}");
//	    customOIDCSSOClientConfig.setSecretKey("{client_secret}");
//	    customOIDCSSOClientConfig.setTokenEndpoint("{token_endpoint}");
//	    server.setAuthProvider(OpenMetadataServerConnection.AuthProvider.CUSTOM_OIDC);
//	    server.setSecurityConfig(customOIDCSSOClientConfig);


        // OpenMetadata Gateway
        /*   OpenMetadata openMetadataGateway = new OpenMetadata(server);*/

        // Dashboards API
        /*   DashboardsApi dashboardApi = openMetadataGateway.buildClient(DashboardsApi.class);*/

        // Tables API
        /*   TablesApi tablesApiClient = openMetadataGateway.buildClient(TablesApi.class);*/

        // Users API
        /*  UsersApi usersApi = openMetadataGateway.buildClient(UsersApi.class);*/

//	    // Locations API
//	    LocationsApi locationsApi = openMetadataGateway.buildClient(LocationsApi.class);
//	    
//	    TablesApi tablesApi = openMetadataGateway.buildClient(TablesApi.class);
//	    tablesApi.addLocationToTable("{table_id}", "{location_id}");
//	    
//	    LocationsApi locationsApi = openMetadataGateway.buildClient(LocationsApi.class);
//	    CreateLocation createLocation = new CreateLocation();
//	    Location location = locationsApi.createLocation(createLocation);

    /*    resultCode = 0;
        resultMessage = "Operazione conclusa con successo!";
*/
     /*   UploadFileResponse response = UploadFileResponse.builder()
                .resultCode(resultCode)
                .resultMessage(resultMessage)
                .urlMultimedias(urlMultimedias)
                .build();

        logger.info("FINE uploadMultiFile - UploadMultiFileResponse: " + response);

        return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().noinfo(response).build());*/

    }

}
