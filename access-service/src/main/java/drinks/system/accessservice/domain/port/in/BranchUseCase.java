package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.BranchStatusRequest;
import drinks.system.accessservice.application.dto.request.CreateBranchRequest;
import drinks.system.accessservice.application.dto.request.UpdateBranchRequest;
import drinks.system.accessservice.application.dto.response.BranchResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para operaciones de gestión de sucursales.
 * Define los casos de uso CRUD y cambio de estado de sucursales.
 */
public interface BranchUseCase {

    BranchResponse create(CreateBranchRequest request, Long currentUserId);

    PageResponse<BranchResponse> findAll(Pageable pageable, Boolean isActive);

    BranchResponse findById(Long id);

    BranchResponse update(Long id, UpdateBranchRequest request, Long currentUserId);

    void updateStatus(Long id, BranchStatusRequest request, Long currentUserId);
}
