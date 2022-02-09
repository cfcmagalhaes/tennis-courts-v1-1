package com.tenniscourts.schedules;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>
{
    List<Schedule> findByTennisCourt_IdOrderByStartDateTime( Long tennisCourtId );

    Optional<Object> findByTennisCourt_IdAndStartDateTime( Long tennisCourtId, LocalDateTime startDateTime );

    List<Schedule> findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual( LocalDateTime startDate,
		    LocalDateTime endDate );
}
