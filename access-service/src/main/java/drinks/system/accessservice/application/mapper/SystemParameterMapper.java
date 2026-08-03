package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.SystemParameterResponse;
import drinks.system.accessservice.domain.model.SystemParameter;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.SystemParameterEntity;
import org.springframework.stereotype.Component;

@Component
public class SystemParameterMapper {

    public SystemParameter toDomain(SystemParameterEntity entity) {
        return new SystemParameter(
                entity.getId(),
                entity.getParameterKey(),
                entity.getParameterValue(),
                entity.getDataType(),
                entity.getDescription(),
                entity.getModule(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public SystemParameterEntity toEntity(SystemParameter domain) {
        SystemParameterEntity entity = new SystemParameterEntity();
        entity.setId(domain.id());
        entity.setParameterKey(domain.parameterKey());
        entity.setParameterValue(domain.parameterValue());
        entity.setDataType(domain.dataType());
        entity.setDescription(domain.description());
        entity.setModule(domain.module());
        entity.setIsActive(domain.isActive());
        entity.setCreatedBy(domain.createdBy());
        entity.setUpdatedBy(domain.updatedBy());
        return entity;
    }

    public SystemParameterResponse toResponse(SystemParameter param) {
        return new SystemParameterResponse(
                param.id(),
                param.parameterKey(),
                param.parameterValue(),
                param.dataType(),
                param.description(),
                param.module(),
                param.isActive(),
                param.createdAt(),
                param.updatedAt()
        );
    }
}
