package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame {

    private IUIHandler m_StartHandler = null;

    public MainFrame()
    {
        JFrame frame = new JFrame("Bot settings");
        // when you close the frame, the app exits
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // center the frame and show it

        String[] Labels = {"Bot token: ", "host: ", "port: ", "Database: ", "SDK User: ", "password"};
        final JTextField[] Fields = {null, null, null, null, null, null};
        int numPairs = Labels.length;

        //Create and populate the panel.
        frame.getContentPane().setLayout(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel Label = new JLabel(Labels[i], JLabel.TRAILING);
            frame.getContentPane().add(Label);

            if (i == numPairs - 1) {
                Fields[i] = new JPasswordField(10);
            }
            else {
                Fields[i] = new JTextField(10);
            }

            Label.setLabelFor(Fields[i]);
            frame.getContentPane().add(Fields[i]);
        }

        JButton StopButton = new JButton("Stop");
        frame.getContentPane().add(StopButton);
        JButton StartButton = new JButton("Start");
        frame.getContentPane().add(StartButton);
        StartButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (m_StartHandler == null)
                            return;;


                        m_StartHandler.start(
                                Fields[0].getText(), Fields[1].getText(), Integer.parseInt(Fields[2].getText())
                                , Fields[3].getText(), Fields[4].getText(), Fields[5].getText()
                        );
                    }
                }
        );

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(frame.getContentPane(), numPairs + 1, 2,15, 15,15, 10);       //xPad, yPad
        frame.setSize(300, 270);
        frame.setVisible(true);
    }

    public void setStartHandler(IUIHandler _StartHandler)
    {
        m_StartHandler = _StartHandler;
    }
}
