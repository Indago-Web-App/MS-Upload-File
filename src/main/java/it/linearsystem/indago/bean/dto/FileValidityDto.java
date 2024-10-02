package it.linearsystem.indago.bean.dto;

import it.linearsystem.indago.entity.FileUpload;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
public class FileValidityDto {
    private Boolean isValid;
    private String message;
    private String nameFile;
    private InputStream inputStream;
    private long size;
    private FileUpload fileUpload;
}