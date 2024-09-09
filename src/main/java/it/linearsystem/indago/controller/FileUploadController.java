package it.linearsystem.indago.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

//import javax.annotation.security.DenyAll;
//import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.openmetadata.catalog.services.connections.metadata.OpenMetadataServerConnection;
import org.openmetadata.client.api.DashboardsApi;
import org.openmetadata.client.api.TablesApi;
import org.openmetadata.client.api.UsersApi;
import org.openmetadata.client.gateway.OpenMetadata;
import org.openmetadata.schema.security.client.OpenMetadataJWTClientConfig;
import org.openmetadata.schema.services.connections.metadata.AuthProvider;
//import org.openmetadata.client.model.OpenMetadataConnection;
import org.openmetadata.schema.services.connections.metadata.OpenMetadataConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.linearsystem.indago.bean.dto.ResponseDto;
import it.linearsystem.indago.bean.dto.UploadFileRequest;
import it.linearsystem.indago.bean.dto.UploadFileResponse;
import it.linearsystem.indago.config.FileUploadConfig;
//import my.maven.project.service.FilesStorageService;
import lombok.extern.java.Log;

/**
 * http://localhost:8080/Indago/indago/
 * 
 * @author by Andrea Zaccanti
 * @Created 05 Maggio 2020
 * @Updated 15 Giugno 2023
 *
 */
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/indago")
//@Validated
@Log
public class FileUploadController {
	
	@Value("${openmetadata.server.url}")
	String openmetadataServerURL;
	
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
			, headers = {"Content-Type=multipart/form-data","Accept=application/json"}
			, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.MULTIPART_MIXED_VALUE}
			, produces = MediaType.APPLICATION_JSON_VALUE
			)
//	@PermitAll
//	@DenyAll // Exception: org.springframework.security.access.AccessDeniedException: Access Denied
	@RolesAllowed("ADMIN") // ("USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied 
//	@Secured("ROLE_ADMIN") // ("ROLE_USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied
//	@PreAuthorize("hasAuthority('ROLE_ADMIN')") // ("ROLE_USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied
//	@PreAuthorize("hasRole('ROLE_ADMIN')") // ("ROLE_USER") Exception: org.springframework.security.access.AccessDeniedException: Access Denied
	public ResponseEntity<ResponseDto<UploadFileResponse>> uploadFile( 
			@Valid 
			UploadFileRequest uploadFileRequest ) {
	
		log.log(Level.INFO, "INIZIO uploadFile - uploadFileRequest: " + uploadFileRequest);
		
		Integer resultCode = 1;
		String resultMessage = "Array Multipart is Empty!";
		
		List<String> urlMultimedias = new ArrayList<String>();
				
		log.log(Level.INFO, "INIZIO uploadFile - file.getOriginalFilename: " + uploadFileRequest.getFile().getOriginalFilename());
		log.log(Level.INFO, "INIZIO uploadFile - file.getSize: " + uploadFileRequest.getFile().getSize());
		
//		try {
//			String urlMultimedia = storageService.save("E", "1", uploadFileRequest.getFile());
//			
//			urlMultimedias.add( urlMultimedia );
//
//		} catch ( IOException e ) {
//			log.log(Level.SEVERE, "ERROR uploadMultiFile: " + e.getMessage());
//			urlMultimedias.add( "Errore uploadMultiFile: " + uploadFileRequest.getFile().getOriginalFilename());
//		}
		
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(uploadFileRequest.getFile().getInputStream());
		} catch (IOException e) {
			log.log(Level.SEVERE, "ERROR XSSFWorkbook: " + e.getMessage());
		}
	    XSSFSheet worksheet = workbook.getSheetAt(0);
	    
	    for(int i=0;i<worksheet.getPhysicalNumberOfRows() ;i++) {
	            
	        XSSFRow row = worksheet.getRow(i);
	            
	        log.log(Level.INFO, "ploadFile - CELL 0 Value: " + row.getCell(0).getStringCellValue());
	        log.log(Level.INFO, "ploadFile - CELL 0 Value: " + row.getCell(1).getStringCellValue());
	      
	    }
	    
	    try {
	    	
	    	workbook = new XSSFWorkbook( uploadFileRequest.getFile().getInputStream() );
	    	Sheet datatypeSheet = workbook.getSheetAt(0);
	    	Iterator<Row> iterator = datatypeSheet.iterator();

//	    	workbook.forEach(sheet -> {
	        while (iterator.hasNext()) {
	
	            Row currentRow = iterator.next();
	            Iterator<Cell> cellIterator = currentRow.iterator();
	
	            while (cellIterator.hasNext()) {
	
	                Cell currentCell = cellIterator.next();
	                //getCellTypeEnum shown as deprecated for version 3.15
	                //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
	    	        log.log(Level.INFO, "ploadFile - CELL 0 Value: " + currentCell.getStringCellValue());
	                if (currentCell.getCellType() == CellType.STRING) {
	                    System.out.print(currentCell.getStringCellValue() + "--");
	                } else if (currentCell.getCellType() == CellType.NUMERIC) {
	                    System.out.print(currentCell.getNumericCellValue() + "--");
	                }
	
	            }
	            System.out.println();
	
	        }
	        
	        Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				for (Cell currentCell : row) {
//					String cellValue = dataFormatter.formatCellValue(cell);
//					list.add(cellValue);
					log.log(Level.INFO, "ploadFile - CELL 0 Value: " + currentCell.getStringCellValue());
	                if (currentCell.getCellType() == CellType.STRING) {
	                    System.out.print(currentCell.getStringCellValue() + "--");
	                } else if (currentCell.getCellType() == CellType.NUMERIC) {
	                    System.out.print(currentCell.getNumericCellValue() + "--");
	                }
				}
			}
			
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    log.log(Level.INFO, "### OPEN METADATA ###");
	    
//	    OpenMetadataClient client = new OpenMetadataClient();
	    
	    OpenMetadataConnection server = new OpenMetadataConnection();
//	    OpenMetadataServerConnection server = new OpenMetadataServerConnection();
		server.setHostPort(openmetadataServerURL); // http://localhost:{port}}/api
	    server.setApiVersion("v1");
//	    server.setAuthProvider(OpenMetadataConnection.AuthProvider.{auth_provider});
//	    server.setAuthProvider(OpenMetadataServerConnection.AuthProvider.NO_AUTH);
	    server.setAuthProvider(AuthProvider.AUTH_0);
//	    NoOpAuthenticationProvider noOpAuthenticationProvider = new NoOpAuthenticationProvider();
//	    server.setAuthProvider(noOpAuthenticationProvider);
	    OpenMetadataJWTClientConfig openMetadataJWTClientConfig = new OpenMetadataJWTClientConfig();
	    openMetadataJWTClientConfig.setJwtToken("{jwt_token}");
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
	    OpenMetadata openMetadataGateway = new OpenMetadata(server);
	    
	 // Dashboards API
	    DashboardsApi dashboardApi = openMetadataGateway.buildClient(DashboardsApi.class);

	    // Tables API
	    TablesApi tablesApiClient = openMetadataGateway.buildClient(TablesApi.class);

	    // Users API
	    UsersApi usersApi = openMetadataGateway.buildClient(UsersApi.class);

//	    // Locations API
//	    LocationsApi locationsApi = openMetadataGateway.buildClient(LocationsApi.class);
//	    
//	    TablesApi tablesApi = openMetadataGateway.buildClient(TablesApi.class);
//	    tablesApi.addLocationToTable("{table_id}", "{location_id}");
//	    
//	    LocationsApi locationsApi = openMetadataGateway.buildClient(LocationsApi.class);
//	    CreateLocation createLocation = new CreateLocation();
//	    Location location = locationsApi.createLocation(createLocation);
			
		resultCode = 0;
		resultMessage = "Operazione conclusa con successo!";
	
		UploadFileResponse response = UploadFileResponse.builder()
				.resultCode(resultCode)
				.resultMessage(resultMessage)
				.urlMultimedias(urlMultimedias)
				.build();
	
		log.log(Level.INFO, "FINE uploadMultiFile - UploadMultiFileResponse: " + response);
	
		return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().noinfo(response).build());
	
	}
	
}
