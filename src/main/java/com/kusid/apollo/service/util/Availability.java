package com.kusid.apollo.service.util;

import com.kusid.apollo.domain.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * Created by arkham on 7/10/16.
 *
 */
@Component
public class Availability {

    @PersistenceContext
    private EntityManager em;

    public boolean isAppointmentWithDoctorAvailable(ZonedDateTime startTime, ZonedDateTime endTime, Long doctorId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Appointment> cq = cb.createQuery(Appointment.class);
        Root<Appointment> rootEntry = cq.from(Appointment.class);
        CriteriaQuery<Appointment> all = cq.select(rootEntry);

        Path<Long> foreignKey = rootEntry.join("doctor").get("id");
        Predicate condition = cb.equal(foreignKey, doctorId);
        all.where(condition);
        TypedQuery<Appointment> allQuery = em.createQuery(all);

        List<Appointment> allAppointments = allQuery.getResultList();
        boolean exists = true;

        for (Appointment appointment : allAppointments) {
            System.out.println("Appointment start: " + appointment.getAppointmentStart());
            System.out.println("Appointment end: " + appointment.getAppointmentEnd());

            // Overlap if (StartDate1 <= EndDate2) and (StartDate2 <= EndDate1)
            if (appointment.getAppointmentStart().isBefore(endTime) && startTime.isBefore(appointment.getAppointmentEnd())) {
                exists = false;
            }
        }
        return exists;
    }

}
