package drinks.system.salesservice.application.mapper;

import drinks.system.salesservice.application.dto.response.BranchPrinterResponse;
import drinks.system.salesservice.domain.model.BranchPrinter;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.BranchPrinterEntity;
import org.springframework.stereotype.Component;

@Component
public class BranchPrinterMapper {

    public BranchPrinter toDomain(BranchPrinterEntity e) {
        return new BranchPrinter(e.getId(), e.getBranchId(), e.getName(), e.getHost(), e.getPort(),
                e.getPaperWidthMm(), e.getCopies(), e.getCutPaper(), e.getOpenDrawer(),
                e.getIsDefault(), e.getIsActive(), e.getCreatedAt(), e.getUpdatedAt(),
                e.getCreatedBy(), e.getUpdatedBy());
    }

    public BranchPrinterEntity toEntity(BranchPrinter d) {
        BranchPrinterEntity e = new BranchPrinterEntity();
        e.setId(d.id());
        e.setBranchId(d.branchId());
        e.setName(d.name());
        e.setHost(d.host());
        e.setPort(d.port());
        e.setPaperWidthMm(d.paperWidthMm());
        e.setCopies(d.copies());
        e.setCutPaper(d.cutPaper());
        e.setOpenDrawer(d.openDrawer());
        e.setIsDefault(d.isDefault());
        e.setIsActive(d.isActive());
        e.setCreatedBy(d.createdBy());
        e.setUpdatedBy(d.updatedBy());
        return e;
    }

    public BranchPrinterResponse toResponse(BranchPrinter d) {
        return new BranchPrinterResponse(d.id(), d.branchId(), d.name(), d.host(), d.port(),
                d.paperWidthMm(), d.copies(), d.cutPaper(), d.openDrawer(), d.isDefault(), d.isActive());
    }
}
