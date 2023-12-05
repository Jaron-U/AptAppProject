
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// This is the controller for both login screen and register
public class DT_LoginController {
    public void onLogin(ActionEvent e) {
        // if (e.getSource() == this.getBtnLogin()) {
        String username = DT_AptAppManager.getInstance().getLoginScreen().getTxtUserName().getText().trim();
        String password = DT_AptAppManager.getInstance().getLoginScreen().getTxtPassword().getText().trim();

        System.out.println("Login with username = " + username + " and password = " + password);
        User user = DT_AptAppManager.getInstance().getDataAccess().loadUser(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(null, "This user does not exist!");
        } else {
            DT_AptAppManager.getInstance().setCurrentUser(user);
            DT_AptAppManager.getInstance().getLoginScreen().setVisible(false);
            DT_AptAppManager.getInstance().getMainScreen().setVisible(true);
            DT_WishListScreen wishListScreen = DT_AptAppManager.getInstance().getWishListScreen();
            wishListScreen.setAptList(
                    DT_AptAppManager.getInstance().getDataAccess().loadWishListByUserID(user.getUserID()));

            DT_AptListScreen aptListScreen = DT_AptAppManager.getInstance().getAptList();
            aptListScreen.setAptList(DT_AptAppManager.getInstance().getDataAccess().loadAptList());
        }
    }

    public void onReg(ActionEvent e) {
        String username = DT_AptAppManager.getInstance().getRegScreen().getTxtUserName().getText().trim();
        String password = DT_AptAppManager.getInstance().getRegScreen().getTxtPassword().getText().trim();
        String name = DT_AptAppManager.getInstance().getRegScreen().getTxtFullName().getText().trim();
        String email = DT_AptAppManager.getInstance().getRegScreen().getTxtEmail().getText().trim();

        System.out.println(
                "Register with username = " + username + " and password = " + password + " " + name + " " + email);
        User user = new User();
        user.setUsername(username);
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(password);
        // return;
        if (DT_AptAppManager.getInstance().getDataAccess().saveUser(user)) {
            // If register is successful, log the user in
            DT_AptAppManager.getInstance().getRegScreen().setVisible(false);
            DT_AptAppManager.getInstance().getLoginScreen().setVisible(false);
            DT_AptAppManager.getInstance().setCurrentUser(user);
            DT_AptAppManager.getInstance().getMainScreen().setVisible(true);
            DT_WishListScreen wishListScreen = DT_AptAppManager.getInstance().getWishListScreen();
            wishListScreen.setAptList(
                    DT_AptAppManager.getInstance().getDataAccess().loadWishListByUserID(user.getUserID()));

            DT_AptListScreen aptListScreen = DT_AptAppManager.getInstance().getAptList();
            aptListScreen.setAptList(DT_AptAppManager.getInstance().getDataAccess().loadAptList());
        } else {
            JOptionPane.showMessageDialog(null, "username already exists!");
            return;
        }
    }

}
