import javax.swing.*;
import java.awt.*;

public class DT_AptDetailScreen extends JFrame {
    public JButton btnAddWishList = new JButton("Add to wish list");
    private JLabel propertyName = new JLabel("");
    private JLabel type = new JLabel("");
    private JLabel address = new JLabel("");
    private JLabel price = new JLabel("");
    private JLabel area = new JLabel("");
    private JTextArea descri = new JTextArea("");
    private JLabel availableDate = new JLabel("");
    private JLabel posterName = new JLabel("");
    private JLabel posterEmail = new JLabel("");

    public void setPosterName(String s) {
        this.posterName.setText(s);
    }

    public void setType(String s) {
        this.type.setText(s);
    }

    public void setPosterEmail(String s) {
        this.posterEmail.setText(s);
    }

    public void setAvailableDate(String s) {
        this.availableDate.setText(s);
    }

    public void setDescri(String s) {
        this.descri.setText(s);
    }

    public void setArea(String s) {
        this.area.setText(s);
    }

    public void setPrice(String s) {
        this.price.setText(s);
    }

    public void setPropertyName(String s) {
        this.propertyName.setText(s);
    }

    public void setAddress(String s) {
        this.address.setText(s);
    }

    public DT_AptDetailScreen() {
        Color lightBlue = new Color(230, 246, 250);

        this.setTitle("Apartment Detail");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setBounds(700, 200, 550, 650);

        Font titleFont = new Font(propertyName.getFont().getName(), Font.PLAIN, 24);
        propertyName.setFont(titleFont);
        Font contentFont = new Font(propertyName.getFont().getName(), Font.PLAIN, 18);
        Font descrFont = new Font(propertyName.getFont().getName(), Font.PLAIN, 15);
        type.setFont(contentFont);
        price.setFont(contentFont);
        area.setFont(contentFont);
        address.setFont(contentFont);
        descri.setFont(descrFont);
        descri.setEditable(false);
        availableDate.setFont(contentFont);
        posterName.setFont(contentFont);
        posterEmail.setFont(contentFont);

        this.getContentPane().add(propertyName);

        JPanel panelContent = new JPanel(new SpringLayout());
        JLabel typeLabel = new JLabel("Type: ");
        typeLabel.setFont(contentFont);
        panelContent.add(typeLabel);
        panelContent.add(type);
        JLabel priceLabel = new JLabel("Price $/Month: ");
        priceLabel.setFont(contentFont);
        panelContent.add(priceLabel);
        panelContent.add(price);
        JLabel areaLabel = new JLabel("Area sq.ft: ");
        areaLabel.setFont(contentFont);
        panelContent.add(areaLabel);
        panelContent.add(area);
        JLabel addressLabel = new JLabel("Address: ");
        addressLabel.setFont(contentFont);
        panelContent.add(addressLabel);
        panelContent.add(address);
        JLabel avaDateLabel = new JLabel("Available Date: ");
        avaDateLabel.setFont(contentFont);
        panelContent.add(avaDateLabel);
        panelContent.add(availableDate);
        JLabel posterNameLabel = new JLabel("Poster Name: ");
        posterNameLabel.setFont(contentFont);
        panelContent.add(posterNameLabel);
        panelContent.add(posterName);
        JLabel posterEmailLabel = new JLabel("Poster Email: ");
        posterEmailLabel.setFont(contentFont);
        panelContent.add(posterEmailLabel);
        panelContent.add(posterEmail);
        SpringUtilities.makeCompactGrid(panelContent,
                7, 2, // rows, cols
                6, 6, // initX, initY
                6, 6); // xPad, yPad
        // panelContent.setBackground(lightBlue);
        this.getContentPane().add(panelContent);

        JPanel descrPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel descrLabel = new JLabel("Description: ");
        // descrPanel.setBackground(lightBlue);
        
        descrLabel.setFont(contentFont);
        descrPanel.add(descrLabel);
        this.getContentPane().add(descrPanel);
        JScrollPane scrollPane = new JScrollPane(descri);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // 禁用水平滚动条
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // 根据需要显示垂直滚动条
        this.getContentPane().add(scrollPane);

        btnAddWishList.setPreferredSize(new Dimension(140, 50));
        JPanel panelButton = new JPanel();
        panelButton.add(btnAddWishList);
        this.getContentPane().add(panelButton);

    }
}
