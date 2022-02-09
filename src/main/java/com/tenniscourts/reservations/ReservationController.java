package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping( "/reservations" )
public class ReservationController extends BaseRestController
{
    private final ReservationService reservationService;

    @PostMapping
    @ApiOperation( value = "Book a reservation." )
    public ResponseEntity<Void> bookReservation( @RequestBody CreateReservationRequestDTO createReservationRequestDTO )
    {
	return ResponseEntity.created( locationByEntity( reservationService.bookReservation( createReservationRequestDTO )
                                        .getId( ) ) ).build( );
    }

    @GetMapping( "/{reservationId}")
    @ApiOperation( value = "Find a reservation." )
    public ResponseEntity<ReservationDTO> findReservation( @PathVariable Long reservationId )
    {
	return ResponseEntity.ok( reservationService.findReservation( reservationId ) );
    }

    @DeleteMapping( "/{reservationId}" )
    @ApiOperation( value = "Cancel a reservation." )
    public ResponseEntity<ReservationDTO> cancelReservation( @PathVariable Long reservationId )
    {
	return ResponseEntity.ok( reservationService.cancelReservation( reservationId ) );
    }

    @PutMapping( "/{reservationId}/{scheduleId}" )
    @ApiOperation( value = "Reschedule a reservation." )
    public ResponseEntity<ReservationDTO> rescheduleReservation( @PathVariable Long reservationId, @PathVariable Long scheduleId )
    {
        return ResponseEntity.ok( reservationService.rescheduleReservation( reservationId, scheduleId ) );
    }
}
