import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;

public class validator {
    int N;
    boolean[][] cg;
    int lines;
    int other;
    int fails;
    int edgenum;
    int[] num_out_edge;
    int[] num_in_edge;
    double error;   // error := (fails/lines)
    public validator(String path) throws IOException{
        // 2,3,4;2;;;12,2;1;
        String data = readFile(path);
        String[] data_n=data.split("\n");
        this.N = Integer.parseInt(data_n[0]);
        num_out_edge = new int[N];
        num_in_edge = new int[N];
        String[] mat = data_n[1].split(";");
        this.cg = new boolean[N][N];
        for(int i=0;i<N;i++){
            for(String s : mat[i].split(",")){
                if(s.length()>0) {
                    edgenum++;
                    cg[i][Integer.parseInt(s)]=true;
                    num_out_edge[i]++;
                }
            }
        }
    }
    public void validate_r2(String path) throws IOException{
        String data = readFile(path);
        // data = data.replaceAll(" ","");
        HashMap<String,Integer> add_to_num= new HashMap<>();
        String[] data_n = data.split("\n");
        // :-1 & 0,1,2,3
        for(int i=4; i<data_n.length-1;i++){
            String str = data_n[i];
            if(str.contains("->")) continue;
            String[] str_n = str.split(" ");
            // "0x006fb030" [label="obj.__TMC_END" URL="obj.__TMC_END/0x006fb030"];
            // "0x00403bae" [label="sym.f7" URL="sym.f7/0x00403bae"];
            String addr = str_n[0].split("\"")[1];
            // System.out.println(addr);
            String s = str_n[1].split("\"")[1];
            // System.out.println(s.substring(5));
            if(!isInteger(s.substring(5))) continue;
            int num = Integer.parseInt(s.substring(5));
            add_to_num.put(addr,num);
        }

        // "0x0044a00f" -> "0x00403bae" [label="sym.f7" color="green" URL="sym.f7/0x00403bae"];
        other = 0;
        fails = 0;
        for(int i=4; i<data_n.length-1;i++){
            String str = data_n[i];
            if(!str.contains("->")) continue;
            String[] str_n = str.split(" ");
            String from = str_n[0].split("\"")[1];
            String to = str_n[2].split("\"")[1];
            // System.out.println(from+"->"+to);
            if(add_to_num.containsKey(from) && add_to_num.containsKey(to)){
                int from_i = add_to_num.get(from);
                int to_i = add_to_num.get(to);
                System.out.println(from_i + "&&" +to_i);
                if (cg[from_i][to_i]) {
                    cg[from_i][to_i] = false;
                }else{
                    System.out.println("False edge: "+str);
                }
            }
        }


        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                if(cg[i][j]){
                    fails++;
                  //  System.out.println("FAILED TO DETECT: "+i+" -> "+j);
                }
            }
        }
        System.out.println("FAILED TO DETECT: "+fails+" edges out of "+data_n.length+" lines: approax fraction "+((double)fails/(double)edgenum));
        lines = data_n.length;
        error = ((double)fails/(double)data_n.length);

    }
    public void validate(String path) throws IOException{
        String data = readFile(path);
        // strip whitespaces
        other = 0;
        fails = 0;
        data = data.replaceAll(" ","");
        String[] data_n = data.split("\n");
        for(int i=0;i<data_n.length;i++){
            String str = data_n[i];
            if(str.contains("->")){
                String[] nodes = str.split("->");
                if(!nodes[0].startsWith("\"@f") || !nodes[1].startsWith("\"@f")){
                    System.out.println("Other: "+str);
                    other++;
                }else {

                    nodes[0] = nodes[0].substring(3, nodes[0].length() - 1);
                    nodes[1] = nodes[1].substring(3, nodes[1].length() - 1);
                  //  System.out.println(nodes[0]+"-"+nodes[1]);
                    int from = Integer.parseInt(nodes[0]);
                    int to = Integer.parseInt(nodes[1]);
                    if (cg[from][to]) {
                        cg[from][to] = false;
                    }else{
                        System.out.println("False edge: "+str);
                    }
                }
            }
        }

        int[] fail_out = new int[N];
        int[] fail_in = new int[N];
        HashSet<Integer> outward= new HashSet<>();
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                if(cg[i][j]){
                    fail_out[i]++;
                    fail_in[j]++;
                    outward.add(j);
                    fails++;
                  System.out.println("FAILED TO DETECT: "+i+" -> "+j);
                }
            }
        }
        double[] percents= new double[N];

        int num_of_non_complete=0;
        for(int i=0;i<N;i++){
            double percent = 0.0;
            if(fail_out[i]>0) {
                num_of_non_complete++;
            }
            if(fail_in[i]+fail_out[i]>0){
                percent = ((double)(fail_in[i]+fail_out[i]))/((double)(num_out_edge[i]+num_in_edge[i]));
            }
            percents[i]=percent;
            System.out.println(percent);
        }
        System.out.println("out of "+N+" functions, "+num_of_non_complete+" are incomplete outward and "+outward.size()+" inward");
        System.out.println("FAILED TO DETECT: "+fails+" edges out of "+edgenum+" edges: approax fraction "+((double)fails/(double)edgenum));
        lines = data_n.length;
        error = ((double)fails/(double)data_n.length);
    }


    static String readFile(String path)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(".",path));
        return new String(encoded,Charset.defaultCharset());
    }

    public static void main(String[] args) throws IOException{
        if(args.length!=2) {
//            validator vl = new validator("rg300.vld");
//            vl.validate_r2("radare300");

            validator vl = new validator("rg300.vld");
            vl.validate("dotfile");

        }else{
            validator vl = new validator(args[0]);
            vl.validate(args[1]);
            String rez = vl.N+","+vl.fails+","+vl.other+","+vl.error+"\n";
            try {
                Files.write(Paths.get("results"), rez.getBytes(), StandardOpenOption.APPEND);
            }catch (IOException e) {
                // I don't do exception handling :)
            }
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

}
