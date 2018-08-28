package org.seefin.cedar.model.parties;

import org.apache.commons.codec.digest.DigestUtils;
import org.seefin.cedar.model.PartyId;

import java.util.Locale;

/**
 * Individual represents the individual who has created a task, and initially, this
 * is also the assignee (person responsible) for the task
 *
 * @author phillipsr
 */
public final class Individual
        implements Party {
    private final PartyId id;
    private final String name;
    private final String password;
    private final String preferredLocale;

    /**
     * Instantiate a new individual from the supplied values
     *
     * @param name     of the individual
     * @param password (hash) for the individual
     */
    public Individual(String name, String password) {
        this(new PartyId(), name, password, Locale.getDefault());
    }

    /**
     * (re-)instantiate an existing individual from the supplied values
     *
     * @param id       existing unique identity for the individual
     * @param name     of the individual
     * @param password (hash) for the individual
     * @param locale   user's preferred Locale
     */
    public Individual(PartyId id, String name, String password, Locale locale) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null for existing individual");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.password = password;
        this.preferredLocale = locale == null ? null : locale.toString();
    }

    @Override
    /** {@inheritDoc} */
    public PartyId getId() {
        return id;
    }

    /**
     * @return the name of this individual
     */
    public String getName() {
        return name;
    }

    /**
     * @return the password of this individual (hashed)
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the preferred locale of this individual
     */
    public String getLocale() {
        return preferredLocale;
    }


    /**
     * Provides useful info for logging purposes<p/>
     * {@inheritDoc}
     */
    @Override
    public String
    toString() {
        return "Individual(id=" + id + ",name=" + name
                + ",password=" + (password == null ? "<absent>" : "<present>") + ")";
    }

    /**
     * @param testPassword in clear text
     * @return true if the password supplied is the same as user's
     */
    public boolean
    passwordMatches(String testPassword) {
        return DigestUtils.shaHex("{" + testPassword + "_").equals(this.password);
    }
}
