package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestDTO;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleMapper;
import com.tenniscourts.schedules.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService
{
    private final BigDecimal            RESERVATION_FEE = new BigDecimal(10);

    private final GuestService          guestService;
    private final ScheduleService       scheduleService;
    private final ReservationMapper     reservationMapper;
    private final ReservationRepository reservationRepository;

    public ReservationDTO bookReservation( CreateReservationRequestDTO createReservationRequestDTO )
    {
        ScheduleDTO scheduleDTO = scheduleService.listById( createReservationRequestDTO.getScheduleId( ) );
        Schedule    schedule    = scheduleService.getScheduleMapper( ).map( scheduleDTO );

        if( reservationExists( schedule ) )
            throw new AlreadyExistsEntityException( "Reservation already exists." );

        if( reservationInPast( schedule ) )
            throw new IllegalArgumentException( "It is forbidden to reserve on past." );

        GuestDTO guestDTO = guestService.findById( createReservationRequestDTO.getGuestId( ) );
        Guest    guest    = guestService.getGuestMapper( ).map( guestDTO );

        Reservation reservation = Reservation.builder( )
                        .guest( guest )
                        .schedule( schedule )
                        .reservationStatus( ReservationStatus.READY_TO_PLAY ).value( RESERVATION_FEE )
                        .build( );

        return reservationMapper.map( reservationRepository.save( reservation) );
    }

    public ReservationDTO findReservation( Long reservationId )
    {
	return reservationRepository.findById( reservationId ).map( reservationMapper::map )
			.<EntityNotFoundException>orElseThrow( ( ) -> {
			    throw new EntityNotFoundException( "Reservation not found." );
			} );
    }

    public ReservationDTO cancelReservation( Long reservationId )
    {
	return reservationMapper.map( this.cancel( reservationId ) );
    }

    private Reservation cancel( Long reservationId )
    {
	return reservationRepository.findById( reservationId ).map( reservation -> {

	    this.validateCancellation( reservation );

	    BigDecimal refundValue = getRefundValue( reservation );
	    return this.updateReservation( reservation, refundValue, ReservationStatus.CANCELLED );

	} ).orElseThrow( ( ) -> {
	    throw new EntityNotFoundException( "Reservation not found." );
	} );
    }

    private Reservation updateReservation( Reservation reservation, BigDecimal refundValue, ReservationStatus status )
    {
	reservation.setReservationStatus( status );
	reservation.setValue( reservation.getValue( ).subtract( refundValue ) );
	reservation.setRefundValue( refundValue );

	return reservationRepository.save( reservation );
    }

    private void validateCancellation( Reservation reservation )
    {
	if( !ReservationStatus.READY_TO_PLAY.equals( reservation.getReservationStatus( ) ) )
	{
	    throw new IllegalArgumentException( "Cannot cancel/reschedule because it's not in ready to play status." );
	}

	if( reservation.getSchedule( ).getStartDateTime( ).isBefore( LocalDateTime.now( ) ) )
	{
	    throw new IllegalArgumentException( "Can cancel/reschedule only future dates." );
	}
    }

    public BigDecimal getRefundValue( Reservation reservation )
    {
	long hours = ChronoUnit.HOURS.between( LocalDateTime.now( ), reservation.getSchedule( ).getStartDateTime( ) );

	if( hours >= 24 )
	    return reservation.getValue( );

	if( hours >= 12 )
	    return reservation.getValue( ).multiply( BigDecimal.valueOf( 0.75 ) );

	if( hours >= 2 )
	    return reservation.getValue( ).multiply( BigDecimal.valueOf( 0.50 ) );

	if( hours >= 0 )
	    return reservation.getValue( ).multiply( BigDecimal.valueOf( 0.25 ) );

	return BigDecimal.ZERO;
    }

    public ReservationDTO rescheduleReservation( Long previousReservationId, Long scheduleId )
    {
	Reservation previousReservation = reservationMapper.map( findReservation( previousReservationId ) );

	if( scheduleId.equals( previousReservation.getSchedule( ).getId( ) ) )
	    throw new IllegalArgumentException( "Cannot reschedule to the same slot." );

	if( !ReservationStatus.READY_TO_PLAY.equals( previousReservation.getReservationStatus( ) ) )
	    throw new IllegalArgumentException( "Cannot reschedule: The reserve its not ready to play." );

	BigDecimal refund = getRefundValue( previousReservation );

	previousReservation.setReservationStatus( ReservationStatus.RESCHEDULED );
	previousReservation.setValue( previousReservation.getValue( ).subtract( refund ) );
	previousReservation.setRefundValue( refund );
	reservationRepository.save( previousReservation );

	ReservationDTO reservation = bookReservation(
			CreateReservationRequestDTO.builder( )
					.guestId( previousReservation.getGuest( ).getId( ) )
					.scheduleId( scheduleId )
					.build( ) );

	reservation.setPreviousReservation( reservationMapper.map( previousReservation ) );

	return reservation;
    }

    private boolean reservationExists( Schedule schedule )
    {
        List<Reservation> reservationList = reservationRepository.findBySchedule_Id( schedule.getId( ) );

        return reservationList.stream( )
                        .anyMatch( reservation -> ReservationStatus.READY_TO_PLAY.equals( reservation.getReservationStatus( ) ) );
    }

    private boolean reservationInPast( Schedule schedule )
    {
        return schedule.getStartDateTime( ).isBefore( LocalDateTime.now( ) );
    }
}
