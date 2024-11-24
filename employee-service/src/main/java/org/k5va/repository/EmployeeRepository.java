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
        return Optional.ofNullable(
                dslContext.selectFrom(EMPLOYEES)
                        .where(EMPLOYEES.ID.eq(id))
                        .fetchOne()
                        .into(EmployeesRecord.class)
        );
    }

    public List<EmployeesRecord> findAll() {
        return dslContext.selectFrom(EMPLOYEES)
                .fetch()
                .into(EmployeesRecord.class);
    }

    public EmployeesRecord create(EmployeesRecord employee) {
        return dslContext.insertInto(EMPLOYEES)
                .values(employee)
                .returningResult(EMPLOYEES)
                .fetch()
                .get(0)
                .into(EmployeesRecord.class);
    }

    public EmployeesRecord update(EmployeesRecord employee) {
        return dslContext.update(EMPLOYEES)
                .set(employee)
                .where(EMPLOYEES.ID.eq(employee.getId()))
                .returningResult(EMPLOYEES)
                .fetch()
                .get(0)
                .into(EmployeesRecord.class);
    }

    public void delete(Long id) {
        dslContext.delete(EMPLOYEES)
                .where(EMPLOYEES.ID.eq(id))
                .execute();
    }
}
