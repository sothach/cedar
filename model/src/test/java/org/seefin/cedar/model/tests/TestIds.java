package org.seefin.cedar.model.tests;

import org.junit.Assert;
import org.junit.Test;
import org.seefin.cedar.model.GUID;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.TaskId;

import java.util.UUID;

/**
 * @author phillipsr
 */
public class TestIds {
    private static final String UUIDString = "5ab2b0f3-c5d5-4bdb-8c05-207ba9291e98";

    @Test
    public void
    testCreateTaskIdFromString() {
        TaskId id = new TaskId(UUIDString);
        Assert.assertEquals(UUIDString, id.toString());
    }

    @Test
    public void
    testCreatePartyIdFromString() {
        PartyId id = new PartyId(UUIDString);
        Assert.assertEquals(UUIDString, id.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    testCreatePartyIdFailIfStringEmpty() {
        try {
            new PartyId("");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("GUID string cannot be null or empty", e.getMessage());
            throw e;
        }
    }

    @Test
    public void
    testGUID() {
        GUID id1 = GUID.parse(UUIDString);
        GUID id2 = GUID.createUniqueId();
        Assert.assertNotEquals(id1, id2);
        Assert.assertNotEquals(0, id1.compareTo(id2));
    }

    @Test
    public void
    testGUIDEquality() {
        GUID id1 = GUID.parse(UUIDString);
        UUID id2 = UUID.fromString(UUIDString);
        Assert.assertEquals(id1.hashCode(), id2.hashCode());
        Assert.assertEquals(id1.getExternalForm(), id2.toString());
        Assert.assertFalse(id1.equals(id2));
    }
}
