package org.seefin.cedar.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.parties.Individual;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * This test ensures that the production configuration is correct,
 * configuring the datasource as for production deployment, that is,
 * persisting to disk and loading the seed data
 *
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PartyServiceTest {
    // this is the Party Id that is inserted in the production seed data:
    private static final PartyId PARTY_ID = new PartyId("50395480-0805-11e3-8ffd-0800200c9a66"); // ID from pre-populated seed data

    @Resource
    private PartyService partyService;

    @Test
    public void
    testFindByUserId() {
        final Optional<Individual> response = partyService.findPartyById(PARTY_ID);
        Assert.assertTrue("Party found", response.isPresent());
    }

    @Test
    public void
    testFindByUsername() {
        final Optional<Individual> response = partyService.findPartyByUsername("test");
        Assert.assertTrue("Party with username 'test' found", response.isPresent());
    }

    @Test
    public void
    testValidateByUsername() {
        final Optional<Individual> response = partyService.findPartyByUsername("roy");
        Assert.assertTrue("Party with username 'roy' found", response.isPresent());
        Assert.assertTrue(partyService.isPasswordValid("roy", "pa55sw0rd"));
    }
}
