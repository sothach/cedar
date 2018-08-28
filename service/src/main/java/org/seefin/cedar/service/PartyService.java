package org.seefin.cedar.service;

import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.parties.Individual;

import java.util.Optional;

/**
 * Definition of the service interface for working with parties;
 * note these services are a thin layer on top of the persistence
 * mapping, with little business logic - as and when stronger business
 * logic requirements are identified, these classes should be split
 * from the persistence implementation
 *
 * @author phillipsr
 */
public interface PartyService {
    /**
     * @param id of requested party
     * @return the party in the database with the id specified, as
     * either a Success or Failure object
     */
    Optional<Individual> findPartyById(PartyId id);

    /**
     * @param username of requested party
     * @return the party in the database with the id specified, as
     * either a Success or Failure object
     */
    Optional<Individual> findPartyByUsername(String username);

    /**
     * Save the supplied party (individual) to the persistence store
     *
     * @param party object to be saved
     */
    void saveParty(Individual party);

    /**
     * @param username to validate
     * @param password to validate
     * @return true if the specified user exists, and the password matches
     */
    boolean isPasswordValid(String username, String password);

    /**
     * Validate and return an Individual user ('logon')
     *
     * @param username to validate
     * @param password to validate
     * @return a Success object wrapping the user specified, username found and
     * the password matches, else a Failure
     */
    Optional<Individual> logon(String username, String password);
}
