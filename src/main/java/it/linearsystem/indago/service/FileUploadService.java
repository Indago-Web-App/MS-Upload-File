package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.FileValidityDto;
import it.linearsystem.indago.bean.dto.response.UploadFileRequest;
import it.linearsystem.indago.entity.FileUpload;
import it.linearsystem.indago.entity.StatoFile;
import it.linearsystem.indago.entity.Utente;
import it.linearsystem.indago.repository.FileUploadRepository;
import it.linearsystem.indago.repository.StatoFileRepository;
import it.linearsystem.indago.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private StatoFileRepository statoFileRepository;

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

        // SALVA FILE se c'è qualche problema lancio eccezione
        FileUpload fileUpload;
        try {
            fileUpload = salvaFile(uploadFileRequest.getFile());
        } catch (Exception e) {
            result.setIsValid(false);
            result.setMessage(e.getMessage());
            return result;
        }

        // Others
        result.setIsValid(true);
        result.setMessage(null);
        result.setNameFile(uploadFileRequest.getFile().getName());
        result.setInputStream(uploadFileRequest.getFile().getInputStream());
        result.setSize(uploadFileRequest.getFile().getSize());
        result.setFileUpload(fileUpload);
        return result;
    }

    @Transactional
    protected FileUpload salvaFile(MultipartFile file) throws IOException {
        // Trova l'utente e lo stato basati sugli ID passati
        Utente utente = utenteRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        StatoFile statoFile = statoFileRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Stato del file non trovato"));

        // Crea un nuovo oggetto FileUpload
        FileUpload fileUpload = new FileUpload();
        fileUpload.setNomeFile(file.getName());
        fileUpload.setFileContenuto(file.getBytes());
        fileUpload.setUtente(utente);
        fileUpload.setStatoFile(statoFile);
        fileUpload.setDataCaricamento(new Date());
        fileUpload.setNumeroErrori(0);

        // Salva il file nel database
        return fileUploadRepository.save(fileUpload);
    }

    private boolean checkNameFile(String fileName) {
        return fileName != null && !fileName.isEmpty();
    }

    private boolean checkExtensionFile(String fileName) {
        return !fileName.endsWith(".xlsx");
    }
}
