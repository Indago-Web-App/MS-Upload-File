package it.linearsystem.indago.service;

import it.linearsystem.indago.entity.FileUpload;
import it.linearsystem.indago.entity.LogErroreFile;
import it.linearsystem.indago.repository.LogErroreFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogErroreFileService {

    @Autowired
    private LogErroreFileRepository logErroreFileRepository;

    public void insertLogErroreFile(FileUpload fileUpload, String message) {
        LogErroreFile logErroreFile = new LogErroreFile();
        logErroreFile.setFileUpload(fileUpload);
        logErroreFile.setErroreDescrizione(message);
        logErroreFileRepository.save(logErroreFile);
    }
}
