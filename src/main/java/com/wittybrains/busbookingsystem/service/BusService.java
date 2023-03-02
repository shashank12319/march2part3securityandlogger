package com.wittybrains.busbookingsystem.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wittybrains.busbookingsystem.dto.BusDTO;
import com.wittybrains.busbookingsystem.model.Bus;
import com.wittybrains.busbookingsystem.model.Seat;
import com.wittybrains.busbookingsystem.repository.BusRepository;

@Service
public class BusService {

    private final BusRepository busRepository;

    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    public List<BusDTO> createBuses(List<BusDTO> busList) {
        List<BusDTO> createdBuses = new ArrayList<>();
        for (BusDTO busDTO : busList) {
            Bus bus = new Bus();
            bus.setDestination(busDTO.getDestination());
            bus.setNumber(busDTO.getNumber());
            bus.setSource(busDTO.getSource());
            bus.setType(busDTO.getType());
            bus.setTiming(busDTO.getTiming());
            bus.setNumberOfSeats(busDTO.getNumberOfSeats());
            // Create seats for the bus
            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= busDTO.getNumberOfSeats(); i++) {
                Seat seat = new Seat();
                seat.setSeatNumber(String.valueOf(i));
                // seat.setBookingStatus(BookingStatus.AVAILABLE);
                // seat.setSeatType(busDTO.getSeatType());
                seat.setPrice(busDTO.getPrice());
                seat.setBus(bus);
                seats.add(seat);
            }
            bus.setSeats(seats);

            busRepository.save(bus);
            createdBuses.add(new BusDTO(bus));
        }
        return createdBuses;
    }
}