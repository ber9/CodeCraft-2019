package com.huawei.util;

import java.util.Comparator;

public class StringNumComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        return Integer.parseInt((String) o1) - Integer.parseInt((String) o2);
    }
}
