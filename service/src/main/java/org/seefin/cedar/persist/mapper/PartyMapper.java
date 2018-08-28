package org.seefin.cedar.persist.mapper;

import org.seefin.cedar.model.parties.Individual;

import java.util.Map;

/**
 * defines the interface for persistence operations, that is to be
 * implemented by a mapper implementation
 *
 * @author phillipsr
 */
public interface PartyMapper {
    /**
     * Find the party with the specified identity
     *
     * @param partyId (synthetic key) value
     * @return a map of party properties for the requested party,
     * of null if party not found
     */
    Map<String, Object> find(String partyId);

    /**
     * Insert the supplied party (individual) into the database
     *
     * @param party
     */
    void insert(Individual party);

    /**
     * Find the party specified by their username
     *
     * @param username of the party to return
     * @return a map of party properties for the requested party,
     * of null if party not found
     */
    Map<String, Object> findByUsername(String username);
}