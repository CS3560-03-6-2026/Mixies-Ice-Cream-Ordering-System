import java.awt.*;
import java.util.function.IntConsumer;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class ButtonColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor {

    private final JButton renderButton = new JButton();
    private final JButton editButton = new JButton();
    private String label;
    private int currentRow;

    public ButtonColumn(JTable table, IntConsumer action, int column) {
        editButton.addActionListener(e -> {
            action.accept(currentRow); // pass the row to the caller
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
        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        label = value == null ? "" : value.toString();
        editButton.setText(label);
        currentRow = row; // capture the row here, before the click fires
        return editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}