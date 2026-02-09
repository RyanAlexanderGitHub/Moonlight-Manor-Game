import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InventoryPanel extends JPanel {
    Game g;
    JPanel slots;
    JLabel inventoryTitle;

    public InventoryPanel(Game g) {
        this.g = g;
        setPreferredSize(new Dimension(1024, 70)); 
        setBackground(new Color(60, 60, 90)); 
        setLayout(new BorderLayout());
        
        inventoryTitle = new JLabel("Inventory:", SwingConstants.LEFT);
        inventoryTitle.setForeground(Color.WHITE);
        inventoryTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        inventoryTitle.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.add(inventoryTitle, BorderLayout.WEST);


        slots = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        slots.setOpaque(false);
        slots.setBorder(new EmptyBorder(5, 50, 5, 10)); 

        this.add(slots, BorderLayout.CENTER);
        rebuild();
    }

    public void rebuild() {
        slots.removeAll();
        for (Item i : g.inventory.getItems()) {
            JButton btn = new JButton(i.name);
            btn.setPreferredSize(new Dimension(80, 40)); 
            btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                g.inventory.select(i);
                if (g.inventory.getSelected() == i) {
                    g.infoPanel.updateDescription(i.desc); 
                }
                rebuild();
            });
            if (g.inventory.getSelected() == i) {
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            }
            slots.add(btn);
        }

        int empty = g.inventory.capacity - g.inventory.getItems().size();
        for (int j = 0; j < empty; j++) {
            JPanel slot = new JPanel();
            slot.setPreferredSize(new Dimension(80, 40));
            slot.setBackground(new Color(217, 217, 217)); 
            slots.add(slot);
        }

        revalidate();
        repaint();
    }
}