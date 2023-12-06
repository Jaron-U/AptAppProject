import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DT_AptDetailController implements ActionListener {
    DT_AptDetailScreen aptDetailScreen;
    DT_DataAdaptor myDAO;

    private String currAptID;

    public void setCurrAptID(String currAptID) {
        this.currAptID = currAptID;
    }

    public DT_AptDetailController(DT_AptDetailScreen aptDetailScreen, DT_DataAdaptor dao) {
        this.aptDetailScreen = aptDetailScreen;
        myDAO = dao;
        aptDetailScreen.btnAddWishList.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == aptDetailScreen.btnAddWishList) {
            System.out.println("Current Apt id is " + currAptID);
            WishApt wishApt = new WishApt();
            wishApt.aptID = currAptID;
            wishApt.userID = DT_AptAppManager.getInstance().getCurrentUser().getUserID();
            myDAO.saveApt2WishList(wishApt);

            // Add it to wishlist screen
            Apartment apartment = myDAO.loadAptByID(currAptID);
            Object[] row = new Object[5];
            row[0] = apartment.getId();
            row[1] = apartment.getAptName();
            row[2] = apartment.getType();
            row[3] = apartment.getPrice();
            row[4] = apartment.getAddress();
            DT_AptAppManager.getInstance().getWishListScreen().addRow(row);

            JOptionPane.showMessageDialog(
                    null, "Apartment has been added in WishList!");
        }
    }
}
