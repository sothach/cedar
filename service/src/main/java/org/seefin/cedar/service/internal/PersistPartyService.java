package org.seefin.cedar.service.internal;

import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.persist.mapper.PartyMapper;
import org.seefin.cedar.service.PartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * A service implementation, exposing services for working with Parties,
 * backed by a persistence service
 *
 * @author phillipsr
 */
@Service("partyService")
@Transactional
public class PersistPartyService
        implements PartyService {
    private static final String ID_COLUMN = "ID";
    private static final String LOCALE_COLUMN = "LOCALE";
    private static final String PASSWORD_COLUMN = "PASSWORD";
    private static final String USERNAME_COLUMN = "USERNAME";

    private static final Logger log = LoggerFactory.getLogger(PersistPartyService.class);

    @Resource
    private PartyMapper partyMapper; // database access via mapper interface

    /**
     * default constructor, used by Spring when creating a service bean
     */
    public PersistPartyService() {
    }

    /**
     * Constructor useful for testing purposes
     */
    public PersistPartyService(PartyMapper partyMapper) {
        this.partyMapper = partyMapper;
    }

    /**
     * Retrieves party from persistence storage<p/>
     * {@inheritDoc}
     */
    @Override
    public Optional<Individual> findPartyById(PartyId id) {
        log.debug("find(partyId={})", id);
        Map<String, Object> values = null;
        try {
            values = partyMapper.find(id.toString());
        } catch (RuntimeException e) {
            log.warn("Error accessing database", e);
            return Optional.empty();
        }
        if (values == null) {
            log.debug("query returned no results");
            return Optional.empty();
        }
        Individual result = new Individual(id,
                (String) values.get(USERNAME_COLUMN),
                (String) values.get(PASSWORD_COLUMN),
                new Locale((String) values.get(LOCALE_COLUMN)));
        return Optional.ofNullable(result);
    }

    /**
     * Retrieves party from persistence storage<p/>
     * {@inheritDoc}
     */
    @Override
    public Optional<Individual> findPartyByUsername(String username) {
        log.debug("find(username={})", username);
        Map<String, Object> values = null;
        try {
            values = partyMapper.findByUsername(username);
        } catch (RuntimeException e) {
            log.warn("Error accessing database", e);
            return Optional.empty();
        }
        if (values == null) {
            log.debug("query returned no results");
            return Optional.empty();
        }
        log.debug("query returned {}", values);
        final String userLocale = (String) values.get(LOCALE_COLUMN);
        final Individual result = new Individual(
                new PartyId((String) values.get(ID_COLUMN)),
                username,
                (String) values.get(PASSWORD_COLUMN),
                userLocale != null ? new Locale(userLocale) : Locale.getDefault());
        return Optional.of(result);
    }

    /**
     * Saves party to persistence storage<p/>
     * {@inheritDoc}
     */
    @Override
    public void saveParty(Individual party) {
        try {
            partyMapper.insert(party);
        } catch (RuntimeException e) {
            log.warn("Error inserting into database", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean
    isPasswordValid(String username, String password) {
        Optional<Individual> party = findPartyByUsername(username);
        if (party.isPresent() == false) {
            return false;
        }
        Individual user = party.get();
        return user.passwordMatches(password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Individual> logon(String username, String password) {
        Optional<Individual> party = findPartyByUsername(username);
        if (party.isPresent() == false) {
            log.debug("failed to logon user={}", username);
            return party; // this is a Failure
        }
        Individual user = party.get();
        if (user.passwordMatches(password) == false) {
            log.debug("incorrect password for logon user={}", username);
            return Optional.empty();
        }
        return party;
    }

}
