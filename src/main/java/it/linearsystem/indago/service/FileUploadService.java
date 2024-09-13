package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.FileValidityDto;
import it.linearsystem.indago.bean.dto.response.UploadFileRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FileUploadService {

    public FileValidityDto checkValidationFile(UploadFileRequest uploadFileRequest) throws IOException {

        FileValidityDto result = new FileValidityDto();
        // Validation empy content
        if (uploadFileRequest.getFile().isEmpty()) {
            result.setIsValid(false);
            result.setMessage("Il content del file è vuoto");
            return result;
        };


        // Validation name File
        if (!checkNameFile(uploadFileRequest.getFile().getName())) {
            result.setIsValid(false);
            result.setMessage("Nome File null or empty");
            return result;
        }

        // Validation name File and extension
        if (!checkExtensionFile(uploadFileRequest.getFile().getName())) {
            result.setIsValid(false);
            result.setMessage("Il file non ha l'estensione corretta: " + uploadFileRequest.getFile().getName());
            return result;
        }

        // Others
        result.setIsValid(true);
        result.setMessage(null);
        result.setNameFile(uploadFileRequest.getFile().getName());
        result.setInputStream(uploadFileRequest.getFile().getInputStream());
        result.setSize(uploadFileRequest.getFile().getSize());
        return result;
    }

    private boolean checkNameFile(String fileName) {
        return fileName != null && !fileName.isEmpty();
    }

    private boolean checkExtensionFile(String fileName) {
        return !fileName.endsWith(".xlsx");
    }
}
