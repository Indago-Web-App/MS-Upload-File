package it.linearsystem.indago.bean.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadFileResponse implements Serializable {

	private static final long serialVersionUID = -302762867630483989L;
	
	private Integer resultCode;
	private String resultMessage;
	
	private List<String> urlMultimedias;
	
}