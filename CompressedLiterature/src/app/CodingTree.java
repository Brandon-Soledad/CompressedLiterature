package app;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CodingTree {
	
	public final Map<Character, Integer> charMap = new HashMap<Character, Integer>();
	public final Map<Character, String> codes = new HashMap<Character, String>(); 
	public final ArrayList<Node> charList = new ArrayList<Node>();
	public byte[] bytes;
	public Node root;
	public StringBuilder encoded;
	
	static class Node implements Comparable<Node> {
        char ch;
        int frequency;
        Node left, right;

        Node(char ch, int frequency, Node left, Node right) {
            this.ch = ch;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
        
        Node(int frequency, Node left, Node right) {
        	this.frequency = frequency;
        	this.left = left;
        	this.right = right;
        }
        
        private boolean isLeaf() {
            return (left == null) && (right == null);
        }

        public int compareTo(Node that) {
            return this.frequency - that.frequency;
        }
        
        public String toString() {
			return "Node" + ch + "Frequency:" + frequency;					
        }
	}
	/**
     * Counts frequency of characters in a string.
     * @param message The message passed in to be inspected and count the frequency of its characters
     */
	public CodingTree(String message){
		for (int i = 0; i <message.length()-1; i++) {
			if (charMap.containsKey(message.charAt(i))) {
				int old = charMap.get(message.charAt(i));
				old++;
				charMap.put(message.charAt(i), old);
			} else {
				charMap.put(message.charAt(i), 1);
				charList.add(new Node(message.charAt(i), 1, null, null));
			}
		}
		
		for (int i = 0; i<charList.size(); i++) {
			charList.get(i).frequency = charMap.get(charList.get(i).ch);
		}
		
		Collections.sort(charList);		
		createTree(charList);
		createCodes(root, "");
		writeBits(message);
	}
	
	private void createTree(ArrayList<Node> list) {
		ArrayList<Node> temp = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			temp.add(list.get(i));
		}
		while (temp.size() > 1) {
			Collections.sort(temp);
			Node newTree = new Node(temp.get(0).frequency+temp.get(1).frequency,
					temp.get(0), temp.get(1));
			temp.remove(0);
			temp.remove(0);
			temp.add(0, newTree);
		}
		root = temp.get(0);	
	}

	private void createCodes(Node troot, String tcode) {
		if (!troot.isLeaf()) {
			createCodes(troot.left, tcode+'0');
			createCodes(troot.right, tcode+'1');			
		} else {
			codes.put(troot.ch, tcode);
		}
	}
	
	private void writeBits(String message) {
		encoded = new StringBuilder();
		for (int i = 0; i <message.length()-1; i++) {
			encoded.append(codes.get(message.charAt(i)));
		}

		bytes = new byte[encoded.length()/8];
		for (int i = 0; i < bytes.length;i++) {
			bytes[i] = (byte) Integer.parseUnsignedInt(encoded.substring(i*8, (i*8)+8), 2);
		}
	}
	
	public static void decode(String infile, String keyfile) throws IOException{
		Map<String, Character> dcodes = new HashMap<String, Character>(); 
		StringBuilder encoded = new StringBuilder();
		StringBuilder decodekeys = new StringBuilder();
		StringBuilder decoded = new StringBuilder();
		
		byte[] data = Files.readAllBytes(Paths.get(infile));
		
		for(int i = 0; i < data.length; i++){
			if(data[i] < 0){
				encoded.append(Integer.toBinaryString(data[i]).substring(24));
			}
			else{
				encoded.append(String.format("%8s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0'));
			}
		}
		
		Scanner sc = new Scanner(new File("codes.txt"));
		decodekeys.append(sc.useDelimiter("\\Z").next());
		sc.close();
		
		while(decodekeys.length() > 1){
			int end_index;
			end_index = decodekeys.indexOf(", ");
			dcodes.put(decodekeys.substring(2, end_index), decodekeys.charAt(0));
			decodekeys.delete(0, end_index + 2);
		}
		
        int index = 0;
		int end_index = 0;
		do{
			end_index++;
            if(dcodes.containsKey(encoded.substring(index
            , end_index))){
                decoded.append(dcodes.get(encoded.substring(index
                , end_index)));
                index
                 = end_index;
			}
		}while(end_index < encoded.length());
		
		Files.write(Paths.get("decoded.txt"), decoded.toString().getBytes());
	}
}