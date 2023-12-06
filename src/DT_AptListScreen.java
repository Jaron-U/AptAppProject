import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DT_AptListScreen extends JFrame {
    private DefaultTableModel apts = new DefaultTableModel();
    private JTable tblApts = new JTable(apts);
    private List<Apartment> aptList = new ArrayList<>();

    public JTable getTblApts() {
        return tblApts;
    }

    public void addRow(Object[] row) {
        apts.addRow(row);
    }

    public void setAptList(List<Apartment> aptList) {
        apts.setRowCount(0);
        this.aptList.clear();
        this.aptList.addAll(aptList);
        for (Apartment apartment : aptList) {
            Object[] row = {
                    apartment.getId(),
                    apartment.getAptName(),
                    apartment.getType(),
                    apartment.getPrice(),
                    apartment.getAddress()
            };
            apts.addRow(row);
        }
    }

    public JTextField txtSearchByPriceLow = new JTextField(3);
    public JTextField txtSearchByPriceHigh = new JTextField(3);
    private String[] types = { "1b1b", "2b2b" };
    public JComboBox<String> typeSelect = new JComboBox<>(types);

    public JLabel labelLow = new JLabel("Low");
    public JLabel labelHigh = new JLabel("High");
    public JButton btnSearchByPrice = new JButton("Search By Price");
    public JButton btnSearchByType = new JButton("Search By Type");
    public JButton btnReset = new JButton("Reset");
    Color lightBlue = new Color(230, 246, 250);

    public DT_AptListScreen() {

        this.setTitle("Apartment listings");
        this.setBounds(750, 260, 700, 500);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        JPanel pSearchByPrice = new JPanel(new SpringLayout());
        pSearchByPrice.add(labelLow);
        pSearchByPrice.add(txtSearchByPriceLow);
        pSearchByPrice.add(labelHigh);
        pSearchByPrice.add(txtSearchByPriceHigh);
        pSearchByPrice.add(btnSearchByPrice);
        SpringUtilities.makeCompactGrid(pSearchByPrice,
                1, 5, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad
        pSearchByPrice.setBackground(lightBlue);
        this.getContentPane().add(pSearchByPrice);

        JPanel pSearchByType = new JPanel(new SpringLayout());
        pSearchByType.add(typeSelect);
        pSearchByType.add(btnSearchByType);
        SpringUtilities.makeCompactGrid(pSearchByType,
                1, 2, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad
        pSearchByType.setBackground(lightBlue);
        this.getContentPane().add(pSearchByType);

        JPanel pResetBtn = new JPanel();
        pResetBtn.add(btnReset);
        pResetBtn.setBackground(lightBlue);
        this.getContentPane().add(pResetBtn);

        apts.addColumn("ApartmentID");
        apts.addColumn("Apartment Name");
        apts.addColumn("Type");
        apts.addColumn("Price $/month");
        apts.addColumn("Address");

        Font font = new Font(tblApts.getFont().getName(), Font.PLAIN, 18);
        tblApts.getTableHeader().setFont(font);
        tblApts.setFont(font);
        tblApts.setShowGrid(true);
        tblApts.setGridColor(Color.BLACK);

        int rowHeight = font.getSize() + 4; // Adjust the value as needed
        tblApts.setRowHeight(rowHeight);
        // tblApts.setBackground(lightBlue);

        JScrollPane scrollable = new JScrollPane(tblApts, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // scrollable.setBackground(lightBlue);
        scrollable.setBounds(20, 120, 800, 300);
        this.getContentPane().add(scrollable);
        this.setBackground(lightBlue);
    }
}
