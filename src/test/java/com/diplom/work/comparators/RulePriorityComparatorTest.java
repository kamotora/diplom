package com.diplom.work.comparators;

import com.diplom.work.core.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RulePriorityComparatorTest {

    @Test
    public void compare() {
        ArrayList<Rule> rules = new ArrayList<>();
        Random random = new Random();
        for (int i = 300; i > 0; i--) {
            Rule rule = new Rule();
            if (random.nextBoolean())
                rule.setPriority(null);
            else
                rule.setPriority(random.nextInt(10000));
            rules.add(rule);
        }
        rules.sort(new RulePriorityComparator());
        for (int i = 1; i < rules.size(); i++) {
            final Integer prior1 = rules.get(i - 1).getPriority();
            final Integer prior2 = rules.get(i).getPriority();
            if (prior1 != null && prior2 != null)
                assertTrue(prior1 <= prior2);
            else if (prior1 == null)
                assertNull(prior2);
        }
    }
}