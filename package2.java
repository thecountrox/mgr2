import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.formdev.flatlaf.FlatDarkLaf;

class NpmCommandExecutor {

    public static void main(String[] args) {
        executeNpmCommand("npm","info");
    }

    public static void executeNpmCommand(String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Redirect error stream to output stream

        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print the output to the console
                // You can also process the output as needed for your GUI
            }

            int exitCode = process.waitFor(); // Wait for the process to finish
            if (exitCode == 0) {
                System.out.println("Command executed successfully.");
            } else {
                System.err.println("Command failed with exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void executeDirNpmCommand(File dir, String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(dir);
        processBuilder.redirectErrorStream(true); // Redirect error stream to output stream

        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print the output to the console
                // You can also process the output as needed for your GUI
            }

            int exitCode = process.waitFor(); // Wait for the process to finish
            if (exitCode == 0) {
                System.out.println("Command executed successfully.");
            } else {
                System.err.println("Command failed with exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class package2 {
    
    private static JFrame jFrame;
    private static JPanel jPanel;
    private static JButton open;
    private static JButton create;
    private static JButton addPackage;
    private static JLabel wel;
    private static JTextArea packageListTextArea;
    private static JScrollPane packageListScrollPane;

    private static String selectedDirectoryPath = null;
    private static File sd;

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        JFileChooser fileChooser = new JFileChooser();

        jFrame = new JFrame("Package manager");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(1000, 700);

        jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.setBackground(new Color(29, 29, 29));

        packageListTextArea = new JTextArea();
        packageListTextArea.setEditable(false);
        packageListScrollPane = new JScrollPane(packageListTextArea);

        wel = new JLabel("WELCOME");
        wel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wel.setFont(new Font("bahnschrift", Font.PLAIN, 42));
        wel.setForeground(Color.white);

        open = createButton("📂 Open");
        create = createButton("+ Create");
        addPackage = createButton("+ Add Package");
        addPackage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement code to add a package here
                // For example, you can open a dialog to input the package name and run 'npm install' to add it
            }
        });

        open.addActionListener(new ActionListener() {
            @Override

            // TODO: Implement input validation, error handling

            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int fc = fileChooser.showOpenDialog(null);
                final File selectedDirectory = fileChooser.getSelectedFile();
                sd = selectedDirectory;
                selectedDirectoryPath = selectedDirectory.getAbsolutePath();
                System.out.println("Selected directory: " + selectedDirectoryPath);

                NpmCommandExecutor.executeNpmCommand("npm", "info", selectedDirectoryPath);
                NpmCommandExecutor.executeNpmCommand("npm", "list", selectedDirectoryPath);
                jPanel.removeAll();
                jPanel.add(Box.createVerticalGlue());
                jPanel.add(wel);
                jPanel.add(Box.createRigidArea(new Dimension(0, 30)));
                jPanel.add(packageListScrollPane); // Add the scrollable list
                jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                jPanel.add(addPackage);
                jPanel.add(create);
                jPanel.add(Box.createVerticalGlue());
                jFrame.revalidate();
            }
        });

        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // set file chooser fc to pick folders
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int fc = fileChooser.showSaveDialog(null);
                File selectedDirectory = fileChooser.getSelectedFile();
                sd = selectedDirectory;
                selectedDirectoryPath = selectedDirectory.getAbsolutePath();
                System.out.println("Selected directory: " + selectedDirectoryPath);
                
                NpmCommandExecutor.executeDirNpmCommand(selectedDirectory, "npm", "init", "-y" );
                jFrame.getContentPane().removeAll();
                jFrame.repaint();
            }
        });

        addPackage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e) {
               String packageName = showPackageInputDialog();
                if (packageName != null && !packageName.isEmpty()) {
                    NpmCommandExecutor.executeDirNpmCommand(sd, "npm", "install", packageName);
            }
        }
        });

        jPanel.add(Box.createVerticalGlue());
        jPanel.add(wel);
        jPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Add some space between components
        jPanel.add(open);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some space between components
        jPanel.add(create);
        jPanel.add(Box.createVerticalGlue());
        jFrame.add(jPanel, BorderLayout.CENTER);
        jFrame.setVisible(true);

    }

     private static String showPackageInputDialog() {
        return JOptionPane.showInputDialog(jFrame, "Enter the package name:", "Add Package", JOptionPane.PLAIN_MESSAGE);
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Monospace", Font.PLAIN, 20));
        button.setForeground(Color.white);
        button.setBackground(Color.BLUE);
        button.setMaximumSize(new Dimension(200, 40));
        return button;
    }
}
