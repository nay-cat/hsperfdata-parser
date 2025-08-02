package org.nay.ui.view.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.nay.utils.StringUtils.formatBytes;

public class KeyValueTableModel extends AbstractTableModel {
    private final List<Map.Entry<String, Object>> entries;
    private final String[] columnNames = {"Key", "Value"};

    public KeyValueTableModel(List<Map.Entry<String, Object>> entries) {
        this.entries = entries;
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Map.Entry<String, Object> entry = entries.get(rowIndex);
        if (columnIndex == 0) {
            return entry.getKey();
        } else {
            return formatValue(entry.getValue());
        }
    }

    private String formatValue(Object value) {
        return getString(value);
    }

    public static String getString(Object value) {
        if (value == null) {
            return "null";
        }

        String str = value.toString();
        if (str.matches("\\d+")) {
            try {
                long num = Long.parseLong(str);
                if (num > 1000000) {
                    return String.format("%,d (%s)", num, formatBytes(num));
                } else if (num > 1000) {
                    return String.format("%,d", num);
                }
            } catch (NumberFormatException ignored) {}
        }

        return str;
    }


}