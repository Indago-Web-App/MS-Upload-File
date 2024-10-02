package it.linearsystem.indago.service;

import it.linearsystem.indago.entity.FileUpload;
import it.linearsystem.indago.entity.LogErroreOpenmetadata;
import it.linearsystem.indago.repository.LogErroreOpenmetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogErroreOpenmetadataService {

    @Autowired
    private LogErroreOpenmetadataRepository logErroreOpenmetadataRepository;

    public void insertLogErroreOpenmetadata(FileUpload fileUpload, String message) {
        LogErroreOpenmetadata logErroreOpenmetadata = new LogErroreOpenmetadata();
        logErroreOpenmetadata.setFileUpload(fileUpload);
        logErroreOpenmetadata.setErroreDescrizione(message);
        logErroreOpenmetadataRepository.save(logErroreOpenmetadata);
    }
}
