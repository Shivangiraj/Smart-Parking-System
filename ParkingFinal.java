package parkstore;

import javax.swing.*;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.*;
import java.util.ArrayList;
import java.util.Date;

public class ParkingFinal extends JFrame
{
	
	//class declarations
    private double[][] array;
    private double[][] tempMergArr;
    static private long[][] entertime=new long[9][11] ;//stores the entry time of every booked slot
    private int length;
    private long enter,exit;
            /*1-occuplied slots
          2-road
          0-Mall Entrance
          7-boundary
        */
    static int[][] park = {{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
                    {7, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 7},
                    {7, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 7},
                    {7, 1, 2, 1, 2, 1, 1, 1, 2, 2, 1, 7},
                    {7, 1, 2, 1, 2, 0, 0, 1, 2, 2, 1, 7},
                    {7, 1, 2, 1, 2, 0, 0, 1, 2, 2, 1, 7},
                    {7, 1, 2, 1, 1, 1, 2, 1, 2, 2, 1, 7},
                    {7, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 7},
                    {7, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 7}, 
                    {7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7}};
    static double slots[][] = new double[80][4]; //col 1- row, col 2- column, col 3- free or not (0 if free, 1 if occupied), col 4- distance
    static int ptr = 0; //Stores number of parking slots
    ArrayList<Integer> path = new ArrayList<Integer>();
    ArrayList<Integer> pathf = new ArrayList<Integer>();
    int freer, freec;//free slot store
    int maze[][] = new int[10][12];//graphic print
    
    //panel declarations
    JFrame frame = new JFrame("Parking Slot Allocation");
    JPanel panelCont = new JPanel();
    JPanel panel1 = new JPanel();
    JPanel panel3=new JPanel();
    JPanel panel2 = new JPanel()
    {
        @Override
	    public void paint(Graphics g)//--------------------------------------------------draws maze 
	    {
	        super.paint(g); //To change body of generated methods, choose Tools | Templates.
	        System.out.println("Printing maze.");
	        for (int i = 1; i < 9; i++) 
	        {
	            for (int j = 1; j < 11; j++) 
	                System.out.print(maze[i][j] + "\t");
	            System.out.println("\n");
	        }
	        
	        Color color = null;
	        for(int row = 1; row<9; row++)
	        {
	            for(int col = 1; col<11; col++)
	            {
	                
	                switch(park[row][col])
	                {
	                    case 1: color = Color.cyan; 
	                    		break;
	                    case 2: color = Color.LIGHT_GRAY; 
	                    		break;
	                    case 0: color = Color.WHITE; 
	                    		break;
	                    case 8: color=Color.ORANGE;
	                    		break;
	                }
	                g.setColor(color);
	                g.fillRect(50*col, 50*row, 50, 50);
	                g.setColor(Color.BLACK);
	                g.drawRect(50*col, 50*row, 50, 50);
	            }
	        }
	        
	        //make selected slot green
	        //printing path
	        
	        for(int q=path.size()-1; q>=0; q--)
	            pathf.add(path.get(q));

	            System.out.println(pathf);
	            
	        for (int p = 0; p < pathf.size()-2; p += 2) 
	        {
	            int pathX = pathf.get(p);
	            int pathY = pathf.get(p + 1);
	            g.setColor(Color.GREEN);
	            g.fillRect(pathX * 50, pathY * 50, 50, 50);
	        }
	        
	        g.setColor(Color.RED);
	        g.fillRect(freec * 50, freer * 50, 50, 50);
	    path.clear();
	    pathf.clear();
	    }
    };//panel 2 printing ends
    
    //declaring GUI elements
    JButton bFindSlot = new JButton("Find Slot");
    JLabel print = new JLabel("               ");
    JButton bClose = new JButton("Close");
    JButton bExit =new JButton("Payment");
    JButton bEnter =new JButton("Enter");
    JButton bGoBack=new JButton("Go Back");
    CardLayout cl = new CardLayout();
    JTextField parkip=new JTextField(3);
    JLabel paid=new JLabel("");
    JLabel key1=new JLabel("Grey: Road");
    
    JLabel key=new JLabel("<html>Grey: Road<br>Green:Path<br>Blue:Free slots<br>Orange:Occupied Slots<br>Red: Alloted slot</html>");
    JLabel l1=new JLabel("Enter parking slot:");
    JLabel l2=new JLabel("Parking slot allocated:");
    String parkips;
    
    public ParkingFinal()
    {
        panelCont.setLayout(cl);
        
        //adding elements to panels
        panel1.add(bFindSlot);
        panel2.add(bClose);
        panel2.add(print);
        panel1.add(bExit);
        panel3.add(l1);
        panel3.add(parkip);
        panel3.add(bEnter);
        panel3.add(paid);
        panel3.add(bGoBack);
        panel2.add(l2);
        panel2.add(key);
        //setting background for all panels
        panel1.setBackground(Color.DARK_GRAY);
        panel2.setBackground(Color.DARK_GRAY);
        panel3.setBackground(Color.DARK_GRAY);
        
        //something panel?????
        panelCont.add(panel1, "1");
        panelCont.add(panel2, "2");
        panelCont.add(panel3, "3");
        
        cl.show(panelCont, "1");
        
        bFindSlot.addActionListener(new ActionListener(){//---------------------------FindSlot button
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Starting actionPerformed for Find Slot.");
                
                for(int i = 0; i<ptr; i++)
                {
                    if(slots[i][2] == 0)
                    {
                        //change colour of the slot
                      for(int k = 0; k<10; k++)
                          for(int j = 0; j<12; j++)
                              maze[k][j] = park[k][j];
                      
                        freer = (int)slots[i][0];
                        freec = (int)slots[i][1];                        
                        int no=freer*10+freec;//stores parking slot no. for use during exit
                        System.out.println("Parking slot no.:"+no);
                        
                        maze[freer][freec] = 9;
                        park[freer][freec]=8;
                        slots[i][2] = 1;
                        
                        Date entry=new Date();
                        enter=entry.getTime();//gets the entry time for every call
                        entertime[freer][freec]=enter;
                        System.out.println("Entry time= "+enter);
                       
                        key.setForeground(Color.WHITE);
                        l2.setForeground(Color.WHITE);
                        l2.setText("Parking slot alloted: "+String.valueOf(no));
                        MazeSolver.searchPath(maze, 8, 1, path);
                        System.out.println("Path traversal");
                        System.out.println(path);
                        break;
                    }
                }
                cl.show(panelCont, "2");
                
                }
        });
        
        
        bClose.addActionListener(new ActionListener() {//-----------------------------Close button
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("Starting actionPerformed for Close.");
                cl.show(panelCont, "1");
            }
            
        });
        
        bExit.addActionListener(new ActionListener() {//------------------------------Exit button
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("Starting actionPerformed for Exit.");
                cl.show(panelCont, "3");
                parkip.setEditable(true);
                l1.setForeground(Color.WHITE);
                //parkip.setForeground(Color.WHITE);
                parkip.setText("0");
                paid.setText("\t\t");
            }
        });
        
        bEnter.addActionListener(new ActionListener() {//------------------------------Enter button
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("Starting actionPerformed for Enter.");
            	parkips=parkip.getText();
            	System.out.println("Entered parking lot no.:"+parkips);
            	
             	int n=Integer.parseInt(parkips);
            	//System.out.println(n);
                int r=n/10;
                int c=n%10;//obtain parking slot
                //System.out.println(r+" "+c);
                if(entertime[r][c]!=0)
                {
	                Date exiting=new Date();
	                exit=exiting.getTime();
	                int paying=payment(entertime[r][c],exit);
	                l1.setForeground(Color.WHITE);
	                paid.setForeground(Color.WHITE);
	                paid.setText("Amount to be paid: "+ String.valueOf(paying));
	                System.out.println("Valid /nAmount to be paid= "+paying);
	                entertime[r][c]=0;
	                //if the money is paid the slot is freed
	                for(int i=0;i<ptr;i++)
	                	if(slots[i][0]==r&&slots[i][1]==c)
	                		slots[i][2]=0;
                }
                else
                {
                	System.out.println("Invalid");
                	paid.setForeground(Color.WHITE);
                	paid.setText("Invalid slot entered");
                }
            }
            
        });
        
        bGoBack.addActionListener(new ActionListener() {//-----------------------------Print button
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("Starting actionPerformed for Go Back.");
                cl.show(panelCont, "1");
            }
        });
        
        frame.setPreferredSize(new Dimension(1000,800));
        frame.add(panelCont);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String args[])//-------------------------------------------------main
    {
    //printing array
	    initializeEnterTime(entertime);	
	    System.out.println("Parking layout");
    	printPark(park);
	    //finding parking slots and storing them in slots[][] array
	    for(int row = 1; row<9 ; row++)
	    {
	        for(int col = 1;col<11; col++)
	        {
	            if(park[row][col]==1)
	            {
	                slots[ptr][0] = row;
	                slots[ptr][1] = col;
	                slots[ptr][2] = 0;
	                slots[ptr][3] = Math.pow((Math.pow((col - 6),2) + Math.pow((row - 5), 2)), 0.5);
	                ptr++;
	            }
	        }
	    }
	    //Now, ptr stores the number of parking slots
	
	   //Sorting slots array according to distance
	    ParkingFinal mms = new ParkingFinal();
	        mms.sort(slots, ptr);
	    //Array is now sorted
	    System.out.println("Sorted slots array is: ");
	    printSlots(slots);
	    
	    SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                new ParkingFinal();
	            }
	        });
    }
    
    public void sort(double inputArr[][], int len) 
    {
        this.array = inputArr;
        this.length = len;
        this.tempMergArr = new double[length][4];
        doMergeSort(0, length - 1);
    }
 
    private void doMergeSort(int lowerIndex, int higherIndex) 
    {
         
        if (lowerIndex < higherIndex) 
        {
            int middle = lowerIndex + (higherIndex - lowerIndex) / 2;
            // Below step sorts the left side of the array
            doMergeSort(lowerIndex, middle);
            // Below step sorts the right side of the array
            doMergeSort(middle + 1, higherIndex);
            // Now merge both sides
            mergeParts(lowerIndex, middle, higherIndex);
        }
    }
 
    private void mergeParts(int lowerIndex, int middle, int higherIndex) 
    {
 
        for (int i = lowerIndex; i <= higherIndex; i++) {
            tempMergArr[i][0] = array[i][0];
            tempMergArr[i][1] = array[i][1];
            tempMergArr[i][2] = array[i][2];
            tempMergArr[i][3] = array[i][3];
        }
        int i = lowerIndex;
        int j = middle + 1;
        int k = lowerIndex;
        while (i <= middle && j <= higherIndex) 
        {
            if (tempMergArr[i][3] <= tempMergArr[j][3])
            {
                array[k][0] = tempMergArr[i][0];
                array[k][1] = tempMergArr[i][1];
                array[k][2] = tempMergArr[i][2];
                array[k][3] = tempMergArr[i][3];
                i++;
            } 
            else 
            {
                array[k][0] = tempMergArr[j][0];
                array[k][1] = tempMergArr[j][1];
                array[k][2] = tempMergArr[j][2];
                array[k][3] = tempMergArr[j][3];
                j++;
            }
            k++;
        }
        while (i <= middle) 
        {
            array[k][0] = tempMergArr[i][0];
            array[k][1] = tempMergArr[i][1];
            array[k][2] = tempMergArr[i][2];
            array[k][3] = tempMergArr[i][3];
            k++;
            i++;
        }
 
    }
    
    private int payment(long t1, long t2)//t1= entry t2=exit
    {
    	long dur;
    	int pay=0;
    	dur=t2-t1;
    	dur=dur/1000;
    	System.out.println("Time:"+dur);
    	if(dur<=15)
    		pay=20;
    	else if(dur<=30)
    		pay=30;
    	else if(dur<=60)
    		pay=40;
    	else
    		pay=50;
    	return pay;
    }
    
    static private void printSlots(double a[][] )
    {
    	for(int i = 0; i<ptr; i++)
	    {
	        for(int j = 0; j<4; j++)
	            System.out.print(a[i][j]+"\t");
	        System.out.println("\n");
	    }
    }
    static private void printPark(int a[][])
    {
    	for (int i = 1; i < 9; i++) 
        {
            for (int j = 1; j < 11; j++) 
            {
                System.out.print(park[i][j] + "\t");
            }
            System.out.println("\n");
        }
    }
    static private void initializeEnterTime(long a[][])
    {
    	for(int i=0;i<9;i++)
    		for(int j=0;j<11;j++)
    			entertime[i][j]=0;
    }
}

