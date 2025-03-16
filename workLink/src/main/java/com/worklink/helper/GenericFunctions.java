package com.worklink.helper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class GenericFunctions {
    public <R> boolean findDuplicatesOfArrayLists(Collection<? extends R> input1) {
        Set<R> input2 = new HashSet<>(input1);
//        System.out.println(input1);
//        System.out.println(input2);
        return input1.size() == input2.size();
    }
}