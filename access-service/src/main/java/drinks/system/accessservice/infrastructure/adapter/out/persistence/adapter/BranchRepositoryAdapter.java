package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.BranchMapper;
import drinks.system.accessservice.domain.model.Branch;
import drinks.system.accessservice.domain.port.out.BranchRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.BranchEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.BranchJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final BranchJpaRepository branchJpaRepository;
    private final BranchMapper branchMapper;

    @Override
    public Optional<Branch> findById(Long id) {
        return branchJpaRepository.findById(id).map(branchMapper::toDomain);
    }

    @Override
    public Branch save(Branch branch) {
        BranchEntity entity = branchMapper.toEntity(branch);
        BranchEntity saved = branchJpaRepository.save(entity);
        return branchMapper.toDomain(saved);
    }

    @Override
    public Page<Branch> findAll(Pageable pageable, Boolean isActive) {
        return branchJpaRepository.findAllByIsActiveFiltered(isActive, pageable)
                .map(branchMapper::toDomain);
    }
}
