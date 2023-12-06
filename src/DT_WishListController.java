import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DT_WishListController implements ListSelectionListener {
    private DT_WishListScreen screen;
    Apartment apartment = null;

    DT_DataAdaptor myDAO;
    public DT_WishListController(DT_WishListScreen screen, DT_DataAdaptor dao) {
        this.screen = screen;
        this.myDAO = dao;
        screen.getTblApts().getSelectionModel().addListSelectionListener(this);
        apartment = new Apartment();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = screen.getTblApts().getSelectedRow();
            if (selectedRow >= 0) {
                String id = screen.getTblApts().getValueAt(selectedRow, 0).toString();
                // int id = Integer.parseInt(idS);
                // set the id to detail screen for saving the apt to wishlist
                DT_AptAppManager.getInstance().getAptDetailController().setCurrAptID(id);
                apartment = DT_AptAppManager.getInstance().getDataAccess().loadAptByID(id);
                User poster = DT_AptAppManager.getInstance().getDataAccess().loadUserByID(apartment.getPosterID());

                DT_AptDetailScreen aptDetailScreen = DT_AptAppManager.getInstance().getAptDetailScreen();
                aptDetailScreen.setPropertyName(apartment.getAptName());
                aptDetailScreen.setAddress(apartment.getAddress());
                aptDetailScreen.setArea(Double.toString(apartment.getArea()));
                aptDetailScreen.setType(apartment.getType());
                aptDetailScreen.setPrice(Double.toString(apartment.getPrice()));
                aptDetailScreen.setAvailableDate(apartment.getAvailableDate());
                aptDetailScreen.setDescri(apartment.getDescr());
                if (poster != null) {
                    aptDetailScreen.setPosterName(poster.getFullName());
                    aptDetailScreen.setPosterEmail(poster.getEmail());
                } else {
                    aptDetailScreen.setPosterName("unknown_user");
                    aptDetailScreen.setPosterEmail("unknown_email");
                }
                aptDetailScreen.setVisible(true);
            }
        }
    }
}
