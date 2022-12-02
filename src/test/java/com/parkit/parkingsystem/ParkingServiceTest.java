package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
    @InjectMocks
    private  ParkingService parkingService;

    @Mock
    private  InputReaderUtil inputReaderUtil;
    @Mock
    private  ParkingSpotDAO parkingSpotDAO;
    @Mock
    private  TicketDAO ticketDAO;


    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() {
        setUpPerTest();
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Nested
    class processIncomingVehicleTest {
        @Test
        public void processIncomingVehicleTest() throws Exception {
            when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
            when(inputReaderUtil.readSelection()).thenReturn(1);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            parkingService.processIncomingVehicle();

            verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        }

        @Test
        void shouldNotProcessIncomingVehicle_whenParkingSpotIdIsZero() throws Exception{
            when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(0);
            when(inputReaderUtil.readSelection()).thenReturn(1);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

            parkingService.processIncomingVehicle();

            verify(inputReaderUtil, Mockito.times(0)).readVehicleRegistrationNumber();
            verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
        }

    }



    @Test
    public void getNextParkingNumberIfAvailableForCarTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot expectedParkingSpotCar = new ParkingSpot(2, ParkingType.CAR, true);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(2);

        ParkingSpot actualParkingSpotCar = parkingService.getNextParkingNumberIfAvailable();

        verify(inputReaderUtil).readSelection();
        verify(parkingSpotDAO).getNextAvailableSlot(any());
        assertEquals(expectedParkingSpotCar, actualParkingSpotCar);
    }
    @Test
    public void getNextParkingNumberIfAvailableForBikeTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot expectedParkingSpotBike = new ParkingSpot(2, ParkingType.BIKE, true);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(2);

        ParkingSpot actualParkingSpotBike = parkingService.getNextParkingNumberIfAvailable();

        verify(inputReaderUtil).readSelection();
        verify(parkingSpotDAO).getNextAvailableSlot(any());
        assertEquals(expectedParkingSpotBike, actualParkingSpotBike);
    }

}
