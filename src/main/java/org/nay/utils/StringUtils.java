package org.nay.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

import static org.nay.ui.view.model.KeyValueTableModel.getString;

public class StringUtils {

    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    public static String formatValue(Object value) {
        return getString(value);
    }

    public static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        JOptionPane.showMessageDialog(null, "COPIED");
    }

}
