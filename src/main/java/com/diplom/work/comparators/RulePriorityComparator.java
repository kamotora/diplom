package com.diplom.work.comparators;

import com.diplom.work.core.Rule;

import java.util.Comparator;

public class RulePriorityComparator implements Comparator<Rule> {

    @Override
    public int compare(Rule newRule, Rule notNewRule) {
        return newRule.getPriority().compareTo(notNewRule.getPriority());
    }
}
