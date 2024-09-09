package it.linearsystem.indago.config;

import java.io.FileNotFoundException;
import java.io.IOException;
//import java.net.ConnectException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.LockTimeoutException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.stream.MalformedJsonException;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
//import lombok.extern.log4j.Log4j;
//import lombok.extern.slf4j.Slf4j;
import it.linearsystem.indago.bean.dto.ResponseDto;
//import it.linearsystem.indago.bean.dto.ResponseDto.ResponseInfo;
import it.linearsystem.indago.bean.dto.ResponseError;
//import my.maven.project.exception.CypherException;
//import my.maven.project.exception.FileStorageException;
//import my.maven.project.exception.Md5ChecksumException;
//import my.maven.project.exception.UsernameAlreadyExistsException;

/**
 *
 * @author by Andrea Zaccanti
 * @Created 05 Maggio 2020
 * @Updated 31 Maggio 2022
 * @Updated 28 Agosto 2022 -> ResourceAccessException.class, ConnectException.class, HttpClientErrorException.class FOR CROSS REQUEST
 * @Updated 12 Ottobre 2022 -> AccessDeniedException.class
 * @Updated 10 Marzo 2023 -> ResourceAccessException.class, ConnectException.class, HttpClientErrorException.class FOR CROSS REQUEST WITH DETAILED MESSAGE
 * @Updated 04 Agosto 2023 -> CannotAcquireLockException.class, LockTimeoutException.class
 * @Updated 22 Agosto 2023 -> ResourceAccessException.class, ConnectException.class, HttpClientErrorException.class + IllegalArgumentException.class TO OUTPUT WITH STATUS SERVICE_UNAVAILABLE
 * @Updated 31 Agosto 2023 -> JsonParseException.class, JsonMappingException.class, JsonProcessingException.class FOR Jackson Json library
 * 
 * @ControllerAdvice -> @RestControllerAdvice
 * private Logger log = LoggerFactory.getLogger(ExceptionConfig.class); -> @Slf4j
 * 
 * 
 * If no custom rollback rules are configured in this annotation, the transaction will roll back on RuntimeException and Error but not on checked exceptions.
 *
 * IN CASO DI CHIAMATE CROSS CHE RITORNANO UN ECCEZIONE, IN BASE AL CODICE D'ERRORE DI RITORNO, LA REST_TEMPLATE SI COMPORTA IN MANIERA DIVERSA:
 *     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NOT_FOUND); // LA REST TEMPLATE CHIAMANTE VA IN ECCEZIONE
 *     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.ALREADY_REPORTED); // LA REST TEMPLATE CHIAMANTE NON VA IN ECCEZIONE
 *     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NO_CONTENT); // LA REST TEMPLATE CHIAMANTE NON VA IN ECCEZIONE, MA LA ResponseDto E' NULL
 *     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.PROCESSING); // LA REST TEMPLATE CHIAMANTE SI BLOCCA, NON RICEVE NULLA
 *     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NOT_MODIFIED); // LA REST TEMPLATE CHIAMANTE NON VA IN ECCEZIONE, MA LA ResponseDto E' NULL
 *     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.FAILED_DEPENDENCY); // LA REST TEMPLATE CHIAMANTE VA IN ECCEZIONE
 *     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NOT_IMPLEMENTED); // LA REST TEMPLATE CHIAMANTE VA IN ECCEZIONE
 * //     return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.ALREADY_REPORTED);
 *      return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.ACCEPTED); // OK
 *      return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NOT_MODIFIED); // NO EXCEPTION, RESP NULL
 *      return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.PRECONDITION_FAILED); // EXCEPTION
 *      return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NOT_FOUND); // EXCEPTION
 *        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.PRECONDITION_REQUIRED); // EXCEPTION
 *        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.IM_USED); // OK
 *        
 *  // A SEGUITO DELLA FINDALL CHE RITORNAVA 404 QUANDO NON ESTRAEVA NULLA, IN SEGUITO AUTO RISOLTO
 *  @ExceptionHandler({InvalidDataAccessResourceUsageException.class,JpaObjectRetrievalFailureException.class})
 *	@ResponseStatus(value = HttpStatus.OK, reason = "Empty list")
 *	public final List<ClusterDto> handleEntityNotFoundException(Exception ex, WebRequest request) {
 *
 *		log.log(Level.SEVERE, "Exception: " + ex);
 *
 *        return new ArrayList<ClusterDto>();
 *
 *	}   
 *
 * 
 */
@RestControllerAdvice
@Log
public class ExceptionConfig extends ResponseEntityExceptionHandler {

	@Autowired
	MessageSource messageSource;
	
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Internal Server Error")
	@ExceptionHandler(Exception.class)	
	public ResponseEntity<List<String>> handleGenericException(Exception ex, WebRequest request) throws Throwable {
//	public final ResponseEntity<ResponseDto<?>> handleAllExceptions(Exception ex, WebRequest request) /* throws Throwable */ {	
		
//		log.log(Level.SEVERE, "Exception: " + ex);
		log.log(Level.SEVERE, "Exception: " + ex);
//		log.log(Level.SEVERE, "Exception Message: " + ex.getMessage());
		log.log(Level.SEVERE, "Exception Message: " + ex.getMessage());
//		log.log(Level.SEVERE, "Exception StackTrace: " + ex.getStackTrace());
//		log.log(Level.SEVERE, "Exception StackTrace toString: " + ex.getStackTrace().toString());
//		log.log(Level.SEVERE, "Exception fillInStackTrace: " + ex.fillInStackTrace());
//		log.log(Level.SEVERE, "Exception Cause: " + ex.getCause());
		log.log(Level.SEVERE, "Exception Cause: " + ex.getCause());
		
		List<String> messages = new ArrayList<String>();
		
		Throwable cause = ex.getCause();
		if ( null != cause ) {
			if (!(cause instanceof RollbackException) ) throw cause;
			if (!(cause.getCause() instanceof ConstraintViolationException)) throw cause.getCause();
			ConstraintViolationException validationException = (ConstraintViolationException) cause.getCause();
			messages = validationException.getConstraintViolations().stream()
					.map(ConstraintViolation::getMessage)
					.collect(Collectors.toList());
		}
		
//		return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messages);
		
//	    ResponseDto<?> error = ResponseDto.builder().log(Level.SEVERE, "GEN_ERR", "Errore non previsto catturato, controllare i log per ulteriori informazioni.", null/*new ClusterDto()*/).build();
//	        
//  	//return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//		
//	    return new ResponseEntity<>(String.format(e.getMessage(), e), HttpStatus.NOT_ACCEPTABLE);
	}	
	
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        
    	log.log(Level.SEVERE, "Exception Message: " + ex.getMessage());
    	
    	List<ResponseDto.ResponseInfo> errorList = new ArrayList<>();
        int counter = 0;
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            ResponseDto.ResponseInfo errorDesc = new ResponseDto.ResponseInfo();
            counter = counter + 1;

            errorDesc.setErrorCode("VAL".concat(String.valueOf(counter)));
            errorDesc.setType("ERROR");
            errorDesc.setErrorDesc(error.getDefaultMessage());

            errorList.add(errorDesc);
        }
        ResponseDto<Object> error = ResponseDto.builder().list(errorList, null/*new ClusterDto()*/).build();
        
        return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
        
//        Map<String, String> errorMap = new HashMap<>();
//
//        errorMap.put("#Error count", String.valueOf(e.getBindingResult().getErrorCount()));
//        e.getBindingResult().getAllErrors()
//            .forEach(error -> errorMap.put(error.getCode(), error.getDefaultMessage()));
//
//        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
      
    }
	
	// IN CASO DI RequestValidator CHE LANCIA UNA ConstraintViolationException MA SOLO PER I SERVIZI REST
//	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Validation Request Error") // MAPPA UNA SUCCESSIVA CHIAMATA /error AL BasicErrorController // NON VISUALIZZO L'ECCEZIONE IN OUTPUT 	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<BaseResponse /*List<String>*/> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
		
		log.log(Level.SEVERE, "ConstraintViolationException Message: " + ex.getMessage());
		
		BaseResponse response = new BaseResponse();
		
		Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
		
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
		
		String message = messageSource.getMessage(messages.get(0), null, Locale.ITALIAN);
		
		log.log(Level.SEVERE, "ConstraintViolationException: " + message);

		response.setReturnCode(HttpStatus.BAD_REQUEST.value());
		response.setReturnMessage(message);
		
//		ConstraintViolationException exTmp = new ConstraintViolationException(messageSource.getMessage(messages.get(0), null, Locale.ITALIAN), violations);
//		request.setAttribute("javax.servlet.error.exception", exTmp);

		return new ResponseEntity<BaseResponse>(response/*messages*/, HttpStatus.BAD_REQUEST);
//		
//      ResponseError err = new Responselog(Level.SEVERE, LocalDateTime.now(), "File Storage Exception", messages);
//        
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);

	}
	
	@ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
	public final ResponseEntity<ResponseDto<?>> handleDataNotFoundException(Exception ex, WebRequest request) {

		log.log(Level.SEVERE, "Exception: " + ex);

		ResponseDto<?> error = ResponseDto.builder().error("DATA_NOT_FOUND", ex.getLocalizedMessage(), null).build();

		return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NOT_FOUND);

	}

	/**
	 * HttpStatus.NOT_FOUND -> HttpStatus.BAD_REQUEST
	 */
    @ExceptionHandler({SQLException.class, SQLGrammarException.class, PersistenceException.class, InvalidDataAccessResourceUsageException.class, 
    				   GenericJDBCException.class, JpaSystemException.class, NonUniqueResultException.class, IncorrectResultSizeDataAccessException.class})
	public final ResponseEntity<ResponseDto<?>> handleDataErrorException(Exception ex, WebRequest request) {

		log.log(Level.SEVERE, "Exception: " + ex);
		
        ResponseDto<?> error = ResponseDto.builder().error("DATA_ERR", ex.getLocalizedMessage(), null).build();

        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.BAD_REQUEST);

	}
    
    @ExceptionHandler({DataIntegrityViolationException.class, /*MalformedJsonException.class, JsonSyntaxException.class,*/ JsonParseException.class, JsonMappingException.class, JsonProcessingException.class})
	public final ResponseEntity<ResponseDto<?>> handleDataIntegrityViolationException(Exception ex, WebRequest request) {

		log.log(Level.SEVERE, "Exception: " + ex);
		
        ResponseDto<?> error = ResponseDto.builder().error("DATA_INTEG_ERR", ex.getLocalizedMessage(), null).build();

        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.CONFLICT);

	}
    
    @ExceptionHandler({IOException.class})
	public final ResponseEntity<ResponseDto<?>> handleIOException(Exception ex, WebRequest request) {

		log.log(Level.SEVERE, "Exception: " + ex);
		
        ResponseDto<?> error = ResponseDto.builder().error("IO_ERR", "Il file sembra non essere conforme.", null).build();

        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.INSUFFICIENT_STORAGE);

	}  
    
    @ExceptionHandler({MalformedURLException.class})
	public final ResponseEntity<ResponseDto<?>> handleMalformedURLException(MalformedURLException ex, WebRequest request) {

		log.log(Level.SEVERE, "Exception: " + ex);
		
        ResponseDto<?> error = ResponseDto.builder().error("URL_ERR", "L'URL sembra non essere formattato correttamente.", null).build();

        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.BAD_REQUEST);

	}
    
    @ExceptionHandler({NumberFormatException.class})
	public final ResponseEntity<ResponseDto<?>> handleNumberFormatException(NumberFormatException ex, WebRequest request) {

		log.log(Level.SEVERE, "Exception: " + ex);
		
        ResponseDto<?> error = ResponseDto.builder().error("PARAMS_URL_ERR", "Parametri dell'URL non formattati correttamente: " + ex.getLocalizedMessage(), null).build();

        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.BAD_REQUEST);

	}
    
//    @ExceptionHandler(UsernameAlreadyExistsException.class)
//    public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exc) {
//    	
//    	log.error("UsernameAlreadyExistsException Message: " + exc.getMessage());
//        
//        List<String> details = new ArrayList<String>();
//        details.add(exc.getMessage());
//        
//        ResponseError err = new Responseerror(LocalDateTime.now(), "Username Already Exists Exception", details);
//        
//        return ResponseEntity.status(HttpStatus.FOUND).body(err);
//        
//    }
//	
//    @ExceptionHandler(FileStorageException.class)
//    public ResponseEntity<Object> handleFileStorageException(FileStorageException exc) {
//    	
//    	log.error("FileStorageException Message: " + exc.getMessage());
//        
//        List<String> details = new ArrayList<String>();
//        details.add(exc.getMessage());
//        
//        ResponseError err = new Responselog(Level.SEVERE, LocalDateTime.now(), "File Storage Exception", details);
//        
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
//        
//    }
	
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException exc) {
    	
    	log.log(Level.SEVERE, "FileNotFoundException Message: " + exc.getMessage());
        
        List<String> details = new ArrayList<String>();
        details.add(exc.getMessage());
        
        ResponseError err = new ResponseError(LocalDateTime.now(), "File Not Found", details);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        
    	log.log(Level.SEVERE, "MaxUploadSizeExceededException Message: " + exc.getMessage());
    	
        List<String> details = new ArrayList<String>();
        details.add(exc.getMessage());
        
        ResponseError err = new ResponseError(LocalDateTime.now(), "File Size Exceeded", details);
        
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(err);
        
    }
    
//    @ExceptionHandler({ResourceAccessException.class, ConnectException.class, HttpClientErrorException.class, IllegalArgumentException.class})
//    public final ResponseEntity<ResponseDto<?>> handleExternalResourceException(Exception ex, WebRequest request) {
//
//    	log.log(Level.SEVERE, "Exception: " + ex);
//
//    	String message = messageErrorHandler( ex );
//    	
//    	if ( ex.getLocalizedMessage().contains("/approvazioni/") ) message = "Connessione rifiutata dal servizio Approvazioni";
//    	
//    	ResponseDto<?> error = ResponseDto.builder().error("RES_NOT_FOUND", message, null).build();
//
//    	return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.SERVICE_UNAVAILABLE);
//
//    }     
//    
//    @ExceptionHandler(Md5ChecksumException.class)
//	public final ResponseEntity<ResponseDto<?>> handleMd5ChecksumException(Md5ChecksumException ex, WebRequest request) {
//
//		log.error("Exception: " + ex.getMessage());
//		
//        ResponseDto<?> error = ResponseDto.builder().error("MD5_ERROR", ex.getLocalizedMessage(), null).build();
//
//        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.NOT_ACCEPTABLE);
//
//	} 
//    
//    @ExceptionHandler(CypherException.class)
//	public final ResponseEntity<ResponseDto<?>> handleCypherException(CypherException ex, WebRequest request) {
//
//		log.error("Exception: " + ex.getMessage());
//		
//        ResponseDto<?> error = ResponseDto.builder().error("CYPHER_ERROR", ex.getLocalizedMessage(), null).build();
//
//        return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.UNPROCESSABLE_ENTITY);
//
//	} 
//    
//    private String messageErrorHandler( Exception e ) {
//    	com.google.gson.Gson gson = new com.google.gson.Gson();
//    	String msg = e.getLocalizedMessage();
//    	String message = "Errore durante la chiamata al servizio";
//    	try {
//    		String subStr = msg.substring(7, msg.length()-1);
//    		ResponseDto<?> responseDto = gson.fromJson(subStr, ResponseDto.class);
//    		List<ResponseInfo> responseInfoList = responseDto.getInfo();
//    		ResponseInfo responseInfo = responseInfoList.get(0);
//    		message = responseInfo.getErrorDesc();
//    	} catch(Exception ex) {
//    		log.error("Errore nel parsing del messaggio di risposta: " + ex);
//    	}
//    	return message;
//    }
    
	@ExceptionHandler({CannotAcquireLockException.class, LockTimeoutException.class})
	public final ResponseEntity<ResponseDto<?>> handleCannotAcquireLockException(Exception ex, WebRequest request) {

		log.log(Level.SEVERE, "Exception: " + ex);

		ResponseDto<?> error = ResponseDto.builder().error("LOCK_TIMEOUT", ex.getLocalizedMessage(), null).build();

		return new ResponseEntity<ResponseDto<?>>(error, HttpStatus.LOCKED);

	}    
    	
}

@Data
@NoArgsConstructor
class BaseResponse {
	private int returnCode;
	private String returnMessage;
}
