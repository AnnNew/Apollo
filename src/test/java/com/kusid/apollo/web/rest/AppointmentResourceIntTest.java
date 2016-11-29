package com.kusid.apollo.web.rest;

import com.kusid.apollo.ApolloApp;
import com.kusid.apollo.domain.Appointment;
import com.kusid.apollo.repository.AppointmentRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AppointmentResource REST controller.
 *
 * @see AppointmentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApolloApp.class)
@WebAppConfiguration
@IntegrationTest
public class AppointmentResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_APPOINTMENT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_APPOINTMENT_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_APPOINTMENT_START_STR = dateTimeFormatter.format(DEFAULT_APPOINTMENT_START);

    private static final ZonedDateTime DEFAULT_APPOINTMENT_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_APPOINTMENT_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_APPOINTMENT_END_STR = dateTimeFormatter.format(DEFAULT_APPOINTMENT_END);

    @Inject
    private AppointmentRepository appointmentRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAppointmentMockMvc;

    private Appointment appointment;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AppointmentResource appointmentResource = new AppointmentResource();
        ReflectionTestUtils.setField(appointmentResource, "appointmentRepository", appointmentRepository);
        this.restAppointmentMockMvc = MockMvcBuilders.standaloneSetup(appointmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        appointment = new Appointment();
        appointment.setAppointmentStart(DEFAULT_APPOINTMENT_START);
        appointment.setAppointmentEnd(DEFAULT_APPOINTMENT_END);
    }

    @Test
    @Transactional
    public void createAppointment() throws Exception {
        int databaseSizeBeforeCreate = appointmentRepository.findAll().size();

        // Create the Appointment

        restAppointmentMockMvc.perform(post("/api/appointments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appointment)))
                .andExpect(status().isCreated());

        // Validate the Appointment in the database
        List<Appointment> appointments = appointmentRepository.findAll();
        assertThat(appointments).hasSize(databaseSizeBeforeCreate + 1);
        Appointment testAppointment = appointments.get(appointments.size() - 1);
        assertThat(testAppointment.getAppointmentStart()).isEqualTo(DEFAULT_APPOINTMENT_START);
        assertThat(testAppointment.getAppointmentEnd()).isEqualTo(DEFAULT_APPOINTMENT_END);
    }

    @Test
    @Transactional
    public void checkAppointmentStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setAppointmentStart(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(post("/api/appointments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appointment)))
                .andExpect(status().isBadRequest());

        List<Appointment> appointments = appointmentRepository.findAll();
        assertThat(appointments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAppointmentEndIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setAppointmentEnd(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(post("/api/appointments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appointment)))
                .andExpect(status().isBadRequest());

        List<Appointment> appointments = appointmentRepository.findAll();
        assertThat(appointments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAppointments() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointments
        restAppointmentMockMvc.perform(get("/api/appointments?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
                .andExpect(jsonPath("$.[*].appointmentStart").value(hasItem(DEFAULT_APPOINTMENT_START_STR)))
                .andExpect(jsonPath("$.[*].appointmentEnd").value(hasItem(DEFAULT_APPOINTMENT_END_STR)));
    }

    @Test
    @Transactional
    public void getAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get the appointment
        restAppointmentMockMvc.perform(get("/api/appointments/{id}", appointment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(appointment.getId().intValue()))
            .andExpect(jsonPath("$.appointmentStart").value(DEFAULT_APPOINTMENT_START_STR))
            .andExpect(jsonPath("$.appointmentEnd").value(DEFAULT_APPOINTMENT_END_STR));
    }

    @Test
    @Transactional
    public void getNonExistingAppointment() throws Exception {
        // Get the appointment
        restAppointmentMockMvc.perform(get("/api/appointments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment
        Appointment updatedAppointment = new Appointment();
        updatedAppointment.setId(appointment.getId());
        updatedAppointment.setAppointmentStart(UPDATED_APPOINTMENT_START);
        updatedAppointment.setAppointmentEnd(UPDATED_APPOINTMENT_END);

        restAppointmentMockMvc.perform(put("/api/appointments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAppointment)))
                .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointments = appointmentRepository.findAll();
        assertThat(appointments).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointments.get(appointments.size() - 1);
        assertThat(testAppointment.getAppointmentStart()).isEqualTo(UPDATED_APPOINTMENT_START);
        assertThat(testAppointment.getAppointmentEnd()).isEqualTo(UPDATED_APPOINTMENT_END);
    }

    @Test
    @Transactional
    public void deleteAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);
        int databaseSizeBeforeDelete = appointmentRepository.findAll().size();

        // Get the appointment
        restAppointmentMockMvc.perform(delete("/api/appointments/{id}", appointment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Appointment> appointments = appointmentRepository.findAll();
        assertThat(appointments).hasSize(databaseSizeBeforeDelete - 1);
    }
}
