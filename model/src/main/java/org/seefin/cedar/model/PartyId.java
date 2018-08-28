package org.seefin.cedar.model;

/**
 * Type representing the unique identity of an individual person or organization,
 * used as a synthetic (globally-unique) id
 *
 * @author phillipsr
 */
public final class PartyId extends GUID {
    // this is effectively a type alias for GUID

    /**
     * Instantiate a new PartyId from a random value<b/>
     * see {@link org.seefin.cedar.model.GUID}
     */
    public PartyId() {
        super();
    }

    /**
     * Instantiate a PartyId from the supplied external format value<b/>
     * see {@link org.seefin.cedar.model.GUID}
     *
     * @param stringForm representing the PartyId
     */
    public PartyId(String stringForm) {
        super(stringForm);
    }
}
