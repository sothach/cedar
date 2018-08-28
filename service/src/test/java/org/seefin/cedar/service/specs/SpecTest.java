package org.seefin.cedar.service.specs;

import cucumber.api.junit.Cucumber;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * @author phillipsr
 */
@Ignore
@RunWith(Cucumber.class)
@Cucumber.Options(format = {"pretty", "html:target/cucumber"})
public class SpecTest {
    // tests specified by cucumber feature
}
