package io.github.aalsanie.irp.connections;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CreateConnectionRequest(
        @NotBlank(message = "name must not be blank")
        @Size(max = 150, message = "name must not exceed 150 characters")
        String name,

        @NotBlank(message = "providerType must not be blank")
        @Size(max = 50, message = "providerType must not exceed 50 characters")
        String providerType ) {

}
