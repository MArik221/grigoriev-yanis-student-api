package kz.iitu.grigoriev.studentapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRequest(
    @NotBlank @Size(max = 80) String name,
    @NotBlank @Size(max = 80) String surname,
    @NotBlank @Size(max = 40) String groupCode,
    @NotBlank @Size(max = 32) String studentNumber
) { }
