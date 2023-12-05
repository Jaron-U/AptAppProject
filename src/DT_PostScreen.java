
import javax.swing.*;

// import javafx.event.ActionEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DT_PostScreen extends JFrame {

    private JButton btnPost = new JButton("Post");

    private JTextField propertyNameField = new JTextField(18);
    private JTextField addressField = new JTextField(8);

    private JTextField areaField = new JTextField(4);

    private String[] types = { "1b1b", "2b2b" };
    private JComboBox<String> typeSelect = new JComboBox<>(types);

    private JTextField dateField = new JTextField(8);

    private JTextField priceField = new JTextField(4);

    private JTextArea descrField = new JTextArea(null, 20, 30);

    public JTextField getPropertyNameField() {
        return propertyNameField;
    }

    public JTextField getAddressField() {
        return addressField;
    }

    public JTextField getAreaField() {
        return areaField;
    }

    public JComboBox<String> getTypeSelect() {
        return typeSelect;
    }

    public JTextField getDateField() {
        return dateField;
    }

    public JTextField getPriceField() {
        return priceField;
    }

    public JTextArea getDescrField() {
        return descrField;
    }

    Color lightBlue = new Color(230, 246, 250);

    public DT_PostScreen() {

        this.setTitle("Create New Listing for Apartment");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setSize(550, 650);

        JPanel panelPropertyName = new JPanel();
        panelPropertyName.add(new JLabel("Apartment Name:"));
        panelPropertyName.add(propertyNameField);
        panelPropertyName.setBackground(lightBlue);
        this.getContentPane().add(panelPropertyName);

        JPanel panelInputAddress = new JPanel(new SpringLayout());
        panelInputAddress.add(new JLabel("Address: "));
        panelInputAddress.add(addressField);
        panelInputAddress.add(new JLabel("Area (in sq. ft.): "));
        panelInputAddress.add(areaField);
        panelInputAddress.add(new JLabel("Type: "));
        panelInputAddress.add(typeSelect);
        panelInputAddress.add(new JLabel("Available from: "));
        panelInputAddress.add(dateField);
        panelInputAddress.add(new JLabel("Price (per month): "));
        panelInputAddress.add(priceField);
        SpringUtilities.makeCompactGrid(panelInputAddress,
                5, 2, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad
        panelInputAddress.setBackground(lightBlue);
        this.getContentPane().add(panelInputAddress);

        JPanel panelDescr = new JPanel();
        panelDescr.setLayout(new BoxLayout(panelDescr, BoxLayout.PAGE_AXIS));
        JLabel descrLabel = new JLabel("Description :");
        descrLabel.setHorizontalAlignment(JLabel.LEFT);
        descrLabel.setAlignmentX(LEFT_ALIGNMENT);
        descrField.setAlignmentX(LEFT_ALIGNMENT);

        panelDescr.add(descrLabel);
        panelDescr.add(new JScrollPane(descrField));
        panelDescr.setBackground(lightBlue);
        descrField.setText(
                "Other information you want to share with potential renters, like: \n" +
                        "Amenities, views around the property, is it close to a supermarket, etc.");
        this.getContentPane().add(panelDescr);

        JPanel panelButton = new JPanel();
        panelButton.setPreferredSize(new Dimension(400, 80));
        panelButton.add(btnPost);
        panelButton.setBackground(lightBlue);
        this.getContentPane().add(panelButton);
    }

    public JButton getBtnPost() {
        return btnPost;
    }
}