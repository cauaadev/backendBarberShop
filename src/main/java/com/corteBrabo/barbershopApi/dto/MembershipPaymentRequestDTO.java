package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MembershipPaymentRequestDTO(
        @NotBlank(message = "Forma de pagamento é obrigatória")
        @Pattern(regexp = "PIX|CARTAO|DINHEIRO|BOLETO", message = "Forma de pagamento inválida")
        String method
) {
}
