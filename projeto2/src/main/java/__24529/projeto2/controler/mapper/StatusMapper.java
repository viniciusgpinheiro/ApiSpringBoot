package __24529.projeto2.controler.mapper;

import __24529.projeto2.controler.dto.StatusDTO;
import __24529.projeto2.model.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusMapper {
    StatusDTO toDTO(Status status);
}
