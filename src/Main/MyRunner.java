package Main;

import Lexical.*;
import Parse.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyRunner extends JFrame{
    public MyRunner(){
        super("Calculator");

        Font font = new Font("微软雅黑",Font.PLAIN,20);

        setSize(1200, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        this.add(panel,BorderLayout.NORTH);

        JLabel input_label = new JLabel("Input: ");
        input_label.setFont(font);
        JTextField input_field = new JTextField(20);
        input_field.setFont(font);
        panel.add(input_label);
        panel.add(input_field);

        JTextArea output_area = new JTextArea();
        output_area.setFont(font);
        JScrollPane jsp = new JScrollPane(output_area);
        jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(jsp);

        JButton exc_btn = new JButton("Execute");
        exc_btn.setFont(font);
        exc_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                output_area.setText("");
                String input = input_field.getText();
                if(input.equals(""))
                    output_area.setText("Please enter the expression in the input field");
                else {
                    getInput(input);

                    try {
                        Scan scan = new Scan("input.txt");
                        Parser parser = new Parser();
                        output_area.setText(parser.test(scan.getTokens()).toString());
                    } catch (IOException | ParseException | LexicalException ie){
                        output_area.setText(ie.getMessage());
                    }
                }
            }
        });
        panel.add(exc_btn);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void getInput(String input){
        try {
            File input_file = new File("input.txt");
            FileWriter fileWriter = new FileWriter(input_file.getName());
            fileWriter.write(input);
            fileWriter.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ParseException
    {
        MyRunner runner = new MyRunner();
        runner.setVisible(true);
    }
}
