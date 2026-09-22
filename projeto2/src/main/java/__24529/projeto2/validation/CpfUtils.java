package __24529.projeto2.validation;

public final class CpfUtils {

    private CpfUtils() {
    }

    public static boolean isCpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return false;
        }
        // CPFs com todos os dígitos iguais (00000000000, 11111111111...) são inválidos
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }

        int[] numeros = digitos.chars().map(c -> c - '0').toArray();

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += numeros[i] * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }
        if (primeiroDigito != numeros[9]) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += numeros[i] * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        return segundoDigito == numeros[10];
    }
}
