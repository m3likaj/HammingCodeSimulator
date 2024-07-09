package HemmingCodeSimulator;

import static javax.swing.SwingConstants.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

public class visuals implements ActionListener {
	JFrame frame;
	ArrayList<JButton> Bits = new ArrayList<JButton>();
	JButton sendButton, readButton;
	JButton[] fourBits, eightBits, sixteenBits, readBits;
	Font titleFont;
	JLabel title, instruction, encoded, decoded, result, correctedData;
	JTabbedPane tabPanel;
	JPanel page1, page2, page3, send, read, completeDataPanel;
	static int[] checkBitsWrite, checkBitsRead, checkBitsPos, completeData;
	int checkBitWrite, checkBitRead;
	boolean evenParityWrite, evenParityRead;

	public visuals() {
		titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 50);
		title = new JLabel();
		title.setText("HAMMING CODE SIMULATOR");
		title.setFont(titleFont);
		title.setForeground(Color.white);
		title.setHorizontalAlignment(CENTER);
		title.setVerticalAlignment(CENTER);

		instruction = new JLabel();
		instruction.setText("Please choose the length of the bits. Click on the buttons to alternate between 0 and 1");
		instruction.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		instruction.setForeground(Color.white);
		instruction.setHorizontalAlignment(CENTER);
		instruction.setVerticalAlignment(CENTER);

		tabPanel = new JTabbedPane();
		tabPanel.setBounds(0, 0, 700, 500);
		tabPanel.setForeground(Color.white);
		tabPanel.setBackground(Color.black);

		page1 = new JPanel();// four bits tab
		page1.setBackground(Color.black);
		fourBits = new JButton[4];
		for (int i = 0; i < 4; i++) {
			fourBits[i] = new JButton("0");
			fourBits[i].setPreferredSize(new Dimension(40, 40));
			fourBits[i].addActionListener(this);
			page1.add(fourBits[i]);
			Bits.add(fourBits[i]);
		}

		page2 = new JPanel();
		page2.setBackground(Color.black);// 8 bit tab
		eightBits = new JButton[8];
		for (int i = 0; i < 8; i++) {
			eightBits[i] = new JButton("0");
			eightBits[i].setPreferredSize(new Dimension(40, 40));
			eightBits[i].addActionListener(this);
			page2.add(eightBits[i]);
			Bits.add(eightBits[i]);
		}

		page3 = new JPanel();// 16 bit tab
		page3.setBackground(Color.black);
		sixteenBits = new JButton[16];
		for (int i = 0; i < 16; i++) {
			sixteenBits[i] = new JButton("0");
			sixteenBits[i].setPreferredSize(new Dimension(40, 40));
			sixteenBits[i].addActionListener(this);
			page3.add(sixteenBits[i]);
			Bits.add(sixteenBits[i]);
		}

		send = new JPanel();// container for send button
		send.setBackground(Color.black);
		sendButton = new JButton("Send");
		sendButton.setPreferredSize(new Dimension(100, 30));
		sendButton.setBackground(Color.green);
		sendButton.addActionListener(this);
		send.add(sendButton);

		read = new JPanel();
		read.setVisible(false);
		read.setBackground(Color.black);
		read.setPreferredSize(new Dimension(60,60));
		readButton = new JButton("Read");
		readButton.setPreferredSize(new Dimension(100, 30));
		readButton.setBackground(Color.green);
		readButton.addActionListener(this);
		read.add(readButton);

		tabPanel.addTab("4 bit", page1);
		tabPanel.addTab("8 bit", page2);
		tabPanel.addTab("16 bit", page3);

		completeDataPanel = new JPanel();
		completeDataPanel.setVisible(false);
		encoded = new JLabel();
		decoded = new JLabel();
		result = new JLabel();
		correctedData = new JLabel();
		
		frame = new JFrame();
		frame.setTitle("Hamming Code Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(800, 900);
		frame.getContentPane().setBackground(Color.black);
		frame.setLayout(new GridLayout(10, 25));
		frame.add(title);
		frame.add(instruction);
		frame.add(tabPanel);
		frame.add(send);
		frame.add(encoded);
		frame.add(completeDataPanel);
		frame.add(read);
		frame.add(result);
		frame.add(correctedData);
		frame.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton temp = (JButton) e.getSource();
		if (Bits.contains(temp)) {
			if (temp.getText().equals("0"))
				temp.setText("1");
			else
				temp.setText("0");
		} else if (e.getSource() == sendButton) {

			if (tabPanel.getSelectedIndex() == 0)
				writeBits(getButtonBits(fourBits));
			else if (tabPanel.getSelectedIndex() == 1)
				writeBits(getButtonBits(eightBits));
			else
				writeBits(getButtonBits(sixteenBits));
		} else if (e.getSource() == readButton) {
			readData();
		}

	}

	public int[] getButtonBits(JButton[] buttons) {
		int[] bits = new int[buttons.length];
		for (int i = 0; i < buttons.length; i++) {// gets the bits and reverses the order
			bits[i] = Integer.valueOf(buttons[buttons.length - i - 1].getText());
		}
		return bits;
	}

	public boolean checkEvenParity(int[] dataBits) {
		int count = 0;
		for (int i = 0; i < dataBits.length; i++) {
			if (dataBits[i] == 1)
				count += 1;

		}
		return ((count % 2) == 0) ? true : false;
	}

	public void writeBits(int[] bits) {
		// calculating how many check bits are needed
		int checkNo = 0;
		int[] dataBits;
		for (int i = 1; i < bits.length; i++) {
			if (Math.pow(2, i) >= bits.length + i + 1) {
				checkNo = i;
				break;
			}
		}
		checkBitsPos = new int[checkNo];// array to hold the positions of the check bits
		for (int i = 0; i < checkNo; i++) {
			checkBitsPos[i] = (int) Math.pow(2, i) - 1;// a decrement of one because its zero indexed;
		}
		dataBits = new int[checkNo + bits.length];

		// marking the check bits with 2 and positioning data bits in big endian
		for (int i = 0, j = 0, k = 0; i < dataBits.length; i++, j++) {
			if (k < checkBitsPos.length && i == checkBitsPos[k]) {
				dataBits[i] = 2;
				k++;
				j--;
			} else
				dataBits[i] = bits[j];
		}
		System.out.print("\n");
		calculateCheckBits(dataBits, 'w');
		evenParityWrite = checkEvenParity(dataBits);
		displayCompleteData();
	}
	public void reset() {
		for(JButton bit : readBits) {
			bit.setOpaque(false);
			bit.setBackground(Color.gray);
		}
		correctedData.setVisible(false);
		encoded.setVisible(false);
		
	}
	public void readData() {
		int[] dataBits = getButtonBits(readBits);
		reset();
		evenParityRead = checkEvenParity(dataBits);
		calculateCheckBits(dataBits, 'r');
		checkBitsRead = new int[checkBitsPos.length];
		System.out.print("Check bits when decoding: ");
		// gets the check bits from the data
		for (int i = 0; i < checkBitsPos.length; i++) {
			checkBitsRead[i] = dataBits[checkBitsPos[checkBitsPos.length - i - 1]];
			System.out.print(checkBitsRead[i]);
		}
		System.out.print("\n");
		// turns the check bits into a string then the string to an intiger
		checkBitRead = Integer.parseInt(Arrays.toString(checkBitsRead).replaceAll("\\[", "").replaceAll("\\]", "")
				.replaceAll(",", "").replaceAll("\\s", ""), 2);
		// calculates the syndrome code
		int syndromeCode = checkBitRead ^ checkBitWrite;

		// checks if the encoded and decoded bits have the same parity
		boolean differentParity = evenParityRead ^ evenParityWrite;

		// reversing the data bits
		completeData = new int[dataBits.length];
		for (int i = 0; i < dataBits.length; i++) {
			completeData[i] = dataBits[dataBits.length - i - 1];
			System.out.print(completeData[i]);
		}

		String comment, corrected = "";

		System.out.println("The incorrect bit is: " + syndromeCode);
		System.out.println("The incorrect bit is bit: " + (completeData.length - syndromeCode + 1));
		if (!differentParity) {
			if (syndromeCode == 0)
				comment = "There is no incorrect bit. The data hasnt been tampered  with";

			else {
				comment = "There is more than one incorrect bit. The data  can't be corrected";
			}
		} else if (syndromeCode == 0)
			comment = "There is more than one incorrect bit. The data  can't be corrected";

		else {
			correctedData.setVisible(true);
			comment = "The incorrect bit is in bit position (big endian): " + syndromeCode;
			if ((syndromeCode & (syndromeCode - 1)) == 0) {

				comment = "The incorrect bit is a check bit. " + comment;
			} else {
				comment = "The incorrect bit is a data bit. " + comment;
			}
			readBits[completeData.length-syndromeCode].setOpaque(true);
			readBits[completeData.length-syndromeCode].setBackground(Color.red);
			completeData[completeData.length - syndromeCode] = (completeData[completeData.length - syndromeCode] == 0)
					? 1
					: 0;
			corrected = Arrays.toString(completeData).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "")
					.replaceAll("\\s", "");

		}
		result.setText(comment);
		result.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		result.setForeground(Color.white);
		result.setHorizontalAlignment(CENTER);
		result.setVerticalAlignment(CENTER);

		correctedData.setText("The corrected data is: " + corrected);
		correctedData.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		correctedData.setForeground(Color.white);
		correctedData.setHorizontalAlignment(CENTER);
		correctedData.setVerticalAlignment(CENTER);

		frame.setVisible(true);
		System.out.println("Even parity write= " + evenParityWrite);
		System.out.println("Even parity read= " + evenParityRead);
		System.out.println("CheckBit write= " + checkBitWrite);
		System.out.println("CheckBit read= " + checkBitRead);

	}

	public void calculateCheckBits(int[] dataBits, char c) {
		checkBitWrite = 0;
		for (int i = 0, k = 0; i < dataBits.length; i++) {
			if (k < checkBitsPos.length && i == checkBitsPos[k]) {
				k++;
				continue;
			} else if (dataBits[i] == 1) {
				checkBitWrite ^= i + 1;
			}
		}
		// turns the calculated check bit to binary and displays it in the desired
		// length
		String result = String.format("%" + checkBitsPos.length + "s", Integer.toBinaryString(checkBitWrite))
				.replaceAll(" ", "0");
		System.out.println("Checkbits int: " + checkBitWrite);
		System.out.println("Checkbits:" + result);

		// turns the result to an array of integers
		checkBitsWrite = Arrays.stream(result.split("")).mapToInt(Integer::parseInt).toArray();

		// checking
		System.out.print("Check bits when encoding: ");
		for (int i = 0; i < checkBitsWrite.length; i++) {
			System.out.print(checkBitsWrite[i]);
		}
		System.out.print("\n");
		if (c == 'w') {
			completeData = new int[dataBits.length];
			// places the check bits in their right place
			for (int i = 0; i < checkBitsWrite.length; i++) {
				dataBits[checkBitsPos[i]] = checkBitsWrite[checkBitsWrite.length - i - 1];
			}

			// checking
			System.out.print("\n");
			for (int i = 0; i < dataBits.length; i++) {
				completeData[i] = dataBits[dataBits.length - i - 1];
				System.out.print(completeData[i]);
			}
			System.out.print("\n");
		}
	}

	public void displayCompleteData() {
		encoded.setText("The calculated parity bits are: " + Arrays.toString(checkBitsWrite).replaceAll("\\[", "")
				.replaceAll("\\]", "").replaceAll(",", "").replaceAll("\\s", "") + "\nThe encoded Message is: ");
		encoded.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		encoded.setForeground(Color.white);
		encoded.setHorizontalAlignment(CENTER);
		encoded.setVerticalAlignment(CENTER);
		encoded.setVisible(true);

		completeDataPanel.setVisible(false);
		Component[] components = completeDataPanel.getComponents();
		for (Component component : components) {
			completeDataPanel.remove(component);
		}

		completeDataPanel.setBackground(Color.black);
		readBits = new JButton[completeData.length];
		for (int i = 0, k = checkBitsPos.length - 1; i < completeData.length; i++) {
			readBits[i] = new JButton("" + completeData[i]);
			if ((k >= 0) && (i == (completeData.length - checkBitsPos[k] - 1))) {
				readBits[i].setOpaque(true);
				readBits[i].setBackground(Color.yellow);
				k--;
			}
			readBits[i].setPreferredSize(new Dimension(30, 40));
			readBits[i].addActionListener(this);
			completeDataPanel.add(readBits[i]);
			Bits.add(readBits[i]);
		}

		decoded.setText("Click on the bits to change values. Click on read to check for any changes");
		decoded.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		decoded.setForeground(Color.white);
		decoded.setHorizontalAlignment(CENTER);
		decoded.setVerticalAlignment(CENTER);
		read.setVisible(true);
		completeDataPanel.setVisible(true);

		frame.setVisible(true);
	}

	public void displayResult() {

	}
}