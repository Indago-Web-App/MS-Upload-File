package it.linearsystem.indago.bean.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

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