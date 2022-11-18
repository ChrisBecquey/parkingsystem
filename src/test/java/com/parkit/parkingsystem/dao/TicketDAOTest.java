package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketDAOTest {
    @Mock
    private DataBaseConfig dataBaseConfig;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @InjectMocks
    private static TicketDAO ticketDAO;

    private Ticket ticket;

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }


    @Test
    void saveTicket() throws SQLException, ClassNotFoundException {
//        Date inTime = new Date();
//        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
//        Date outTime = new Date();
//        ParkingSpot parkingSpot = new ParkingSpot(12, ParkingType.CAR, true);
//
//        when(dataBaseConfig.getConnection()).thenReturn(connection);
//        when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);
//        ticket.setParkingSpot(parkingSpot);
//        ticket.setVehicleRegNumber("EQ650DQ");
//        ticket.setInTime(inTime);
//        ticket.setRecuringMember(false);
//        ticket.setOutTime(outTime);
//
//        boolean result = ticketDAO.saveTicket(ticket);
//        assertFalse(ticketDAO.saveTicket(ticket));
//
//        Ticket test = ticketDAO.getTicket("EQ650DQ");
//        assertEquals(ParkingType.CAR, test.getParkingSpot().getParkingType());

    }

    @Test
    void getTicket() {
    }

    @Test
    void updateTicket() {
    }
}