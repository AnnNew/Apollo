package com.kusid.apollo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "appointment_start", nullable = false)
    private ZonedDateTime appointmentStart;

    @NotNull
    @Column(name = "appointment_end", nullable = false)
    private ZonedDateTime appointmentEnd;

    @ManyToOne
    private User user;

    @ManyToOne
    @NotNull
    private Doctor doctor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(ZonedDateTime appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    public ZonedDateTime getAppointmentEnd() {
        return appointmentEnd;
    }

    public void setAppointmentEnd(ZonedDateTime appointmentEnd) {
        this.appointmentEnd = appointmentEnd;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Appointment appointment = (Appointment) o;
        if(appointment.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, appointment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + id +
            ", appointmentStart='" + appointmentStart + "'" +
            ", appointmentEnd='" + appointmentEnd + "'" +
            '}';
    }
}
