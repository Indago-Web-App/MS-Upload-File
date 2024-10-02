package it.linearsystem.indago.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.linearsystem.indago.bean.dto.FileProcessDto;
import it.linearsystem.indago.bean.dto.FileValidityDto;
import it.linearsystem.indago.bean.dto.response.ResponseDto;
import it.linearsystem.indago.bean.dto.response.UploadFileRequest;
import it.linearsystem.indago.bean.dto.response.UploadFileResponse;
import it.linearsystem.indago.service.FileProcessService;
import it.linearsystem.indago.service.FileUploadService;
import it.linearsystem.indago.service.OpenMetadataService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RestController
@RequestMapping("/indago")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileProcessService fileProcessService;

    // OPEN METADATA
    @Autowired
    private OpenMetadataService openMetadataService;

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
                    )
            })
    @PostMapping(
            value = "/uploadFile"
            , headers = {"Content-Type=multipart/form-data", "Accept=application/json"}
            , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.MULTIPART_MIXED_VALUE}
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed("ADMIN")
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

        FileProcessDto fileProcessed;
        try {
            fileProcessed = fileProcessService.processFile(fileValidityDto);
        } catch (IOException e) {
            return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().error("400", e.getMessage(), null).build());
        }


        try {
            logger.info("### OPEN METADATA ###");
            openMetadataService.processFile(fileProcessed);

            logger.info("### FINEEE OPEN METADATA ###");

            UploadFileResponse response = UploadFileResponse.builder()
                    .resultCode(0)
                    .resultMessage("Lista errori: " + fileProcessed.getDatabaseMap().getErrori())
                    .urlMultimedias(urlMultimedias)
                    .build();
            return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().noinfo(response).build());

        } catch (Exception e) {
            return ResponseEntity.ok(ResponseDto.<UploadFileResponse>builder().error("400", e.getMessage(), null).build());
        }

    }

}
