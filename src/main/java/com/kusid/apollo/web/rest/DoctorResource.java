package com.kusid.apollo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.kusid.apollo.domain.Doctor;
import com.kusid.apollo.repository.DoctorRepository;
import com.kusid.apollo.security.AuthoritiesConstants;
import com.kusid.apollo.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Doctor.
 */
@RestController
@RequestMapping("/api")
public class DoctorResource {

    private final Logger log = LoggerFactory.getLogger(DoctorResource.class);

    @Inject
    private DoctorRepository doctorRepository;

    /**
     * POST  /doctors : Create a new doctor.
     *
     * @param doctor the doctor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new doctor, or with status 400 (Bad Request) if the doctor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/doctors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody Doctor doctor) throws URISyntaxException {
        log.debug("REST request to save Doctor : {}", doctor);
        if (doctor.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("doctor", "idexists", "A new doctor cannot already have an ID")).body(null);
        }
        Doctor result = doctorRepository.save(doctor);
        return ResponseEntity.created(new URI("/api/doctors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("doctor", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /doctors : Updates an existing doctor.
     *
     * @param doctor the doctor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated doctor,
     * or with status 400 (Bad Request) if the doctor is not valid,
     * or with status 500 (Internal Server Error) if the doctor couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/doctors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Doctor> updateDoctor(@Valid @RequestBody Doctor doctor) throws URISyntaxException {
        log.debug("REST request to update Doctor : {}", doctor);
        if (doctor.getId() == null) {
            return createDoctor(doctor);
        }
        Doctor result = doctorRepository.save(doctor);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("doctor", doctor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /doctors : get all the doctors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of doctors in body
     */
    @RequestMapping(value = "/doctors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Doctor> getAllDoctors() {
        log.debug("REST request to get all Doctors");
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors;
    }

    /**
     * GET  /doctors/:id : get the "id" doctor.
     *
     * @param id the id of the doctor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the doctor, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/doctors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        log.debug("REST request to get Doctor : {}", id);
        Doctor doctor = doctorRepository.findOne(id);
        return Optional.ofNullable(doctor)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /doctors/:id : delete the "id" doctor.
     *
     * @param id the id of the doctor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/doctors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        log.debug("REST request to delete Doctor : {}", id);
        doctorRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("doctor", id.toString())).build();
    }

}
