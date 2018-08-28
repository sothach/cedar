package org.seefin.cedar.model.parties;

import org.seefin.cedar.model.PartyId;

/**
 * Generic concept of a party: organization or individual
 *
 * @author phillipsr
 */
public interface Party {
    /**
     * @return the unique id of the party: a synthetic key
     */
    PartyId getId();
}
