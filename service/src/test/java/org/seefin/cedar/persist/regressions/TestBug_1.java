package org.seefin.cedar.persist.regressions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.service.TaskService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Optional;

/**
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/seefin/cedar/persist/PersistTest-context.xml"})
@DirtiesContext
public class TestBug_1 {
    private static final String LONG_DESCRIPTION_2500 =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent pharetra augue ac iaculis blandit. " +
                    "Suspendisse euismod augue at mi cursus, eget viverra urna ultrices. Suspendisse ac nibh laoreet, hendrerit leo " +
                    "sit amet, auctor velit. Donec vehicula tincidunt tincidunt. Nulla non diam id massa iaculis ornare nec ut sapien. " +
                    "Suspendisse rutrum neque in justo convallis, sit amet dapibus augue congue. Nullam dapibus, arcu vel hendrerit congue, " +
                    "turpis mauris porttitor ipsum, id venenatis nunc leo ac tortor. Etiam sed justo quis mauris condimentum pulvinar. " +
                    "Praesent aliquam auctor accumsan. Mauris ac tincidunt nibh. Etiam sit amet nisl et odio tincidunt commodo. Vestibulum " +
                    "sodales lectus a lacus feugiat rhoncus. Fusce vehicula velit sit amet aliquam tincidunt. Mauris ultrices a nibh " +
                    "pellentesque pulvinar. Donec rutrum, felis iaculis lacinia condimentum, felis enim accumsan orci, id facilisis tellus " +
                    "augue ut tortor. In hac habitasse platea dictumst. Curabitur quis commodo orci, ac eleifend tortor. Nullam faucibus, " +
                    "est a varius malesuada, nibh orci posuere mi, at suscipit neque felis vel erat. Aliquam erat volutpat. Nulla pulvinar " +
                    "nisi id nibh sodales, ut ultricies tortor pellentesque. Pellentesque vel nunc non justo cursus laoreet. Quisque vestibulum " +
                    "turpis non quam tristique, sed hendrerit libero pellentesque. Ut sit amet tristique urna, id adipiscing neque." +
                    "Nunc faucibus neque aliquet, viverra ipsum ut, condimentum sapien. Maecenas vulputate laoreet leo vel placerat. " +
                    "Duis sodales nunc eget arcu egestas rhoncus. Nullam vitae nibh ut risus dictum mattis. In hac habitasse platea dictumst. " +
                    "Maecenas in bibendum sem. In hac habitasse platea dictumst. Ut et est sapien. Sed commodo sodales diam vitae mollis. " +
                    "Ut hendrerit tristique vestibulum. Morbi at eleifend felis, eu ultricies ante. Nulla feugiat volutpat nunc, ut porta mauris " +
                    "luctus ut. In fermentum ligula non dapibus dignissim. Integer sit amet viverra lorem, in commodo mi. Aenean pretium urna in " +
                    "aliquet rutrum. Nunc et dolor quis orci faucibus molestie posuere vitae ipsum. Suspendisse in velit non velit mollis tristique " +
                    "nec sed leo. Phasellus pulvinar bibendum justo eget dapibus. Phasellus vestibulum placerat sollicitudin. Vestibulum sed rutrum odio. " +
                    "In dapibus dignissim sagittis. Nam aliquam gravida ipsum eu posuere. Nunc vitae pulvinar ante. Donec in congue leo." +
                    "Nullam pellentesque neque at risus sollicitudin scelerisque. Fusce non odio at ipsum lobortis ornare et in libero." +
                    "In a porta justo, sollicitudin nullam.";

    private static final Individual OWNER
            = new Individual(new PartyId("f70f76d0-072f-11e3-8ffd-0800200c9a66"), "test", "bogus-password-hash", Locale.getDefault());

    @Resource
    private TaskService taskService;

    @Test
    public void
    testDescFieldSizeLarge() {
        Task task = new Task(OWNER, LONG_DESCRIPTION_2500);

        taskService.saveTask(task);
        Optional<Task> rereadTask = taskService.findTaskById(task.getTaskId());
        Assert.assertTrue(rereadTask.isPresent());
        Assert.assertEquals(
                LONG_DESCRIPTION_2500.length(), rereadTask.get().getDescription().length());
    }

    @Test
    public void
    testDescFieldEmpty() {
        Task task = new Task(OWNER, "");

        taskService.saveTask(task);
        Optional<Task> rereadTask = taskService.findTaskById(task.getTaskId());
        Assert.assertTrue(rereadTask.isPresent());
        Assert.assertTrue(rereadTask.get().getDescription().isEmpty());
    }

}
