package com.imcode.repositories;


import com.imcode.entities.SchoolTransport;
import com.imcode.services.NamedService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolTransportRepository extends JpaRepository<SchoolTransport, Long>, NamedService<SchoolTransport> {
}
