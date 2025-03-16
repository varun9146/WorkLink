package com.worklink.utills;

import java.util.Optional;

import javax.json.JsonValue;

public class GenericResponseModel<R, E> {
    private R data;
    private E error;

    public GenericResponseModel(R data, E error) {
        this.data = data;
        this.error = error;
    }

    public GenericResponseModel(Optional<JsonValue> of, Object error2) {
		// TODO Auto-generated constructor stub
        this.data = (R) of;
        this.error = (E) error2;
	}

	public R getData() {
        return data;
    }

    public E getError() {
        return error;
    }

    public void setData(R data) {
        this.data = data;
    }

    public void setError(E error) {
        this.error = error;
    }
}
