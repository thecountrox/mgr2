import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;

import com.formdev.flatlaf.FlatDarkLaf;

class NpmCommandExecutor {

    public static String executeNpmCommand(String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Redirect error stream to output stream

        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                return line; // Print the output to the console
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
        return "";
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

class openInBrowser {
    public static void openUrl(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                // Use the Desktop class to open the default web browser
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback for systems that don't support Desktop class
                String osName = System.getProperty("os.name").toLowerCase();
                if (osName.contains("linux")) {
                    // Linux-specific command to open a URL in the default browser
                    NpmCommandExecutor.executeNpmCommand("xdg-open", url);
                } else {
                    System.out.println("Unsupported operating system.");
                }
            }
        } catch (IOException | URISyntaxException e) {
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
    private static JButton removePackage;
    private static JButton openBrowser;
    private static JLabel wel;
    private static JTextArea packageListTextArea;

    private static String selectedDirectoryPath = null;
    private static DefaultListModel<String> installedPackagesModel = new DefaultListModel<>();
    private static File sd;

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        jFrame = new JFrame("Package manager");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(1000, 700);

        jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.setBackground(new Color(29, 29, 29));

        packageListTextArea = new JTextArea();
        packageListTextArea.setEditable(false);

        wel = new JLabel("WELCOME");
        wel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wel.setFont(new Font("bahnschrift", Font.PLAIN, 42));
        wel.setForeground(Color.white);

        open = createButton("📂 Open");
        create = createButton("+ Create");
        addPackage = createButton("+ Add Package");
        removePackage = createButton("- Remove Package");
        openBrowser = createButton("Open In Browser");

        JList<String> list = new JList<>(installedPackagesModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex >= 0) {
                    openBrowser.setVisible(true);
                } else {
                    openBrowser.setVisible(false);
                }
            }
        });

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int fc = fileChooser.showOpenDialog(null);

                if (fc == JFileChooser.APPROVE_OPTION) {
                    final File selectedDirectory = fileChooser.getSelectedFile();
                    sd = selectedDirectory;
                    selectedDirectoryPath = selectedDirectory.getAbsolutePath();
                    System.out.println("Selected directory: " + selectedDirectoryPath);
                    readAndDisplayPackageJson(selectedDirectory);
                    // NpmCommandExecutor.executeDirNpmCommand(selectedDirectory, "npm", "info",
                    // selectedDirectoryPath);
                    // NpmCommandExecutor.executeDirNpmCommand(selectedDirectory, "npm", "list",
                    // selectedDirectoryPath);
                    jPanel.removeAll();
                    jPanel.add(Box.createVerticalGlue());
                    jPanel.add(wel);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 30)));
                    jPanel.add(scrollPane);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    jPanel.add(addPackage);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    jPanel.add(removePackage);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    jPanel.add(openBrowser);
                    openBrowser.setVisible(false);
                    jPanel.add(Box.createVerticalGlue());
                    jFrame.revalidate();

                    // After updating the packages list, display them
                    readAndDisplayPackageJson(sd);
                }
            }
        });

        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int fc = fileChooser.showOpenDialog(null);

                if (fc == JFileChooser.APPROVE_OPTION) {
                    final File selectedDirectory = fileChooser.getSelectedFile();
                    sd = selectedDirectory;
                    selectedDirectoryPath = selectedDirectory.getAbsolutePath();
                    System.out.println("Selected directory: " + selectedDirectoryPath);
                    readAndDisplayPackageJson(selectedDirectory);
                    NpmCommandExecutor.executeDirNpmCommand(selectedDirectory, "npm", "init", selectedDirectoryPath);
                    NpmCommandExecutor.executeDirNpmCommand(selectedDirectory, "npm", "info", selectedDirectoryPath);
                    jPanel.removeAll();
                    jPanel.add(Box.createVerticalGlue());
                    jPanel.add(wel);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 30)));
                    jPanel.add(scrollPane);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    jPanel.add(addPackage);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    jPanel.add(removePackage);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    jPanel.add(Box.createVerticalGlue());
                    jFrame.revalidate();

                    // After updating the packages list, display them
                    readAndDisplayPackageJson(sd);
                }
            }
        });

        addPackage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String packageName = showPackageDialog("Add Package");
                if (packageName != null && !packageName.isEmpty()) {
                    NpmCommandExecutor.executeDirNpmCommand(sd, "npm", "install", packageName);
                    readAndDisplayPackageJson(sd);
                }
            }
        });

        removePackage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String packageName = showPackageDialog("Remove Package");
                if (packageName != null && !packageName.isEmpty()) {
                    NpmCommandExecutor.executeDirNpmCommand(sd, "npm", "uninstall", packageName);
                    readAndDisplayPackageJson(sd);
                }
            }
        });

        openBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                String selectedValue = installedPackagesModel.get(selectedIndex);
                if (selectedIndex >= 0) {
                    String[] words = selectedValue.split(" ");
                    if (words.length > 0) {
                        String url = NpmCommandExecutor.executeNpmCommand("npm", "view", words[0], "homepage");
                        System.out.println("debug:"+url);
                        openInBrowser.openUrl(url);
                    }
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

    private static String showPackageDialog(String A) {
        return JOptionPane.showInputDialog(jFrame, "Enter the package name:", A, JOptionPane.PLAIN_MESSAGE);
    }

    private static void readAndDisplayPackageJson(File directory) {
        File packageJsonFile = new File(directory, "package.json");

        // Check if package.json file exists
        if (!packageJsonFile.exists()) {
            JOptionPane.showMessageDialog(jFrame, "package.json file not found in the selected directory.");
            return;
        }

        try {
            String packageJsonContent = new String(Files.readAllBytes(packageJsonFile.toPath()));
            System.out.println("Package JSON content: " + packageJsonContent); // Debug print
            Map<String, String> dependencies = parsePackageJson(packageJsonContent);

            packageListTextArea.setText("");
            installedPackagesModel.clear(); // Clear the list before adding new packages

            if (!dependencies.isEmpty()) {
                dependencies.forEach((packageName, packageVersion) -> {
                    String packageInfo = String.format("%-25s %s", packageName, "(" + packageVersion + ")");
                    installedPackagesModel.addElement(packageInfo + "\n");
                    String installedPackages = installedPackagesModel.toString().replaceAll("(^\\[|\\]$)", "")
                            .replace(",", "");
                    packageListTextArea.setText(installedPackages);
                });
            } else {
                JOptionPane.showMessageDialog(jFrame, "No dependencies found in package.json.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> parsePackageJson(String jsonContent) {
        Map<String, String> dependencies = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            JSONObject dependenciesObject = jsonObject.getJSONObject("dependencies");

            if (dependenciesObject != null) {
                for (String key : dependenciesObject.keySet()) {
                    String value = dependenciesObject.getString(key);
                    dependencies.put(key, value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dependencies;
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