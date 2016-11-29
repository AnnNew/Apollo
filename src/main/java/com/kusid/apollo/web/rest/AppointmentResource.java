package com.kusid.apollo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.kusid.apollo.domain.Appointment;
import com.kusid.apollo.repository.AppointmentRepository;
import com.kusid.apollo.security.AuthoritiesConstants;
import com.kusid.apollo.service.MailService;
import com.kusid.apollo.service.UserService;
import com.kusid.apollo.service.util.Availability;
import com.kusid.apollo.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Appointment.
 */
@RestController
@RequestMapping("/api")
public class AppointmentResource {

    private final Logger log = LoggerFactory.getLogger(AppointmentResource.class);

    @Inject
    private AppointmentRepository appointmentRepository;

    @Inject
    private UserService userService;

    @Inject
    private Availability availability;

    @Inject
    private MailService mailService;

    /**
     * POST  /appointments : Create a new appointment.
     *
     * @param appointment the appointment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new appointment, or with status 400 (Bad Request) if the appointment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/appointments",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody Appointment appointment) throws URISyntaxException {
        log.debug("REST request to save Appointment : {}", appointment);

        if (appointment.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "idexists", "A new appointment cannot already have an ID")).body(null);
        }
        if (appointment.getAppointmentStart().isBefore(ZonedDateTime.now())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Start time cannot be in the past")).body(null);
        }
        if (appointment.getAppointmentStart().isAfter(appointment.getAppointmentEnd())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Start time cannot be more than end time")).body(null);
        }
        if (ChronoUnit.HOURS.between(appointment.getAppointmentStart(), appointment.getAppointmentEnd()) > 1) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Appointments cannot be made for more than 1 hour.")).body(null);
        }
        if (appointment.getAppointmentStart().toLocalDateTime().getHour() < 11 || appointment.getAppointmentEnd().toLocalDateTime().getHour() > 18) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Appointment can only be booked between 11 AM to 6 PM.")).body(null);
        }

        if (availability.isAppointmentWithDoctorAvailable(appointment.getAppointmentStart(), appointment.getAppointmentEnd(), appointment.getDoctor().getId())) {
            appointment.setUser(userService.getUserWithAuthorities());
            Appointment result = appointmentRepository.save(appointment);

            mailService.sendConfirmationEmail(userService.getUserWithAuthorities(), appointment);

            return ResponseEntity.created(new URI("/api/appointments/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("appointment", result.getId().toString()))
                .body(result);
        }
        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Appointment not available for the selected time and day.")).body(null);
    }

    /**
     * PUT  /appointments : Updates an existing appointment.
     *
     * @param appointment the appointment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated appointment,
     * or with status 400 (Bad Request) if the appointment is not valid,
     * or with status 500 (Internal Server Error) if the appointment couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/appointments",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appointment> updateAppointment(@Valid @RequestBody Appointment appointment) throws URISyntaxException {
        log.debug("REST request to update Appointment : {}", appointment);
        if (appointment.getId() == null) {
            return createAppointment(appointment);
        }
        if (appointment.getAppointmentStart().isBefore(ZonedDateTime.now())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Start time cannot be in the past")).body(null);
        }
        if (appointment.getAppointmentStart().isAfter(appointment.getAppointmentEnd())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Start time cannot be more than end time")).body(null);
        }
        if (ChronoUnit.HOURS.between(appointment.getAppointmentStart(), appointment.getAppointmentEnd()) > 1) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Appointments cannot be made for more than 1 hour.")).body(null);
        }
        if (appointment.getAppointmentStart().toLocalDateTime().getHour() < 11 || appointment.getAppointmentEnd().toLocalDateTime().getHour() > 18) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Appointment can only be booked between 11 AM to 6 PM.")).body(null);
        }
        if (availability.isAppointmentWithDoctorAvailable(appointment.getAppointmentStart(), appointment.getAppointmentEnd(), appointment.getDoctor().getId())) {
            appointment.setUser(userService.getUserWithAuthorities());
            Appointment result = appointmentRepository.save(appointment);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("appointment", appointment.getId().toString()))
                .body(result);
        }
        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appointment", "invalid", "Appointment not available for the selected time and day.")).body(null);
    }

    /**
     * GET  /appointments : get all the appointments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of appointments in body
     */
    @RequestMapping(value = "/appointments",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Appointment> getAllAppointments() {
        log.debug("REST request to get all Appointments");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ADMIN)) {
                    return appointmentRepository.findAll();
                }
            }
        }
        return appointmentRepository.findByUserIsCurrentUser();
    }

    /**
     * GET  /appointments/:id : get the "id" appointment.
     *
     * @param id the id of the appointment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the appointment, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/appointments/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
        log.debug("REST request to get Appointment : {}", id);
        Appointment appointment = appointmentRepository.findOne(id);
        return Optional.ofNullable(appointment)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /appointments/:id : delete the "id" appointment.
     *
     * @param id the id of the appointment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/appointments/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        log.debug("REST request to delete Appointment : {}", id);
        appointmentRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("appointment", id.toString())).build();
    }
}
