package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping( "tennisCourts" )
public class TennisCourtController extends BaseRestController
{

    private final TennisCourtService tennisCourtService;

    @GetMapping
    @ApiOperation( value = "List all Tennis Courts" )
    public ResponseEntity<List<TennisCourtDTO>> listAll( )
    {
	return ResponseEntity.ok( tennisCourtService.listAll( ) );
    }

    @GetMapping( "/{tennisCourtId}" )
    @ApiOperation( value = "Find Tennis Court by id" )
    public ResponseEntity<TennisCourtDTO> listById( @PathVariable Long tennisCourtId )
    {
	return ResponseEntity.ok( tennisCourtService.listById( tennisCourtId ) );
    }

    @PostMapping
    @ApiOperation( value = "Add a Tennis Court" )
    public ResponseEntity<Void> add( @RequestBody TennisCourtDTO tennisCourtDTO )
    {
	return ResponseEntity.created( locationByEntity( tennisCourtService.add( tennisCourtDTO ).getId( ) ) ).build( );
    }

    @PutMapping
    @ApiOperation( value = "Update a Tennis Court" )
    public ResponseEntity<TennisCourtDTO> update( @RequestBody TennisCourtDTO tennisCourtDTO )
    {
	return ResponseEntity.ok( tennisCourtService.update( tennisCourtDTO ) );
    }

    @DeleteMapping( "/{tennisCourtId}" )
    @ApiOperation( value = "Delete a Tennis Court" )
    public ResponseEntity<Void> delete( @PathVariable Long tennisCourtId )
    {
	tennisCourtService.delete( tennisCourtId );
	return ResponseEntity.ok( ).build( );
    }

    @GetMapping( "/{tennisCourtId}/schedules" )
    @ApiOperation( value = "Find Tennis Court with schedules" )
    public ResponseEntity<TennisCourtDTO> findByIdWithSchedules( @PathVariable Long tennisCourtId )
    {
	return ResponseEntity.ok( tennisCourtService.findByIdWithSchedules( tennisCourtId ) );
    }
}