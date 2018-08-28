package org.seefin.cedar.persist;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.service.PartyService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/seefin/cedar/persist/PersistTest-context.xml"})
@DirtiesContext
public class PartyPersistTest {
    private static final PartyId PARTY_ID = new PartyId("f70f76d0-072f-11e3-8ffd-0800200c9a66"); // ID from pre-populated test data
    private static final PartyId UNKNOWN_PARTY_ID = new PartyId("aa9a9aa9-072e-11e3-8ffd-0800200c9a66");

    @Resource
    private PartyService partyService;

    @Test
    public void
    testFind() {
        Optional<Individual> response = partyService.findPartyById(PARTY_ID);
        Assert.assertTrue("Party returned", response.isPresent());
    }

    @Test
    public void
    testValidatePassword() {
        Assert.assertTrue(partyService.isPasswordValid("test", "abc123"));
    }

    @Test
    public void
    testValidatePassword2() {
        Assert.assertTrue(partyService.isPasswordValid("roy", "pa55sw0rd"));
    }

    @Test
    public void
    testPartyIdNotFound() {
        Optional<Individual> response = partyService.findPartyById(UNKNOWN_PARTY_ID);
        System.out.println("response=" + response);
        Assert.assertFalse("No Party returned", response.isPresent());
    }

    @Test
    public void
    testUsernameNotFound() {
        Optional<Individual> response = partyService.findPartyByUsername("joe");
        System.out.println("response=" + response);
        Assert.assertFalse("No Party returned", response.isPresent());
    }

    @Test
    public void
    testNewPartyPersisted() {
        Individual party = new Individual("Mr. B. New", "bogus-password-hash");
        partyService.saveParty(party);

        Optional<Individual> response = partyService.findPartyById(party.getId());
        Assert.assertTrue("Party saved", response.isPresent());
        Individual result = response.get();
        Assert.assertEquals(party.getId(), result.getId());
        Assert.assertEquals(party.getName(), result.getName());
        Assert.assertEquals(party.getPassword(), result.getPassword());
        System.out.println("party=" + response.get());
    }

    @Test
    public void
    logonSuccessfullTest() {
        Optional<Individual> response = partyService.logon("test", "abc123");
        Assert.assertTrue(response.isPresent());
    }

    @Test
    public void
    logonBadUserTest() {
        Optional<Individual> response = partyService.logon("testy", "123abc");
        Assert.assertFalse(response.isPresent());
    }

    @Test
    public void
    logonBadPasswordTest() {
        Optional<Individual> response = partyService.logon("test", "123abc");
        Assert.assertFalse(response.isPresent());
    }

}
