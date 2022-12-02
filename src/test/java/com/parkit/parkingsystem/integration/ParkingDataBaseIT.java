package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private static final Logger logger = LogManager.getLogger("ParkingDataBaseIT");
    private static final String VEHICULE_REG_NUMBER = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICULE_REG_NUMBER);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar() throws SQLException {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket carOne = ticketDAO.getTicket(VEHICULE_REG_NUMBER);

        assertNotNull(carOne);
        assertEquals(VEHICULE_REG_NUMBER, carOne.getVehicleRegNumber());
    }

    @Test
    public void testParkingLotExit() throws SQLException {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Ticket ticket = ticketDAO.getTicket(VEHICULE_REG_NUMBER);
        ticket.setInTime(inTime);

        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database


        Connection con = null;
        try {
            con = dataBaseTestConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET_BY_REG_NUMBER + VEHICULE_REG_NUMBER + "\"");
            try {
                ResultSet ticketResult = ps.executeQuery();
                boolean isPriceRight = false;

                if (ticketResult.next()) {
                    isPriceRight = ticketResult.getDouble(4) == ticket.getPrice();
                }

                assertTrue(isPriceRight);
            } finally {
                ps.close();
            }
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
            assertTrue(false);
        } finally {
            dataBaseTestConfig.closeConnection(con);

        }
    }

    @Test
    public void discountForRecurringUsers() throws Exception{
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle(); // first time in the parking
        parkingService.processExitingVehicle(); // exit for first time
        parkingService.processIncomingVehicle(); // coming back
        assertTrue(ticketDAO.getTicket(VEHICULE_REG_NUMBER).isRecuringMember());
    }
}
