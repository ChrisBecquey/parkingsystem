package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();  // renvoie uniquement l'heure !
        long outHour = ticket.getOutTime().getTime();

        // calcul de la durée en heure
        float duration = ((float) (outHour - inHour) / (1000 * 60 * 60));

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (duration > 0.50 && ticket.isRecuringMember()) {
                    ticket.setPrice(0.95 * (duration * Fare.CAR_RATE_PER_HOUR));
                    // cas ou la durée est supérieur à 30 min et client récurrent
                } else if (duration > 0.50 && !ticket.isRecuringMember()) {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    //cas où la durée est supérieur à 30 min et nouveau client
                } else {
                    ticket.setPrice(0);
                    //cas où la durée est inférieur à 30 min
                }
                break;
            }
            case BIKE: {
                if (duration > 0.50 && ticket.isRecuringMember()) {
                    ticket.setPrice(0.95 * (duration * Fare.BIKE_RATE_PER_HOUR));
                } else if (duration > 0.50 && !ticket.isRecuringMember()) {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                } else {
                    ticket.setPrice(0);
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}