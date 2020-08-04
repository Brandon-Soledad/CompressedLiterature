package app;
import java.io.*;

public class Main {

	public static void main(String[] args) throws IOException{
		
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader("WarAndPeace.txt"));
		File original = new File("WarAndPeace.txt");
		long start_time = System.nanoTime();
		
		while(br.ready()){
			String line = br.readLine();
			sb.append(line+"\n");
		}
		
		CodingTree tree = new CodingTree(sb.toString());

		File codes = new File("codes.txt");
		if (!codes.exists()) {
			codes.createNewFile();
		}
		FileWriter fw = new FileWriter(codes.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (int i = 0; i < tree.codes.size(); i++) {
			bw.write(tree.charList.get(i).ch);
			bw.write("=" + tree.codes.get(tree.charList.get(i).ch) + ", ");
		}
		//testCodingTree(sb);
		
		File file = new File("compressed.txt");
		if(!file.exists()){
			file.createNewFile();
		}

		//write encoded message file
		FileOutputStream fs = new FileOutputStream(file.getAbsoluteFile());
		fs.write(tree.bytes);
		fs.close();
		br.close();
		bw.close();
		
		//print statistics
		long end_time = System.nanoTime();
		double run_time = (double)(end_time - start_time)/1000000000; 
		System.out.println("Runtime: "+run_time+" ms");
		System.out.println("File Compression: "+
		((float) file.length()/(float)original.length())*100+"%");
		
		CodingTree.decode("compressed.txt", "codes.txt");

	}
	
	public static void testCodingTree(StringBuilder message) {
		CodingTree tree = new CodingTree(message.toString());
		
		System.out.println(tree.root.toString());
		System.out.println(tree.root.frequency + " root");
	}
}