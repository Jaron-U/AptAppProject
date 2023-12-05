import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class DT_AptListController implements ListSelectionListener, ActionListener {
    private DT_AptListScreen screen;
    Apartment apartment = null;

    DT_DataAdaptor myDAO;

    public DT_AptListController(DT_AptListScreen screen, DT_DataAdaptor dao) {
        this.screen = screen;
        this.myDAO = dao;
        screen.getTblApts().getSelectionModel().addListSelectionListener(this);
        apartment = new Apartment();
        screen.btnSearchByPrice.addActionListener(this);
        screen.btnSearchByType.addActionListener(this);
        screen.btnReset.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == screen.btnSearchByPrice) { // button Load is clicked
            searchByPrice();
        } else if (e.getSource() == screen.btnSearchByType) { // button Save is clicked
            searchByType();
        } else if (e.getSource() == screen.btnReset) {
            screen.setAptList(myDAO.loadAptList());
        }
    }

    private void searchByType() {
        List<Apartment> apartments = new ArrayList<>();
        try {
            String aptType = (String) screen.typeSelect.getSelectedItem();
            apartments = myDAO.loadAptByType(aptType);
            screen.setAptList(apartments);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Invalid format for numbers!");
            ex.printStackTrace();
        }
    }

    private void searchByPrice() {
        List<Apartment> apartments = new ArrayList<>();
        try {
            double low = Double.parseDouble(screen.txtSearchByPriceLow.getText());
            double high = Double.parseDouble(screen.txtSearchByPriceHigh.getText());
            apartments = myDAO.loadAptByPrice(low, high);
            screen.setAptList(apartments);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Invalid format for numbers!");
            ex.printStackTrace();
        }
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