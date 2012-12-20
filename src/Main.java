import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Scanner;


@SuppressWarnings("unused")
public class Main {

	static Hashtable<Character, Integer> frequency = new Hashtable<Character, Integer>(); //Stores characters and their frequencies
	static ArrayList<Node> sortedf = new ArrayList<Node>(); //Stores nodes sorted by their frequencies
	static Hashtable<Character, String> encoded = new Hashtable<Character, String>(); //Stores each character with its binary representation

	public static void main(String[] args) throws FileNotFoundException {
		//System.out.println("Enter a file to be compressed");
		
		Scanner s1 = new Scanner(System.in);
		String file = s1.next();
		
		//System.out.println(file);
		File f = new File(file);
		
		try{

			 FileReader in = new FileReader(file);
			 BufferedReader br = new BufferedReader(in);
			 int character = 0;

			 while(br.ready()){
				 character = br.read();
				 updateFreq((char) character);		
			 }

		 }catch(IOException e){

			 e.printStackTrace();

		 }		
		
		//Create arraylist out of values in the hash table using a class node
		//that stores a character value and its frequency
		
		ArrayList<Character> keys = new ArrayList<Character>(frequency.keySet());
		ArrayList<Integer> values = new ArrayList<Integer>(frequency.values());
		
		for(int i=0; i < keys.size(); i++){
			
			Character c = keys.get(i);
			int v = values.get(i);
			
			sortedf.add(new Node(c, v, null, null));
		}
	
		//Print the frequecy of each character that appears in the file
		//for(Node i: sortedf)
		//System.out.println(i.freq+ " "+i.c);
		
		
		//Create a tree using the frequency hash table
		Node root = makeTree(sortedf);
		
		//Get binary values for each character from the tree
		buildString(root, "");
		
		//Print out the tree
		printTree(root);
		
		//Print out characters and their binary values
		//System.out.println(encoded);
		
		//Get text written in binary
		String binaryText = "";
		binaryText = getBinaryString(f);
		
		//0 pad bits
		int bits = binaryText.length();
		String b = Integer.toString(bits);
		int length = b.length();

		if(length < 10) {
			int rem = 10 - length;
			for(int i=0; i < rem; i++)  b = "0"+b;
		}
		
		//Print the size of the file in bytes
		System.out.print(b);
		
		//Write out each byte of the binary text
		printBit(binaryText);
	}
	
	
	public static void updateFreq(Character c){
				
		//Check if a character is in the hash table already and either
		//add to the table or update its count in the table
		
		if(frequency.containsKey(c)){
			int count = frequency.get(c);
			frequency.remove(c);
			frequency.put(c, count+1);
		}
		else frequency.put(c, 1);
					
	}
	
	public static Node makeTree(ArrayList<Node> n){
		
		PriorityQueue<Node> pq = new PriorityQueue<Node>(n);
		
		while(pq.size() > 1){
			 Node left  = pq.poll();
	         Node right = pq.poll();
	         Node parent = new Node('\0', left.freq + right.freq, left, right);
	         pq.add(parent);	         
		}
		
		return pq.poll();
		
	}
	
	public static void buildString(Node n, String s){
        
		if(n.left != null) buildString(n.left, s + "0");
		else if(n.left == null && n.right == null) encoded.put(n.c, s);
		if(n.right != null) buildString(n.right, s + "1");

	}

	
	public static String getBinaryString(File f){
		String bfile = ""; //Holds binary representation of the text
		String file = ""; //holds the hex representation of the text
		
		//Create a string that replaces each character in the text with its binary representation
		//previously found from making the huffman tree
		
		try{
			 StringBuffer sb = new StringBuffer(); 
			 FileReader in = new FileReader(f);
			 BufferedReader br = new BufferedReader(in);
			 int character = 0;

			 while(br.ready()){
				character = br.read();
				sb.append(encoded.get((char)character));
				 
			 }
			 bfile = sb.toString();

		 }catch(IOException e){
			 e.printStackTrace();
		 }
			
		return bfile;
	}
	
	public static void printBit(String binary){
		
		String temp="";
		StringBuffer t = new StringBuffer(8);
		int value = 0;
		
		for(int j=0; j < binary.length(); j++){
			
			t.append(binary.charAt(j));
			
			if(t.length() == 8){
				temp = t.toString();
				value = Integer.parseInt(temp, 2);
				char out = (char)value;
				System.out.write(out);
				t = new StringBuffer(8);
			}
		}
		
		//Do for the last set of bits
		if(t.length() != 0) {
			temp = t.toString();
			int num = 8 - temp.length();
			for(int i=0; i < num; i++) temp += "0";
			value = Integer.parseInt(temp, 2);
			char out = (char)value;
			System.out.print(out);
		}

	}

	public static void printTree(Node n){
		if(!n.isParent()){
			char aChar= (char) n.c;
			int charVal = aChar;
			System.out.print("(" + charVal + ")");
		}
		else{
			System.out.print("(");
			printTree(n.left);
			printTree(n.right);
			System.out.print(")");
		}
	}
	

}

class Node implements Comparable<Node> {
	
	Character c;
	int freq;
	Node left;
	Node right;
	
	
	public Node(Character c, int f, Node left, Node right){
		this.freq = f;
		this.c = c;
		this.left = left;
		this.right = right;
		
	}
	
	public boolean isParent() {
        return (c == '\0');
    }

	
	public int compareTo(Node ch) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		
		if(this.freq == ch.freq) return EQUAL;
		else if(ch.freq > this.freq) return BEFORE;
		else return AFTER;
	}
	
	
}
