package org.k5va.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.jooq.DSLContext;
import org.k5va.generated.tables.records.EmployeesRecord;

import java.util.List;
import java.util.Optional;

import static org.k5va.generated.tables.Employees.EMPLOYEES;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
    private final DSLContext dslContext;

    public Optional<EmployeesRecord> findById(Long id) {
        return Optional.ofNullable(dslContext.selectFrom(EMPLOYEES)
                .where(EMPLOYEES.ID.eq(id))
                .fetchOne());
    }

    public List<EmployeesRecord> findAll() {
        return dslContext.selectFrom(EMPLOYEES).fetch();
    }

    public EmployeesRecord create(EmployeesRecord employee) {
        return Optional.ofNullable(dslContext.insertInto(EMPLOYEES)
                        .values(employee)
                        .returning()
                        .fetchOne())
                .orElseThrow(() -> new RuntimeException("Failed to return created employee"));
    }

    public EmployeesRecord update(EmployeesRecord employee) {
        return Optional.ofNullable(dslContext.update(EMPLOYEES)
                        .set(employee)
                        .where(EMPLOYEES.ID.eq(employee.getId()))
                        .returning()
                        .fetchOne())
                .orElseThrow(() -> new RuntimeException("Failed to return updated employee"));
    }

    public void delete(Long id) {
        dslContext.delete(EMPLOYEES)
                .where(EMPLOYEES.ID.eq(id))
                .execute();
    }
}
