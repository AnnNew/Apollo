package com.kusid.apollo.repository;

import com.kusid.apollo.domain.Doctor;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Doctor entity.
 */
@SuppressWarnings("unused")
public interface DoctorRepository extends JpaRepository<Doctor,Long> {

}
