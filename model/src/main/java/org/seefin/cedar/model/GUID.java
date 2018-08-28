package org.seefin.cedar.model;

import java.util.UUID;

/**
 * A globally-unique identity, suitable for use as a primary key, transactions id, etc.,
 * that can be stored in a CHAR(32) field, for example
 * <p/>
 * Note: this implementation relies upon the Java UUID class to generate the ID value,
 * which uses type 4 generation (fully random); it may be better to change this to
 * use type 1 (MAC address based) generation: this would require an external implementation
 *
 * @author phillipsr
 */
public class GUID
        implements Comparable<GUID> {
    private final UUID identity;

    /**
     * Instantiate an new GUID from a type 4 (pseudo randomly generated) UUID;
     * The UUID is generated using a cryptographically strong pseudo random
     * number generator<p/>
     * see {@link java.util.UUID}
     */
    protected GUID() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Instantiate a GUID from its external (string) form
     *
     * @param externalForm canonical UUID string
     */
    protected GUID(String externalForm) {
        if (externalForm == null || externalForm.isEmpty()) {
            throw new IllegalArgumentException("GUID string cannot be null or empty");
        }
        identity = UUID.fromString(externalForm);
    }

    /**
     * {@inheritDoc}
     *
     * @return the actual String value of this identity, as there is no requirement
     * to return an obfusticated identity value
     */
    @Override
    public String
    toString() {
        return identity.toString();
    }

    /**
     * @return a string representing this GUID in canonical (UUID) format
     */
    public String getExternalForm() {
        return toString();
    }

    /**
     * Static factory to instantiate a GUID from its external form
     *
     * @param externalForm
     * @return GUID where externmalForm.equals(result.getExternalForm())
     */
    public static GUID
    parse(String externalForm) {
        return new GUID(externalForm);
    }

    /**
     * @return a new UniqueIdentity instance
     */
    public static GUID
    createUniqueId() {
        return new GUID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean
    equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        return identity.equals(((GUID) other).identity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int
    hashCode() {
        return identity.hashCode();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int
    compareTo(GUID otherId) {
        return identity.compareTo(otherId.identity);
    }

}
