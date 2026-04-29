import java.awt.*;
import java.util.function.IntConsumer;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.function.IntPredicate;

public class ButtonColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor {

    private final JButton renderButton = new JButton();
    private final JButton editButton = new JButton();
    private String label;
    private int currentRow;
    private final IntPredicate isEnabled; // decides if button is active per row

    public ButtonColumn(JTable table, IntConsumer action, IntPredicate isEnabled, int column) {
        this.isEnabled = isEnabled;

        editButton.addActionListener(e -> {
            action.accept(currentRow);
            fireEditingStopped();
        });

        table.getColumnModel().getColumn(column).setCellRenderer(this);
        table.getColumnModel().getColumn(column).setCellEditor(this);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        renderButton.setText(value == null ? "" : value.toString());
        renderButton.setEnabled(isEnabled.test(row));
        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        label = value == null ? "" : value.toString();
        editButton.setText(label);
        editButton.setEnabled(isEnabled.test(row));
        currentRow = row;
        return editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}