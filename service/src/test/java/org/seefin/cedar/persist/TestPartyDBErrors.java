package org.seefin.cedar.persist;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.persist.mapper.PartyMapper;
import org.seefin.cedar.service.PartyService;
import org.seefin.cedar.service.internal.PersistPartyService;

import java.util.Optional;

/**
 * Test the failure path in the Party service logic, ensuring database connection
 * errors are correctly handled and not propagated out of the service interface
 *
 * @author phillipsr
 */
public class TestPartyDBErrors {
    private static final PartyId PARTY_ID = new PartyId("5ab2b0f3-c5d5-4bdb-8c05-207ba9291e98");

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    @Mock
    private PartyMapper partyMapper;
    private PartyService partyService;

    @Before
    public void
    createServices() {
        partyService = new PersistPartyService(partyMapper);
    }

    @Test
    public void
    testFindPartyByIdFailure() {
        context.checking(new Expectations() {{
            oneOf(partyMapper).find(PARTY_ID.toString());
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        Optional<Individual> response = partyService.findPartyById(PARTY_ID);
        Assert.assertFalse(response.isPresent());

        context.assertIsSatisfied();
    }

    @Test
    public void
    testFindPartyByUserNameFailure() {
        context.checking(new Expectations() {{
            oneOf(partyMapper).findByUsername("test");
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        Optional<Individual> response = partyService.findPartyByUsername("test");
        Assert.assertFalse(response.isPresent());

        context.assertIsSatisfied();
    }

    @Test
    public void
    testSavePartyFailure() {
        context.checking(new Expectations() {{
            oneOf(partyMapper).insert(with(any(Individual.class)));
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        partyService.saveParty(new Individual("test", "password-hash"));

        context.assertIsSatisfied();
    }

    @Test
    public void
    testIsPasswordValidFailure() {
        context.checking(new Expectations() {{
            oneOf(partyMapper).findByUsername("test");
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        partyService.isPasswordValid("test", "password");

        context.assertIsSatisfied();
    }

    @Test
    public void
    testLogonFailure() {
        context.checking(new Expectations() {{
            oneOf(partyMapper).findByUsername("test");
            will(throwException(new RuntimeException("DB connection failed")));
        }});

        partyService.logon("test", "password");

        context.assertIsSatisfied();
    }

}
