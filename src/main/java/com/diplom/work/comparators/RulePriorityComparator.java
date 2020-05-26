package com.diplom.work.comparators;

import com.diplom.work.core.Rule;

import java.util.Comparator;

public class RulePriorityComparator implements Comparator<Rule> {

    @Override
    public int compare(Rule newRule, Rule notNewRule) {
        // Правила без указанного приоритета считаются как самые "не главные"
        if(newRule.getPriority() == null && notNewRule.getPriority() == null)
            return 0;
        if(newRule.getPriority() == null)
            return 1;
        if(notNewRule.getPriority() == null)
            return -1;
        return newRule.getPriority().compareTo(notNewRule.getPriority());
    }
}
