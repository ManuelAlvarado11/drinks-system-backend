package drinks.system.salesservice.application.service;

import drinks.system.salesservice.application.dto.request.CreateCustomerRequest;
import drinks.system.salesservice.application.dto.request.UpdateCustomerRequest;
import drinks.system.salesservice.application.dto.response.CustomerResponse;
import drinks.system.salesservice.application.mapper.CustomerMapper;
import drinks.system.salesservice.domain.model.Customer;
import drinks.system.salesservice.domain.port.in.CustomerUseCase;
import drinks.system.salesservice.domain.port.out.CustomerRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.BusinessConflictException;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerMapper customerMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public CustomerResponse create(CreateCustomerRequest request, Long userId) {
        if (request.nitCi() != null && !request.nitCi().isBlank() && customerRepository.existsByNitCi(request.nitCi())) {
            throw new BusinessConflictException("Ya existe un cliente con NIT/CI: " + request.nitCi());
        }
        Customer customer = new Customer(null, request.firstName(), request.lastName(),
                request.nitCi(), request.phone(), request.email(), true,
                null, null, null, userId, userId);
        Customer saved = customerRepository.save(customer);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "SALES",
                "Customer", saved.id(), null, null, null, "Cliente creado: " + saved.firstName()));
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> findAll(Pageable pageable, String search) {
        Page<Customer> page = customerRepository.findAll(pageable, search);
        List<CustomerResponse> content = page.getContent().stream().map(customerMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer c = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        return customerMapper.toResponse(c);
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, UpdateCustomerRequest request, Long userId) {
        Customer existing = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        Customer updated = new Customer(existing.id(),
                request.firstName() != null ? request.firstName() : existing.firstName(),
                request.lastName() != null ? request.lastName() : existing.lastName(),
                request.nitCi() != null ? request.nitCi() : existing.nitCi(),
                request.phone() != null ? request.phone() : existing.phone(),
                request.email() != null ? request.email() : existing.email(),
                existing.isActive(), existing.deletedAt(), existing.createdAt(), existing.updatedAt(),
                existing.createdBy(), userId);
        Customer saved = customerRepository.save(updated);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "SALES",
                "Customer", id, null, null, null, "Cliente actualizado: " + saved.firstName()));
        return customerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer existing = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        Customer deleted = new Customer(existing.id(), existing.firstName(), existing.lastName(),
                existing.nitCi(), existing.phone(), existing.email(), false,
                Instant.now(), existing.createdAt(), existing.updatedAt(), existing.createdBy(), existing.updatedBy());
        customerRepository.save(deleted);
        eventPublisher.publishEvent(new AuditEvent(null, null, "DELETE", "SALES",
                "Customer", id, null, null, null, "Cliente desactivado: " + existing.firstName()));
    }
}
