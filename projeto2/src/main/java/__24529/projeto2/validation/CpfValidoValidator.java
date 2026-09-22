package __24529.projeto2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidoValidator implements ConstraintValidator<CpfValido, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return true; // Campo vazio é responsabilidade de @NotBlank
        }
        String digitos = valor.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return true; // Tamanho incorreto é responsabilidade de @Size
        }
        return CpfUtils.isCpfValido(digitos);
    }
}
