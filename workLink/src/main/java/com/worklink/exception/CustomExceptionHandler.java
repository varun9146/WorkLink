package com.worklink.exception;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.worklink.helper.EmptyResponse;
import com.worklink.utills.ApiError;
import com.worklink.utills.GenericResponseModel;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = Logger.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler
	public ResponseEntity<GenericResponseModel<EmptyResponse, ApiError<String>>> getMethodValidationErrors(
			ConstraintViolationException constraintViolationException) {
		logger.error(constraintViolationException.getMessage());
		List<String> errors = constraintViolationException.getConstraintViolations().stream()
				.map(error -> error.getMessage()).toList();
//       error.getPropertyPath().toString().split("\\.")[error.getPropertyPath().toString().split("\\.").length - 1]
		return new ResponseEntity<>(new GenericResponseModel<>(new EmptyResponse(), new ApiError<>(errors.get(0))),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<GenericResponseModel<EmptyResponse, ApiError<String>>> getMethodArgumentErrors(
			MethodArgumentNotValidException methodArgumentNotValidException) {
		logger.error(methodArgumentNotValidException.getMessage());
		FieldError fieldError = methodArgumentNotValidException.getBindingResult().getFieldErrors().get(0);
		return new ResponseEntity<>(
				new GenericResponseModel<>(new EmptyResponse(),
						new ApiError<>(fieldError.getField() + " " + fieldError.getDefaultMessage())),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<GenericResponseModel<EmptyResponse, ApiError<String>>> invalidJsonError(
			HttpMessageNotReadableException httpMessageNotReadableException) {
		logger.error(httpMessageNotReadableException.getMessage());
		return new ResponseEntity<>(
				new GenericResponseModel<>(new EmptyResponse(),
						new ApiError<>("Invalid request, missing request body or invalid json")),
				HttpStatus.BAD_REQUEST);
	}
	
    @ExceptionHandler(DataInsertionFailedException.class)
	public ResponseEntity<GenericResponseModel<EmptyResponse, ApiError<String>>>  dataInsertion(DataInsertionFailedException ex) {
    	 return new ResponseEntity<>(new GenericResponseModel<>(new EmptyResponse(), new ApiError<>(ex.getMessage())), HttpStatus.CONFLICT);
	}
    
    @ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<GenericResponseModel<EmptyResponse, ApiError<String>>> handleInvalidRequestException(InvalidRequestException ex){
    	return new ResponseEntity<>(new GenericResponseModel<>(null, new ApiError<>(ex.getMessage())),HttpStatus.BAD_REQUEST);	
    	}
}
