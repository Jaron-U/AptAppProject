
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DT_PostingController implements ActionListener {
    private DT_PostScreen screen;
    private Apartment aptPost = null;

    public DT_PostingController(DT_PostScreen screen) {
        this.screen = screen;

        screen.getBtnPost().addActionListener(this);

        aptPost = new Apartment();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == screen.getBtnPost())
            addPost();
    }

    private void addPost() {
        String type;
        String propertyName;
        String areaString;
        String address;
        String availableDateString;
        String priceString;
        String description;

        areaString = this.screen.getAreaField().getText();
        Double area = 0.0;
        try {
            area = Double.parseDouble(areaString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "invalid area input");
            return;
        }

        priceString = this.screen.getPriceField().getText();
        Double price;
        try {
            price = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "invalid price input");
            return;
        }

        type = (String) this.screen.getTypeSelect().getSelectedItem();

        propertyName = this.screen.getPropertyNameField().getText();
        if (propertyName.length() <= 0) {
            JOptionPane.showMessageDialog(null, "property name cannot be empty");
            return;
        }

        availableDateString = this.screen.getDateField().getText();
        if (availableDateString.length() <= 0) {
            JOptionPane.showMessageDialog(null, "available date cannot be empty");
            return;
        }
        description = this.screen.getDescrField().getText();

        address = this.screen.getAddressField().getText();
        if (address.length() <= 0) {
            JOptionPane.showMessageDialog(null, "address date cannot be empty");
            return;
        }
        aptPost.setAptName(propertyName);
        aptPost.setType(type);
        aptPost.setPrice(price);
        aptPost.setArea(area);
        aptPost.setAddress(address);
        aptPost.setAvailableDate(availableDateString);
        aptPost.setPosterID(DT_AptAppManager.getInstance().getCurrentUser().getUserID());
        aptPost.setDescr(description);
        if (DT_AptAppManager.getInstance().getDataAccess().saveApt(aptPost)) {
            // int postID = AptAppManager.getInstance().getDataAccess().getLastPostID();
            // int postID = 1111;
            Object[] row = new Object[5];
            row[0] = aptPost.getId();
            row[1] = aptPost.getAptName();
            row[2] = aptPost.getType();
            row[3] = aptPost.getPrice();
            row[4] = aptPost.getAddress();
            DT_AptAppManager.getInstance().getAptList().addRow(row);
            this.screen.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(null, "Unknown Error");
        }
    }
}
