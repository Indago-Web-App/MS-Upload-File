package it.linearsystem.indago.bean.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataAssetRequest {
    private String fileName;
    private byte[] fileContent;

}
