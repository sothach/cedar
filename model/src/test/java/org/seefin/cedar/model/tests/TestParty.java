package org.seefin.cedar.model.tests;

import org.junit.Assert;
import org.junit.Test;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.parties.Party;

import java.util.Locale;

/**
 * Unit tests for driving domain model development
 * <p>
 * Each �task� contains a text description and date when the entry was created.
 * Both fields should be displayed in the UI.
 *
 * @author phillipsr
 */
public class TestParty {
    private static final PartyId PARTY_ID = new PartyId("5ab2b0f3-c5d5-4bdb-8c05-207ba9291e98");

    @Test
    public void
    createANewIndividual() {
        Party party = new Individual("test", "password-hash");
        Assert.assertEquals(Individual.class, party.getClass());
        Individual person = (Individual) party;
        Assert.assertEquals("test", person.getName());
        Assert.assertEquals("password-hash", person.getPassword());
        Assert.assertTrue(person.toString().contains("name=test,password=<present>"));
    }

    @Test
    public void
    recreateIndividual() {
        Party party = new Individual(PARTY_ID, "joe", null, null);

        Assert.assertEquals(PARTY_ID, party.getId());
        Assert.assertTrue(party.toString().contains("name=joe,password=<absent>"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    invalidIdTest() {
        try {
            new Individual(null, null, null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Id cannot be null for existing individual", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void
    invalidNameTest() {
        try {
            new Individual(PARTY_ID, null, null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("name cannot be null or empty", e.getMessage());
            throw e;
        }
    }

    @Test
    public void
    testPasswordMatches() {
        Individual party = new Individual(PARTY_ID, "joe", "29335b135381e4200971dafed2d8302107a1d881", null);
        Assert.assertTrue(party.passwordMatches("abc123"));
    }

    @Test
    public void
    testPasswordDoesNotMatch() {
        Individual party = new Individual(PARTY_ID, "joe", "ccee5354192998cefbe881e250a439b4e33578b7", null);
        Assert.assertFalse(party.passwordMatches("abc123"));
    }

    @Test
    public void
    testUserLocale() {
        Individual party = new Individual("test", "password-hash");
        Assert.assertEquals(Locale.getDefault().toString(), party.getLocale());
    }

}
