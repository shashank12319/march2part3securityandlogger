package com.wittybrains.busbookingsystem.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wittybrains.busbookingsystem.dto.BookingDTO;
import com.wittybrains.busbookingsystem.dto.BusDTO;
import com.wittybrains.busbookingsystem.model.Booking;
import com.wittybrains.busbookingsystem.model.Bus;
import com.wittybrains.busbookingsystem.model.TravelSchedule;
import com.wittybrains.busbookingsystem.repository.BookingRepository;
import com.wittybrains.busbookingsystem.repository.BusRepository;
import com.wittybrains.busbookingsystem.repository.TravelScheduleRepository;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	private final BusRepository busRepository;
	private final TravelScheduleRepository travelScheduleRepository;

	@Autowired
	public BookingService(BookingRepository bookingRepository, BusRepository busRepository,
			TravelScheduleRepository travelScheduleRepository) {
		this.bookingRepository = bookingRepository;
		this.busRepository = busRepository;
		this.travelScheduleRepository = travelScheduleRepository;
	}

	public BookingDTO createBooking(BookingDTO bookingDTO) {
		Booking booking = new Booking();
		BusDTO busDTO = bookingDTO.getBus();

		Optional<Bus> optionalBus = busRepository.findById(busDTO.getId());
		optionalBus.ifPresent(bus -> booking.setBus(bus));

		Optional<TravelSchedule> optionalTravelSchedule = travelScheduleRepository
				.findById(bookingDTO.getSchedule().getId());
		optionalTravelSchedule.ifPresent(travelSchedule -> booking.setSchedule(travelSchedule));
		booking.setDateOfBooking(bookingDTO.getDateOfBooking());

		booking.setNumberOfSeats(bookingDTO.getNumberOfSeats());

		booking.setRouteInfo(bookingDTO.getRouteInfo());

		booking.setTotalAmount(bookingDTO.getTotalAmount());
		booking.setBookingStatus(bookingDTO.getBookingStatus());

		Booking savedBooking = bookingRepository.save(booking);
		return new BookingDTO(savedBooking);
	}

}
