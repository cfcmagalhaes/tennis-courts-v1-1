package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import com.tenniscourts.tenniscourts.TennisCourtMapper;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService
{
    private final ScheduleRepository    scheduleRepository;
    private final TennisCourtRepository tennisCourtRepository;
    @Getter
    private final ScheduleMapper        scheduleMapper;
    private final TennisCourtMapper     tennisCourtMapper;

    public ScheduleDTO add( Long tennisCourtId, ScheduleSlotDTO scheduleSlotDTO )
    {
	// Past date treatment
	if( scheduleSlotDTO.getStartDateTime( ).isBefore( LocalDateTime.now( ) ) )
	    throw new IllegalArgumentException( "It's forbidden add slots on the past" );

	// Slot already exist treatment
	if( scheduleRepository.findByTennisCourt_IdAndStartDateTime( tennisCourtId,
			scheduleSlotDTO.getStartDateTime( ) ).isPresent( ) )
	    throw new AlreadyExistsEntityException( "Schedule slot already exists." );

	// TennisCourt not exist treatment
	TennisCourtDTO tennisCourtDTO = tennisCourtRepository.findById( tennisCourtId ).map( tennisCourtMapper::map )
			.<EntityNotFoundException>orElseThrow( ( ) -> {
			    throw new EntityNotFoundException( "Tennis Court not found." );
			} );

	Schedule schedule = addNewSlot( tennisCourtId, scheduleSlotDTO, tennisCourtDTO );

	return scheduleMapper.map( scheduleRepository.save( schedule ) );
    }

    public ScheduleDTO listById( Long scheduleId )
    {
	return scheduleRepository.findById( scheduleId ).map( scheduleMapper::map )
			.<EntityNotFoundException>orElseThrow( ( ) -> {
			    throw new EntityNotFoundException(
					    "Schedule (#" + scheduleId + ") was not found" );
			} );
    }

    public List<ScheduleDTO> listByFilter( LocalDateTime startDate, LocalDateTime endDate )
    {
	List<Schedule> scheduleList = scheduleRepository.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(
			startDate, endDate )
			.stream( )
			.collect( Collectors.toList( ) );
	return scheduleMapper.map( scheduleList );
    }

    public List<ScheduleDTO> listByTennisCourtId( Long tennisCourtId )
    {
	return scheduleMapper.map( scheduleRepository.findByTennisCourt_IdOrderByStartDateTime( tennisCourtId ) );
    }

    private Schedule addNewSlot( Long tennisCourtId, ScheduleSlotDTO scheduleSlotDTO, TennisCourtDTO tennisCourtDTO )
    {
	TennisCourt tennisCourt     = tennisCourtMapper.map( tennisCourtDTO );
	LocalDateTime scheduleStart = scheduleSlotDTO.getStartDateTime( );
	LocalDateTime scheduleEnd   = scheduleStart.plusHours( 1L );

	return Schedule.builder( ).tennisCourt( tennisCourt ).startDateTime( scheduleStart ).endDateTime( scheduleEnd )
			.build( );
    }
}
