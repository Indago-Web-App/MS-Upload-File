package it.linearsystem.indago.bean.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Schema(name = "UploadFileRequest", description = "Classe Upload File Request", subTypes = {UploadFileRequest.class})
public class UploadFileRequest implements Serializable {

	private static final long serialVersionUID = -8849397950762755987L;
	
//    @ApiModelProperty(
//            name = "files",
//            value = "Foto e Video da archiviare"
//    )
    @NotNull(message = "error.msg.14")
    private MultipartFile file;
    
}