/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.akvo.search.common.Factory;

/**
 * @author trent
 */
public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.install();
        Factory.getFactory().getView();
    }
}
