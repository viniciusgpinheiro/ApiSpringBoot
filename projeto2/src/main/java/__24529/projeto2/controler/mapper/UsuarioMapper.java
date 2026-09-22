package __24529.projeto2.controler.mapper;

import __24529.projeto2.controler.dto.UsuarioDTO;
import __24529.projeto2.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioDTO toDTO(Usuario usuario);
}
