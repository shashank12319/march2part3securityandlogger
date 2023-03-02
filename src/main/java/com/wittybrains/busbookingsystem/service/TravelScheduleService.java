 
package com.wittybrains.busbookingsystem.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wittybrains.busbookingsystem.dto.StationDTO;
import com.wittybrains.busbookingsystem.dto.TravelScheduleDTO;
import com.wittybrains.busbookingsystem.exception.InvalidSourceOrDestinationException;
import com.wittybrains.busbookingsystem.exception.StationNotFoundException;
import com.wittybrains.busbookingsystem.model.Bus;
import com.wittybrains.busbookingsystem.model.Station;
import com.wittybrains.busbookingsystem.model.TravelSchedule;
import com.wittybrains.busbookingsystem.repository.BusRepository;
import com.wittybrains.busbookingsystem.repository.StationRepository;
import com.wittybrains.busbookingsystem.repository.TravelScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class TravelScheduleService {
	private static final Logger logger = LoggerFactory.getLogger(TravelScheduleService.class);
	private static final int MAX_SEARCH_DAYS = 30;
	@Autowired
	private TravelScheduleRepository scheduleRepository;

	@Autowired
	private StationRepository stationRepository;

	public Station getStationByCode(String code) {
		

if (StringUtils.isBlank(code)) {
    throw new IllegalArgumentException("Code must not be null or empty");
}

		Optional<Station> optionalStation = stationRepository.findByStationCode(code);
		if (optionalStation.isPresent()) {
			return optionalStation.get();
		} else {
			throw new StationNotFoundException("Station with code " + code + " not found");
		}
	}

	public List<TravelScheduleDTO> getAvailableSchedules(Station source, Station destination, LocalDate searchDate) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDate currentDate = currentDateTime.toLocalDate();
		LocalTime currentTime = currentDateTime.toLocalTime();

		LocalDateTime searchDateTime = LocalDateTime.of(searchDate, LocalTime.MIDNIGHT);
		if (searchDate.isBefore(currentDate)) {
			// cannot search for past schedules
			throw new IllegalArgumentException("Cannot search for schedules in the past");
		} else if (searchDate.equals(currentDate)) {
			// search for schedules at least 1 hour from now
			searchDateTime = LocalDateTime.of(searchDate, currentTime.plusHours(1));
		}

		LocalDateTime maxSearchDateTime = currentDateTime.plusDays(MAX_SEARCH_DAYS);
		if (searchDateTime.isAfter(maxSearchDateTime)) {
			// cannot search for schedules more than one month in the future
			throw new IllegalArgumentException("Cannot search for schedules more than one month in the future");
		}

		List<TravelSchedule> travelScheduleList = scheduleRepository
				.findBySourceAndDestinationAndEstimatedArrivalTimeAfter(source, destination, currentDateTime);
		List<TravelScheduleDTO> travelScheduleDTOList = new ArrayList<>();
		for (TravelSchedule travelSchedule : travelScheduleList) {
			TravelScheduleDTO travelScheduleDTO = new TravelScheduleDTO(travelSchedule);

			travelScheduleDTOList.add(travelScheduleDTO);
		}
		return travelScheduleDTOList;
	}

	public boolean createSchedule(TravelScheduleDTO travelScheduleDTO) throws ParseException {
		 logger.info("Creating travel schedule: {}", travelScheduleDTO);


		TravelSchedule travelschedule = new TravelSchedule();

		

		StationDTO destinationDTO = travelScheduleDTO.getDestination();
		Station destination = getStationByCode(destinationDTO.getStationCode());
		travelschedule.setDestination(destination);

		Station source = getStationByCode(destinationDTO.getStationCode());
		travelschedule.setDestination(source);
		travelschedule = scheduleRepository.save(travelschedule);
		 // add log statement
        logger.info("Created travel schedule ");
		return travelschedule.getScheduleId() != null;
	}

}


//import java.text.ParseException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeParseException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.wittybrains.busbookingsystem.dto.StationDTO;
//import com.wittybrains.busbookingsystem.dto.TravelScheduleDTO;
//import com.wittybrains.busbookingsystem.exception.InvalidSourceOrDestinationException;
//import com.wittybrains.busbookingsystem.exception.StationNotFoundException;
//import com.wittybrains.busbookingsystem.model.Bus;
//import com.wittybrains.busbookingsystem.model.Station;
//import com.wittybrains.busbookingsystem.model.TravelSchedule;
//import com.wittybrains.busbookingsystem.repository.BusRepository;
//import com.wittybrains.busbookingsystem.repository.StationRepository;
//import com.wittybrains.busbookingsystem.repository.TravelScheduleRepository;
//
//@Service
//public class TravelScheduleService {
//    private static final Logger logger = LoggerFactory.getLogger(TravelScheduleService.class);
//    private static final int MAX_SEARCH_DAYS = 30;
//
//    @Autowired
//    private TravelScheduleRepository scheduleRepository;
//
//    @Autowired
//    private StationRepository stationRepository;
//
//    public Station getStationByCode(String code) {
//        if (StringUtils.isBlank(code)) {
//            throw new IllegalArgumentException("Code must not be null or empty");
//        }
//
//        Optional<Station> optionalStation = stationRepository.findByStationCode(code);
//        if (optionalStation.isPresent()) {
//            return optionalStation.get();
//        } else {
//            throw new StationNotFoundException("Station with code " + code + " not found");
//        }
//    }
//
//    public List<TravelScheduleDTO> getAvailableSchedules(Station source, Station destination, LocalDate searchDate) {
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        LocalDate currentDate = currentDateTime.toLocalDate();
//        LocalTime currentTime = currentDateTime.toLocalTime();
//
//        LocalDateTime searchDateTime = LocalDateTime.of(searchDate, LocalTime.MIDNIGHT);
//        if (searchDate.isBefore(currentDate)) {
//            // cannot search for past schedules
//            throw new IllegalArgumentException("Cannot search for schedules in the past");
//        } else if (searchDate.equals(currentDate)) {
//            // search for schedules at least 1 hour from now
//            searchDateTime = LocalDateTime.of(searchDate, currentTime.plusHours(1));
//        }
//
//        LocalDateTime maxSearchDateTime = currentDateTime.plusDays(MAX_SEARCH_DAYS);
//        if (searchDateTime.isAfter(maxSearchDateTime)) {
//            // cannot search for schedules more than one month in the future
//            throw new IllegalArgumentException("Cannot search for schedules more than one month in the future");
//        }
//
//        List<TravelSchedule> travelScheduleList = scheduleRepository
//                .findBySourceAndDestinationAndEstimatedArrivalTimeAfter(source, destination, currentDateTime);
//        List<TravelScheduleDTO> travelScheduleDTOList = new ArrayList<>();
//        for (TravelSchedule travelSchedule : travelScheduleList) {
//            TravelScheduleDTO travelScheduleDTO = new TravelScheduleDTO(travelSchedule);
//
//            travelScheduleDTOList.add(travelScheduleDTO);
//        }
//        return travelScheduleDTOList;
//    }
//
//
//    public boolean createSchedule(TravelScheduleDTO travelScheduleDTO) throws ParseException {
//        logger.info("Creating travel schedule: {}", travelScheduleDTO);
//
//        TravelSchedule travelSchedule = new TravelSchedule();
//
//        StationDTO sourceDTO = travelScheduleDTO.getSource();
//        Station source = getStationByCode(sourceDTO.getStationCode());
//        travelSchedule.setSource(source);
//
//        StationDTO destinationDTO = travelScheduleDTO.getDestination();
//        Station destination = getStationByCode(destinationDTO.getStationCode());
//        travelSchedule.setDestination(destination);
//        
//        try {
//            travelSchedule.setEstimatedArrivalTime(LocalDateTime.parse(travelScheduleDTO.getEstimatedArrivalTime()));
//        } catch (DateTimeParseException e) {
//            throw new ParseException("Invalid estimated arrival time format", 0);
//        }
//
//        try {
//            travelSchedule.setEstimatedDepartureTime(LocalDateTime.parse(travelScheduleDTO.getEstimatedDepartureTime()));
//        } catch (DateTimeParseException e) {
//            throw new ParseException("Invalid departure time format", 0);
//        }
//
//     
//
//        TravelSchedule savedSchedule = scheduleRepository.save(travelSchedule);
//        logger.info("Created travel schedule with id {}", savedSchedule.getScheduleId());
//
//        return savedSchedule.getScheduleId() != null;
//    }
//    }
        
