
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jdom.*;
import org.jdom.input.*;
//JDOM PARSER download JDOM 1.13, unzip 

class ToDo {

    private String title;
    private String desc;

    public ToDo(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String toString() {
        return title;
    }
}

public class ToDoApp extends JFrame {
    //private String taskName;
    //
    public ToDoApp() {
        super();
        Vector<ToDo> myListModel = new Vector<ToDo>();
        JList<ToDo> myList = new JList<ToDo>(myListModel);
        //If file exists, read it
        try {
            SAXBuilder builder = new SAXBuilder();
            Document xmlDoc = builder.build(new File("todo.xml"));

            Element todosElement = xmlDoc.getRootElement();

            for (Object todoObj : todosElement.getChildren()) {
                Element todo = (Element) todoObj; //cast (Element) while this is an object, I know this is an element object. Not just a regular object.
                String desc = todo.getAttributeValue("desc");
                String title = todo.getAttributeValue("title");

                myListModel.add(new ToDo(title, desc));
            }
        } catch (Exception ex) {
        }

        this.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        this.add(top, BorderLayout.NORTH);
        top.setBackground(Color.cyan);

        JPanel bottom = new JPanel();
        this.add(bottom, BorderLayout.SOUTH);

        //To Do Label and box
        top.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        JLabel toDo = new JLabel("To Do ", JLabel.RIGHT);
        toDo.setFont(new Font("Monotype Corsiva", Font.BOLD, 35));
        top.add(toDo, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField todoField = new JTextField("Enter Task Here");
        todoField.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                todoField.setText("");
            }
        });
        top.add(todoField, c);

        //Details label and box
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        JLabel description = new JLabel("Description:", JLabel.CENTER);
        description.setFont(new Font("Monotype Corsiva", Font.BOLD, 25));
        top.add(description, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        JTextArea descField = new JTextArea("", 10, 40);
        descField.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                descField.setText("");
            }
        });
        top.add(descField, c);

        //"Add" Button
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;


        top.add(new JButton() {
            {
                this.setText("Add");
                this.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        myListModel.add(new ToDo(todoField.getText(), descField.getText()));
                        myList.setListData(myListModel);
                    }
                });
            }
        }, c);
    //delete button
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        JButton deleteButton = new JButton("Delete");
        top.add(deleteButton, c);
        myList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent lse) {
                try {
                    ToDo toDoSelected = myList.getSelectedValue();
                    todoField.setText(toDoSelected.getTitle());
                    descField.setText(toDoSelected.getDesc());
                } catch (Exception e) {
                }

                deleteButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        if (myList.getSelectedIndex() >= 0) {
                            myListModel.remove(myList.getSelectedIndex());
                            myList.setListData(myListModel);
                            todoField.setText("");
                            descField.setText("");
                        }
                    }
                });
            }

        });

        bottom.setLayout(new BorderLayout());
        JScrollPane scroller = new JScrollPane(myList);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        bottom.add(scroller, BorderLayout.CENTER);

        
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>";
                    File file = new File("todo.xml");
                    if (!file.exists()) {
                        file.createNewFile();
                    
                    }
                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(header);
                    bw.write("\n");
                    bw.write("<ToDos>");
                    for (ToDo it: myListModel) {
                        bw.write("<ToDo title =\"" + it.getTitle() +"\" desc=\"" + it.getDesc() +"\"/>" +"\n");
                  
                    }
                    bw.write("</ToDos>");
                    bw.close();
                } catch (Exception ex) {}
                
                }
            });
    }
    public static void main(String[] args) {
        // TODO code application logic here
        ToDoApp todo = new ToDoApp();
        todo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        todo.pack();
        todo.setVisible(true);
    }

}
