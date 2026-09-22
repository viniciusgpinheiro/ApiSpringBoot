package __24529.projeto2.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "11144477735"})
    void deveAceitarCpfValido(String cpf) {
        assertTrue(CpfUtils.isCpfValido(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678900", "00000000000", "11111111111", "52998224726"})
    void deveRejeitarCpfInvalido(String cpf) {
        assertFalse(CpfUtils.isCpfValido(cpf));
    }

    @Test
    void deveRejeitarCpfComTamanhoErrado() {
        assertFalse(CpfUtils.isCpfValido("123456789"));
    }

    @Test
    void deveRejeitarCpfNulo() {
        assertFalse(CpfUtils.isCpfValido(null));
    }
}
