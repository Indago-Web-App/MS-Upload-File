package it.linearsystem.indago.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileProcessDto {
    private boolean isValid;
    private String message;
    private DatabaseMap databaseMap;

}