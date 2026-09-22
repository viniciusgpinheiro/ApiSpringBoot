package __24529.projeto2.controler.commom;

import __24529.projeto2.controler.dto.ErroCampo;
import __24529.projeto2.controler.dto.ErroResposta;
import __24529.projeto2.exceptions.OperacaoNaoPermitidaException;
import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.exceptions.RegistroNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    // Falha de validação de campo (Bean Validation) em POST/PUT -> 422
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<ErroCampo> listaErros = fieldErrors
                .stream()
                .map(fe -> new ErroCampo(fe.getField(),fe.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validação de campo",
                listaErros);
    }

    // Falha de regra de negócio (data/hora inválida, conflito de horário,
    // recurso bloqueado, cancelamento fora do prazo, FK inexistente, etc.) -> 422
    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleOperacaoNaoPermitidaException(OperacaoNaoPermitidaException e)
    {
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                e.getMessage(),
                List.of());
    }

    // Registro duplicado (CPF/e-mail já cadastrado, conflito de reserva) -> 409
    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResposta handleRegistroDuplicadoException(RegistroDuplicadoException e)
    {
        return ErroResposta.conflito(e.getMessage());
    }

    // Registro não encontrado por ID -> 404
    @ExceptionHandler(RegistroNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResposta handleRegistroNaoEncontradoException(RegistroNaoEncontradoException e)
    {
        return new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                List.of());
    }
}