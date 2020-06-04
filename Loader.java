import javax.swing.*;


public class Loader
{
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame();
        Form form = new Form();
        frame.setContentPane(form.getRootPanel());
        frame.setUndecorated(true);
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
