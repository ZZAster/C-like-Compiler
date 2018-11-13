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
import java.io.PrintWriter;

public class MyRunner extends JFrame{
    public MyRunner(){
        super("Calculator");

        setSize(800, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        this.add(panel,BorderLayout.NORTH);

        JLabel input_lable = new JLabel("Input: ");
        JTextField input_field = new JTextField(20);
        panel.add(input_lable);
        panel.add(input_field);

        JTextArea output_area = new JTextArea(5,20);
        JScrollPane jsp = new JScrollPane(output_area);
        jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(jsp);

        JButton exc_btn = new JButton("Execute");
        exc_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                output_area.setText("");
                String input = input_field.getText();
                if(input.equals(""))
                    output_area.setText("Please enter the expression in the input field");
                else {
                    getInput(input);

                    Scan scan = new Scan("input.txt");
                    Parser parser = new Parser();
                    output_area.setText(parser.test(scan.getTokens()).toString());
                }
            }
        });
        panel.add(exc_btn);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void getInput(String input){
        try {
            File input_file = new File("input.txt");
            FileWriter fileWritter = new FileWriter(input_file.getName());
            fileWritter.write(input);
            fileWritter.close();

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
