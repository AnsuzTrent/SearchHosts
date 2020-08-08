package org.apache.fraud.search;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.apache.fraud.search.common.UserInterface;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.install();
        EventQueue.invokeLater(UserInterface::new);
    }
}
