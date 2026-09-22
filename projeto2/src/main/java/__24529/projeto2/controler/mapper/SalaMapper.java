package __24529.projeto2.controler.mapper;

import __24529.projeto2.controler.dto.SalaDTO;
import __24529.projeto2.model.Sala;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalaMapper {

    @Mapping(target = "statusId", source = "status.codigo")
    @Mapping(target = "statusAtual", ignore = true)
    SalaDTO toDTO(Sala sala);
}
