package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping( "/schedules" )
public class ScheduleController extends BaseRestController
{
    private final ScheduleService scheduleService;

    @PostMapping
    @ApiOperation( value = "Add a new slot on a Tennis Court schedule" )
    public ResponseEntity<Void> add( @RequestBody ScheduleSlotDTO scheduleSlotDTO )
    {
	return ResponseEntity.created( locationByEntity(
			scheduleService.add( scheduleSlotDTO.getTennisCourtId( ), scheduleSlotDTO ).getId( ) ) ).build( );
    }

    @GetMapping( "/{scheduleId}" )
    @ApiOperation( value = "Find a schedule by id" )
    public ResponseEntity<ScheduleDTO> listById( @PathVariable Long scheduleId )
    {
	return ResponseEntity.ok( scheduleService.listById( scheduleId ) );
    }


    @PostMapping( "/filters" )
    @ApiOperation( value = "Find schedule slots between 2 given dates" )
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(
		    @RequestBody ScheduleFiltersDTO scheduleFiltersDTO )
    {
	return ResponseEntity.ok( scheduleService.listByFilter( scheduleFiltersDTO.getStartDateTime( ),
			scheduleFiltersDTO.getEndDateTime( ) ) );
    }

}
