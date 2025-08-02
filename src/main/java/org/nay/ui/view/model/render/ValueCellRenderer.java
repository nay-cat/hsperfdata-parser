package org.nay.ui.view.model.render;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ValueCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean selected, boolean focus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, selected, focus, row, column);

        if (!selected) {
            String strValue = value.toString();
            if (strValue.matches("\\d+.*")) {
                setBackground(new Color(230, 255, 210));
            } else if (strValue.startsWith("true") || strValue.startsWith("false")) {
                setBackground(new Color(255, 235, 230));
            } else {
                setBackground(Color.WHITE);
            }
        }

        return c;
    }
}