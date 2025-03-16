package com.worklink.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record Location(@NotNull @NotEmpty (message ="City should not be empty") String city,@NotNull @NotEmpty (message ="State should not be empty") String state,@NotNull @NotEmpty (message ="Country should not be empty") String country) {

}
