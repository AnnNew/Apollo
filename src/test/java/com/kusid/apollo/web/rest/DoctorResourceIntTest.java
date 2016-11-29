package com.kusid.apollo.web.rest;

import com.kusid.apollo.ApolloApp;
import com.kusid.apollo.domain.Doctor;
import com.kusid.apollo.repository.DoctorRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kusid.apollo.domain.enumeration.Gender;

/**
 * Test class for the DoctorResource REST controller.
 *
 * @see DoctorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApolloApp.class)
@WebAppConfiguration
@IntegrationTest
public class DoctorResourceIntTest {

    private static final String DEFAULT_FIRST_NAME = "AAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBB";
    private static final String DEFAULT_LAST_NAME = "AAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBB";
    private static final String DEFAULT_QUALIFICATION = "AAAAA";
    private static final String UPDATED_QUALIFICATION = "BBBBB";

    private static final Integer DEFAULT_YEARS_OF_EXPERIENCE = 0;
    private static final Integer UPDATED_YEARS_OF_EXPERIENCE = 1;
    private static final String DEFAULT_SPECIALITY = "AAAAA";
    private static final String UPDATED_SPECIALITY = "BBBBB";

    private static final Long DEFAULT_CONTACT_NUM = 1L;
    private static final Long UPDATED_CONTACT_NUM = 2L;
    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    @Inject
    private DoctorRepository doctorRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDoctorMockMvc;

    private Doctor doctor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DoctorResource doctorResource = new DoctorResource();
        ReflectionTestUtils.setField(doctorResource, "doctorRepository", doctorRepository);
        this.restDoctorMockMvc = MockMvcBuilders.standaloneSetup(doctorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        doctor = new Doctor();
        doctor.setFirstName(DEFAULT_FIRST_NAME);
        doctor.setLastName(DEFAULT_LAST_NAME);
        doctor.setQualification(DEFAULT_QUALIFICATION);
        doctor.setYearsOfExperience(DEFAULT_YEARS_OF_EXPERIENCE);
        doctor.setSpeciality(DEFAULT_SPECIALITY);
        doctor.setContactNum(DEFAULT_CONTACT_NUM);
        doctor.setEmail(DEFAULT_EMAIL);
        doctor.setGender(DEFAULT_GENDER);
    }

    @Test
    @Transactional
    public void createDoctor() throws Exception {
        int databaseSizeBeforeCreate = doctorRepository.findAll().size();

        // Create the Doctor

        restDoctorMockMvc.perform(post("/api/doctors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doctor)))
                .andExpect(status().isCreated());

        // Validate the Doctor in the database
        List<Doctor> doctors = doctorRepository.findAll();
        assertThat(doctors).hasSize(databaseSizeBeforeCreate + 1);
        Doctor testDoctor = doctors.get(doctors.size() - 1);
        assertThat(testDoctor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testDoctor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testDoctor.getQualification()).isEqualTo(DEFAULT_QUALIFICATION);
        assertThat(testDoctor.getYearsOfExperience()).isEqualTo(DEFAULT_YEARS_OF_EXPERIENCE);
        assertThat(testDoctor.getSpeciality()).isEqualTo(DEFAULT_SPECIALITY);
        assertThat(testDoctor.getContactNum()).isEqualTo(DEFAULT_CONTACT_NUM);
        assertThat(testDoctor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testDoctor.getGender()).isEqualTo(DEFAULT_GENDER);
    }

    @Test
    @Transactional
    public void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setFirstName(null);

        // Create the Doctor, which fails.

        restDoctorMockMvc.perform(post("/api/doctors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doctor)))
                .andExpect(status().isBadRequest());

        List<Doctor> doctors = doctorRepository.findAll();
        assertThat(doctors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setLastName(null);

        // Create the Doctor, which fails.

        restDoctorMockMvc.perform(post("/api/doctors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doctor)))
                .andExpect(status().isBadRequest());

        List<Doctor> doctors = doctorRepository.findAll();
        assertThat(doctors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setGender(null);

        // Create the Doctor, which fails.

        restDoctorMockMvc.perform(post("/api/doctors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doctor)))
                .andExpect(status().isBadRequest());

        List<Doctor> doctors = doctorRepository.findAll();
        assertThat(doctors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDoctors() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctors
        restDoctorMockMvc.perform(get("/api/doctors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
                .andExpect(jsonPath("$.[*].qualification").value(hasItem(DEFAULT_QUALIFICATION.toString())))
                .andExpect(jsonPath("$.[*].yearsOfExperience").value(hasItem(DEFAULT_YEARS_OF_EXPERIENCE)))
                .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY.toString())))
                .andExpect(jsonPath("$.[*].contactNum").value(hasItem(DEFAULT_CONTACT_NUM.intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())));
    }

    @Test
    @Transactional
    public void getDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get the doctor
        restDoctorMockMvc.perform(get("/api/doctors/{id}", doctor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(doctor.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.qualification").value(DEFAULT_QUALIFICATION.toString()))
            .andExpect(jsonPath("$.yearsOfExperience").value(DEFAULT_YEARS_OF_EXPERIENCE))
            .andExpect(jsonPath("$.speciality").value(DEFAULT_SPECIALITY.toString()))
            .andExpect(jsonPath("$.contactNum").value(DEFAULT_CONTACT_NUM.intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDoctor() throws Exception {
        // Get the doctor
        restDoctorMockMvc.perform(get("/api/doctors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();

        // Update the doctor
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(doctor.getId());
        updatedDoctor.setFirstName(UPDATED_FIRST_NAME);
        updatedDoctor.setLastName(UPDATED_LAST_NAME);
        updatedDoctor.setQualification(UPDATED_QUALIFICATION);
        updatedDoctor.setYearsOfExperience(UPDATED_YEARS_OF_EXPERIENCE);
        updatedDoctor.setSpeciality(UPDATED_SPECIALITY);
        updatedDoctor.setContactNum(UPDATED_CONTACT_NUM);
        updatedDoctor.setEmail(UPDATED_EMAIL);
        updatedDoctor.setGender(UPDATED_GENDER);

        restDoctorMockMvc.perform(put("/api/doctors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDoctor)))
                .andExpect(status().isOk());

        // Validate the Doctor in the database
        List<Doctor> doctors = doctorRepository.findAll();
        assertThat(doctors).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctors.get(doctors.size() - 1);
        assertThat(testDoctor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testDoctor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testDoctor.getQualification()).isEqualTo(UPDATED_QUALIFICATION);
        assertThat(testDoctor.getYearsOfExperience()).isEqualTo(UPDATED_YEARS_OF_EXPERIENCE);
        assertThat(testDoctor.getSpeciality()).isEqualTo(UPDATED_SPECIALITY);
        assertThat(testDoctor.getContactNum()).isEqualTo(UPDATED_CONTACT_NUM);
        assertThat(testDoctor.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDoctor.getGender()).isEqualTo(UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void deleteDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);
        int databaseSizeBeforeDelete = doctorRepository.findAll().size();

        // Get the doctor
        restDoctorMockMvc.perform(delete("/api/doctors/{id}", doctor.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Doctor> doctors = doctorRepository.findAll();
        assertThat(doctors).hasSize(databaseSizeBeforeDelete - 1);
    }
}
