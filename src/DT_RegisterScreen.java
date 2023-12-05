
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DT_RegisterScreen extends JFrame {
    private JTextField txtUserName = new JTextField(10);
    private JTextField txtPassword = new JTextField(10);
    private JTextField txtEmail = new JTextField(10);
    private JTextField txtFullName  = new JTextField(10);

    public JTextField getTxtFullName() {
        return txtFullName;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JTextField getTxtPassword() {
        return txtPassword;
    }

    public JTextField getTxtUserName() {
        return txtUserName;
    }

    private JButton btnReg = new JButton("Register");

    public DT_RegisterScreen() {

        this.setSize(300, 220);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.getContentPane().add(new JLabel("New User Register"));

        JPanel main = new JPanel(new SpringLayout());
        main.add(new JLabel("Username:"));
        main.add(txtUserName);
        main.add(new JLabel("Password:"));
        main.add(txtPassword);
        main.add(new JLabel("Email:"));
        main.add(txtEmail);
        main.add(new JLabel("Preferred Name:"));
        main.add(txtFullName);

        SpringUtilities.makeCompactGrid(main,
                4, 2, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad

        JPanel buttons = new JPanel();
        buttons.add(btnReg);

        this.getContentPane().add(main);
        this.getContentPane().add(buttons);
        // this.getContentPane().add(btnLogin);
        btnReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DT_AptAppManager.getInstance().getLoginScreenCtrl().onReg(e);
            }
        }

        );
    }

}
