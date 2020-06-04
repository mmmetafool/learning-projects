import javax.swing.*;
import java.io.File;

public class Form {
    private Converter converter;
    private JPanel rootPanel;
    private JPanel buttonsPanel;
    private JButton startButton;
    private JButton stopButton;
    private JButton pauseButton;
    private JPanel mainPanel;
    private JTextField textField;
    private JButton whereToSaveButton;
    private JTextPane infoPanel;
    private String source;
    private String path;
    private Form form;

    Form() {
        form = this;
        startButton.addActionListener(e -> {
            source = textField.getText();
            converter = new Converter(source, false, form);
            System.out.println(source);
            converter.setPath(path);
            if (path.isBlank()){
                path = JOptionPane.showInputDialog("Please enter where do you want to save the file");
                converter.setPath(path);
            }
            if (!converter.getOnWait()) {
                converter.start();
            }
            infoPanel.setText("The program started its work");
            converter.setStartTime( System.currentTimeMillis()/1000);
            converter.setOnWait(false);

        });
        stopButton.addActionListener(e -> {
            converter.getService().shutdownNow();
            infoPanel.setText("You just stopped the work of the program, you can restart it anytime you want");
        });
        pauseButton.addActionListener(e -> {
            converter.setOnWait(true);
            infoPanel.setText("The program have been paused, press start button to continue the work");
        });

        whereToSaveButton.addActionListener(e -> {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileOpen.showOpenDialog(mainPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileOpen.getSelectedFile();
                path = file.getAbsolutePath();
                whereToSaveButton.setText(file.getAbsolutePath());
            }
        });
    }

    void setInfo(String str) {
        infoPanel.setText(str);
    }

    JPanel getRootPanel() {
        return rootPanel;
    }
}
