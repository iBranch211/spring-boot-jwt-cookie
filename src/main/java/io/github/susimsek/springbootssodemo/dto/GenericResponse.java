package io.github.susimsek.springbootssodemo.dto;

public record GenericResponse(
        SuccessFailure status,

        String message
) {

}
