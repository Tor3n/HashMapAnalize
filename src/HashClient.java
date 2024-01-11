import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class HashClient implements ActionListener, TreeSelectionListener  {

    JFrame jfMain;
    JSplitPane jpContainer;
    JScrollPane jpTreeView;
    JPanel jpDescr;
    JTree tree;
    DefaultMutableTreeNode top;
    JEditorPane text;
    JMenuBar menuBar;
    JMenuItem exitItem;
    JMenu aboutMenu;
    JMenuItem abbout;
    JMenuItem abboutInstance;
    int arg;
    static LinkedList<JFrame> listOfClients = new LinkedList<>();

    public HashClient(){
        jfMain = new JFrame("HashMap analizer");
        jfMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfMain.setLayout(new BorderLayout());

        initNodes();
        initPanes();
        initMenuBar();

        jpContainer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jpTreeView,jpDescr);
        jfMain.add(jpContainer);
        jpContainer.setDividerLocation(300);

        jfMain.setSize(800,tree.getRowCount()>10&&tree.getRowCount()<30?400:tree.getRowCount()>50?600:500);
        jfMain.setVisible(true);
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        HashMap inputs = InputStarter.getData(args.length<1?0:Integer.parseInt(args[0]));
        HashClient client = new HashClient();
        if (args.length>0){
            client.arg = Integer.parseInt(args[0]);
        }

        HashLogic.printMapParams(inputs, client.getTree(), client.getTop());
        listOfClients.add(client.jfMain);

        for (int i = 0; i < client.getTree().getRowCount(); i++) {
            client.getTree().expandRow(i);
        }
    }

    private void initNodes() {
        top = new DefaultMutableTreeNode("thisHashmap");
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
    }

    private void initPanes(){
        text = new JEditorPane();
        text.setEditable(false);
        text.setOpaque(false);
        text.setText("Press the buckets to see the contents. " +
                "Tree structure represents the relationships between the nodes." +
                " Best Maps are those that don't have linked or treefied nodes.");


        jpTreeView = new JScrollPane(tree);
        jpTreeView.setMinimumSize(new Dimension(300,300));
        jpDescr = new JPanel(new BorderLayout());
        jpDescr.add(new JScrollPane(text), BorderLayout.CENTER);
        jpDescr.setMinimumSize(new Dimension(200,300));
        jpDescr.setBorder(BorderFactory.createCompoundBorder());
    }

    private void initMenuBar(){
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Map.variant");
        for(int i=0; i<InputStarter.maps.size();i++){
            JMenuItem currentVar = new JMenuItem("variant"+i);
            fileMenu.add(currentVar);
            currentVar.addActionListener(this);
        }

        exitItem = new JMenuItem("Exit");
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        exitItem.addActionListener(this);

        JMenu aboutMenu = new JMenu("this.app.info");
        abbout = new JMenuItem("about.app");
        abboutInstance = new JMenuItem("this.HashMap.info");
        aboutMenu.add(abbout);
        aboutMenu.add(abboutInstance);
        abbout.addActionListener(this);
        abboutInstance.addActionListener(this);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        jfMain.setJMenuBar(menuBar);
    }

    private void openAboutWindow() {
        JFrame newWindow = new JFrame("About this");
        newWindow.setSize(400, 200);

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.append("@Toren 2023");
        textArea.append(System.lineSeparator());
        textArea.append("https://github.com/Tor3n");
        textArea.setFont(new Font("Serif",Font.PLAIN,20));


        newWindow.add(scrollPane);

        newWindow.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==exitItem){
            JFrame client = listOfClients.pop();
            this.jfMain.setVisible(false);
            this.jfMain.dispose();
            if(listOfClients.size()<1){
                System.exit(0);
            }
            return;
        }
        if(e.getSource()==abbout){
            StringBuilder txtA = new StringBuilder().append("@Toren 2023").append(System.lineSeparator()).append("https://github.com/Tor3n").append(System.lineSeparator());
            txtA.append("this software is completely free, I even stole some code from chadPT, so who am I to judje.").append(System.lineSeparator());
            txtA.append("Use and reuse this software as you see fit with only exception of making war. Make love instead.");
            text.setText(txtA.toString());
        }
        if( ((JMenuItem) e.getSource()).getText().contains("variant")){
            try {
                main(new String[]{((JMenuItem) e.getSource()).getText().split("variant")[1]});
            } catch (NoSuchFieldException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        } if(e.getSource()==abboutInstance){
            Integer variant = this.arg;
            if(variant==0){
                text.setText("This is a classic HashMap example with key as String and value as Integer," +
                        " equals and hashcode methods were not overriden so the distribution is almost perfect." +
                        " However Hola and Halo have found the same bucket since binary & between its hash and " +
                        " the length-1 is the same. It happens even in the best of cases.");
            }
            if(variant==1){
                text.setText("Here we model a long chain of linked Nodes by overriding HashCode method" +
                        "of the Fruit class used as keys in this HashMap to " +
                        "consider only first letter of the key");
            }
            if(variant==2){
                text.setText("This HashMap contains treefied nodes with left and right branches for fruit class" +
                        "used for keys. ");
            }

        }


    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode choix = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (choix==null) return;
        Object choixNode = choix.getUserObject();

        if (choixNode.equals("thisHashmap")){
            text.setText("Press the buckets to see the contents. " +
                    "Tree structure represents the relationships between the nodes." +
                    " Best Maps are those that don't have linked or treefied nodes.");
        } else {
            if (choixNode instanceof ArrayList){
                text.setText(((ArrayList<String>) choixNode).get(0));
            } else {
                text.setText("this node is empty");
            }

        }

    }

    public JTree getTree(){
        return tree;
    }

    public DefaultMutableTreeNode getTop(){
        return top;
    }
}


