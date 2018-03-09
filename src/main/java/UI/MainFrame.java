package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MainFrame {

    private IUIHandler m_UIHandler = null;

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

        loadLastValues(Fields[0], Fields[1], Fields[2], Fields[3], Fields[4], Fields[5]);

        JButton stopButton = new JButton("Stop");
        frame.getContentPane().add(stopButton);
        stopButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (m_UIHandler == null)
                            return;

                        m_UIHandler.stop();
                    }
                }
        )
        ;
        JButton startButton = new JButton("Start");
        frame.getContentPane().add(startButton);
        startButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveValues( Fields[0].getText(), Fields[1].getText(), Fields[2].getText()
                                , Fields[3].getText(), Fields[4].getText());

                        if (m_UIHandler == null)
                            return;

                        m_UIHandler.start(
                                Fields[0].getText(), Fields[1].getText(), Integer.parseInt(Fields[2].getText())
                                , Fields[3].getText(), Fields[4].getText(), Fields[5].getText()
                        );
                    }
                }
        );

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(frame.getContentPane(), numPairs + 1, 2,15, 5,5, 5);       //xPad, yPad
        frame.setSize(250, 225);
        frame.setVisible(true);
    }

    public void setUIHandler(IUIHandler _StartHandler)
    {
        m_UIHandler = _StartHandler;
    }

    private void loadLastValues(JTextField _token, JTextField _host, JTextField _port
            , JTextField _database, JTextField _SDKuser, JTextField _Password) {
        try (BufferedReader br = new BufferedReader(new FileReader("user.config"))) {
            String line = br.readLine();
            _token.setText(line);
            line = br.readLine();
            _host.setText(line);
            line = br.readLine();
            _port.setText(line);
            line = br.readLine();
            _database.setText(line);
            line = br.readLine();
            _SDKuser.setText(line);
            _Password.setText("hpmadm");
        } catch (IOException e) {
            System.out.println("Can not read values from config");
            e.printStackTrace();
        }
    }

    private void saveValues(String _token, String _host, String _port, String _database, String _SDKuser) {

        File config = new File("user.config");
        try {
            config.createNewFile();
        } catch (IOException error) {
            System.out.println("Can not save values to config");
            error.printStackTrace();
            return;
        }

        try (PrintStream out = new PrintStream(new FileOutputStream(config))) {
            out.println(_token);
            out.println(_host);
            out.println(_port);
            out.println(_database);
            out.println(_SDKuser);
        } catch (FileNotFoundException error) {

        }
    }
}
