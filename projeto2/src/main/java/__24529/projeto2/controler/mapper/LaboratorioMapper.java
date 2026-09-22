package __24529.projeto2.controler.mapper;

import __24529.projeto2.controler.dto.LaboratorioDTO;
import __24529.projeto2.model.Laboratorio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LaboratorioMapper {

    @Mapping(target = "statusId", source = "status.codigo")
    @Mapping(target = "statusAtual", ignore = true)
    LaboratorioDTO toDTO(Laboratorio laboratorio);
}
