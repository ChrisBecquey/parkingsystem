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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketDAOTest {
    @Mock
    private DataBaseConfig dataBaseConfig;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
  /*  @Mock
    private static TicketDAO ticketDAOOccurence;*/
    @Spy
    @InjectMocks
    private static TicketDAO ticketDAO;

    private Ticket ticket;

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }


    @Test
    void saveTicket() throws SQLException, ClassNotFoundException, IOException {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(12, ParkingType.CAR, true);

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("EQ650DQ");
        ticket.setInTime(inTime);
        ticket.setRecuringMember(false);
        ticket.setOutTime(outTime);

        ticketDAO.saveTicket(ticket);
    }

    @Test
    void getTicket() throws SQLException, ClassNotFoundException, IOException {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatement);
        doReturn(0).when(ticketDAO).getTicketNumberOccurrence(anyString());

        ResultSet rs = resultSet;
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(anyInt())).thenReturn(1).thenReturn(1);
        when(rs.getDouble(3)).thenReturn(10.50);
        when(rs.getTimestamp(anyInt())).thenReturn(Timestamp.valueOf("2022-11-20 10:00:00.0")).thenReturn(Timestamp.valueOf("2022-11-20 12:00:00.0"));
        when(rs.getString(6)).thenReturn("CAR");
        when(preparedStatement.executeQuery()).thenReturn(rs);

        ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals( 10.50, ticket.getPrice());
    }

    @Test
    void updateTicket() throws SQLException, IOException, ClassNotFoundException {
        Date outTime = new Date();

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatement);

        ticket.setVehicleRegNumber("EQ650DQ");
        ticket.setPrice(15);
        ticket.setOutTime(outTime);

        assertTrue(ticketDAO.updateTicket(ticket));
        assertEquals(15, ticket.getPrice());

    }
}