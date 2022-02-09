package com.tenniscourts.tenniscourts;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TennisCourtService
{

    private final TennisCourtRepository tennisCourtRepository;

    private final ScheduleService scheduleService;

    private final TennisCourtMapper tennisCourtMapper;

    public List<TennisCourtDTO> listAll( )
    {
	return tennisCourtMapper.map( tennisCourtRepository.findAll( ) );
    }

    public TennisCourtDTO listById( Long id )
    {
	return tennisCourtRepository.findById( id ).map( tennisCourtMapper::map ).orElseThrow( ( ) -> {
	    throw new EntityNotFoundException( "Tennis Court not found." );
	} );
    }

    public TennisCourtDTO add( TennisCourtDTO tennisCourt )
    {
	return tennisCourtMapper.map( tennisCourtRepository.saveAndFlush( tennisCourtMapper.map( tennisCourt ) ) );
    }

    public TennisCourtDTO update( TennisCourtDTO tennisCourtDTO )
    {
	listById( tennisCourtDTO.getId( ) );
	TennisCourt tennisCourt = tennisCourtRepository.save( tennisCourtMapper.map( tennisCourtDTO ) );

	return tennisCourtMapper.map( tennisCourt );
    }

    public void delete( Long tennisCourtId )
    {
	listById( tennisCourtId );
	tennisCourtRepository.deleteById( tennisCourtId );
    }

    public TennisCourtDTO findByIdWithSchedules( Long tennisCourtId )
    {
	TennisCourtDTO tennisCourtDTO = listById( tennisCourtId );
	tennisCourtDTO.setTennisCourtSchedules( scheduleService.listByTennisCourtId( tennisCourtId ) );

	return tennisCourtDTO;
    }
}
