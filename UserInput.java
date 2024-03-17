// Class for Reading User Input using BufferedReader and Exception Handling

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class UserInput {

    public String input(String prompt){
        // read one input and return in String
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(prompt);
        String s = "";
        try{
            s = br.readLine();
        }catch(IOException e){
            System.err.println("Error: "+ e.getMessage());
        }
        return s;
    }
}