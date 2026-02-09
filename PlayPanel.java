import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class PlayPanel extends JPanel {
    Game g;
    Hotspot hovered;
    List<FeedbackLabel> feedbackLabels = new ArrayList<>(); 
    
    private static final int MAX_TEXT_WIDTH = 200;

    private class FeedbackLabel {
        String text;
        Point location;
        long startTime;
        int duration = 4000;
        
        // List to hold the text split into lines
        List<String> wrappedLines; 
        
        public FeedbackLabel(String text, Point p) {
            this.text = text;
            this.location = new Point(p.x, p.y);
            this.startTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > duration;
        }
        
        // New method to wrap text based on MAX_TEXT_WIDTH and FontMetrics
        private void wrapText(Graphics2D g2) {
            wrappedLines = new ArrayList<>();
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();

            String[] words = text.split(" ");
            String currentLine = "";
            
            for (String word : words) {
                String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
                
                if (fm.stringWidth(testLine) < MAX_TEXT_WIDTH) {
                    currentLine = testLine;
                } else {
                    if (!currentLine.isEmpty()) {
                        wrappedLines.add(currentLine);
                    }
                    currentLine = word;
                }
            }
            wrappedLines.add(currentLine); // Add the final line
        }

        public void draw(Graphics2D g2) {
            // Calculate progress and alpha for fading
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = (float)elapsed / duration;
            
            if (wrappedLines == null) {
                // Must call wrapText here as FontMetrics are only available in draw()
                wrapText(g2); 
            }
            
            int alpha = 255;
            if (progress > 0.75f) { 
                alpha = (int) (255 * (1.0f - progress) * 4.0f);
            }
            alpha = Math.max(0, Math.min(255, alpha));
            
            if (alpha <= 0) return; // Stop drawing faded text

            // Calculate vertical offset (rise)
            int yOffset = (int) (progress * 30);
            
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int lineHeight = fm.getHeight();
            
            // Calculate starting Y position
            int totalTextHeight = wrappedLines.size() * lineHeight;
            int yStart = location.y - 10 - yOffset - totalTextHeight / 2;

            for (int i = 0; i < wrappedLines.size(); i++) {
                String line = wrappedLines.get(i);
                int textWidth = fm.stringWidth(line);
                int xCentered = location.x - textWidth / 2;
                int yBase = yStart + (i * lineHeight);

                // Draw drop shadow
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.drawString(line, xCentered + 2, yBase + 2);

                // Draw main text
                g2.setColor(new Color(255, 255, 255, alpha));
                g2.drawString(line, xCentered, yBase);
            }
        }
    }
    
    public PlayPanel(Game g) {
        this.g = g;
        setLayout(null); 
        setBackground(new Color(80, 82, 102));
        
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                g.handleClick(e.getPoint()); 
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                updateHover(e.getPoint());
            }
        });
        
        new javax.swing.Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!feedbackLabels.isEmpty()) {
                    feedbackLabels.removeIf(FeedbackLabel::isExpired);
                    repaint();
                }
            }
        }).start();
    }

    public void addFeedback(String msg, Point location) {
        feedbackLabels.add(new FeedbackLabel(msg, location));
    }

    void updateHover(Point p) {
        if (g.state != GameState.IDLE) return;
        hovered = (g.currentScene == null) ? null : g.currentScene.getHotspotByPoint(p);
        if (hovered != null) setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g2 = (Graphics2D) gg;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (g.currentScene != null) {
            for (Hotspot h : g.currentScene.getHotspots()) {
                Rectangle r = h.bounds;
                g2.setColor(new Color(100, 105, 130, 140));
                g2.fillRect(r.x, r.y, r.width, r.height);
                g2.setColor(Color.WHITE);
                g2.drawString(h.name, r.x + 4, r.y + 16);
            }
        }

        if (hovered != null && g.state == GameState.IDLE) {
            Rectangle r = hovered.bounds;
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(169, 177, 217, 200));
            g2.drawRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
        }
        
        for (FeedbackLabel label : feedbackLabels) {
            label.draw(g2);
        }
    }
}