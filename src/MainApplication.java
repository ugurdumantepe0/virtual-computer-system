import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainApplication {

	static String bitmap = "11111000000001110000000000000111";
	static int allocateFirstFit(int size){

		String token;
		Pattern pattern = Pattern.compile("["+'0'+"]{"+size+"}");
		Matcher matcher = pattern.matcher(bitmap);

		if (matcher.find()){
			System.out.println(matcher.group(0));
			token = matcher.group(0);
			return bitmap.indexOf(token);
		}else{
			return -1;
		}


	}


	public static void main(String[] args) {
	Assembler assembler = new Assembler();
	OS os = new OS(100,5,5,assembler);
	//os.loadProcess("assemblyInput.asm", assembler);
	//	new ProducerFile( new Semaphore(1),5, new ArrayList<ProcessImage>(),assembler).start();
	os.start();

	//	System.out.println(allocateFirstFit(9) + "");



	}
}
