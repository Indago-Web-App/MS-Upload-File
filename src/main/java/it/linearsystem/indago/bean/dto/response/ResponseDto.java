package it.linearsystem.indago.bean.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto<T> implements Serializable {

    private static final long serialVersionUID = 1921208897531163426L;
    
    private List<ResponseInfo> info;
    private T data;

    public static class ResponseDtoBuilder<T> {

        public ResponseDtoBuilder<T> noinfo(T data) {
            this.info = new ArrayList<>();
            this.info.add(ResponseInfo.builder().type("noerr").errorCode("200").errorDesc("Operazione eseguita con successo").build());
            this.data = data;
            return this;
        }

        public ResponseDtoBuilder<T> list(List<ResponseInfo> errors, T data) {
            this.info = new ArrayList<>();
            this.info.addAll(errors);
            this.data = data;
            return this;
        }

        public ResponseDtoBuilder<T> info(String errorCode, String errorDesc, T data) {
            this.info = new ArrayList<>();
            this.info.add(ResponseInfo.builder().type("info").errorCode(errorCode).errorDesc(errorDesc).build());
            this.data = data;
            return this;
        }

        public ResponseDtoBuilder<T> warning(String errorCode, String errorDesc, T data) {
            this.info = new ArrayList<>();
            this.info.add(ResponseInfo.builder().type("warning").errorCode(errorCode).errorDesc(errorDesc).build());
            this.data = data;
            return this;
        }

        public ResponseDtoBuilder<T> error(String errorCode, String errorDesc, T data) {
            this.info = new ArrayList<>();
            this.info.add(ResponseInfo.builder().type("error").errorCode(errorCode).errorDesc(errorDesc).build());
            this.data = data;
            return this;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseInfo implements Serializable {
    	
        private static final long serialVersionUID = -857418368556200242L;

        private String type;
        private String errorCode;
        private String errorDesc;

    }
    
}
