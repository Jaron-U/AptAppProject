
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DT_RegisterScreen extends JFrame {
    private JTextField txtUserName = new JTextField(10);
    private JTextField txtPassword = new JTextField(10);
    private JTextField txtEmail = new JTextField(10);
    private JTextField txtFullName = new JTextField(10);

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
        
        Color lightBlue = new Color(230, 246, 250);
        this.setBackground(lightBlue);

        this.setSize(300, 270);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("New User Register");
        Font titleFont = new Font(title.getFont().getName(), Font.BOLD, 18);
        title.setFont(titleFont);
        titlePanel.add(title);
        titlePanel.setBackground(lightBlue);
        this.getContentPane().add(titlePanel);

        JPanel main = new JPanel(new SpringLayout());
        main.add(new JLabel("Username:"));
        main.add(txtUserName);
        main.add(new JLabel("Password:"));
        main.add(txtPassword);
        main.add(new JLabel("Email:"));
        main.add(txtEmail);
        main.add(new JLabel("Preferred Name:"));
        main.add(txtFullName);
        main.setBackground(lightBlue);

        SpringUtilities.makeCompactGrid(main,
                4, 2, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad

        JPanel buttons = new JPanel();
        buttons.add(btnReg);
        buttons.setBackground(lightBlue);

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
